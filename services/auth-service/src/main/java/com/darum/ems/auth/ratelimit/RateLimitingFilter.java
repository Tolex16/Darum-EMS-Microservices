package com.darum.ems.auth.ratelimit;

import com.darum.ems.auth.Implementation.ServiceInterface.JwtService;
import com.darum.ems.auth.Implementation.ServiceInterface.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Production-grade rate limiting filter.
 * - In-memory fixed-window counters (ConcurrentHashMap).
 * - Per (method, pathPattern, keyType) based on RateLimitPolicyRegistry.
 * - Adds Retry-After, X-RateLimit-* headers.
 * - Periodic, lightweight eviction of stale buckets.
 */
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final String RATE_LIMIT_PREFIX = "rl:v1:";
    private static final long EVICT_AFTER_IDLE_MS = Duration.ofMinutes(20).toMillis();
    private static final int EVICT_EVERY_N_REQUESTS = 500; // cheap sampling to keep map tidy

    private final RateLimitPolicyRegistry policyRegistry;
    private final JwtService jwtService;
    private final TokenService tokenService;

    // Map<bucketKey, RequestBucket>
    private final Map<String, RequestBucket> counters = new ConcurrentHashMap<>();
    private int requestCountSinceEvict = 0;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String path = normalizePath(request.getRequestURI());
        String method = request.getMethod();
        RateLimitPolicy policy = policyRegistry.resolve(method, path);

        if (policy == null || policy.windows().isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        String subjectKey = (policy.keyType() == RateLimitPolicy.KeyType.USER)
                ? resolveUserKeyOrFallbackToIp(request)
                : "ip:" + extractClientIp(request);

        String bucketKey = RATE_LIMIT_PREFIX + method + ":" + path + ":" + subjectKey;

        AllowResult result = allowRequest(bucketKey, policy);
        setHeaders(response, result);

        if (!result.allowed) {
            response.setStatus(429);
            if (result.retryAfterSeconds > 0) {
                response.setHeader("Retry-After", String.valueOf(result.retryAfterSeconds));
            }
            response.getWriter().write("Too many requests - please try again later.");
            return;
        }

        // Low-cost periodic eviction to avoid unbounded memory
        maybeEvictStale();

        chain.doFilter(request, response);
    }

    private static final class AllowResult {
        boolean allowed;
        long retryAfterSeconds;
        // For headers (we expose the most-restrictive window)
        long limit = -1;
        long remaining = -1;
        long windowSeconds = -1;
    }

    private AllowResult allowRequest(String bucketKey, RateLimitPolicy policy) {
        long now = System.currentTimeMillis();
        RequestBucket bucket = counters.computeIfAbsent(bucketKey, k -> new RequestBucket());

        AllowResult result = new AllowResult();
        result.allowed = true; // optimistic

        synchronized (bucket) {
            bucket.lastSeen = now;

            // Track “most restrictive” window for headers
            long minRemaining = Long.MAX_VALUE;
            long chosenLimit = -1;
            long chosenWindowSec = -1;
            long maxRetryAfter = 0;

for (var w : policy.windows()) {
    long capacity = w.capacity();
    long periodMs = w.period().toMillis();

    BandwidthKey bwKey = new BandwidthKey(capacity, periodMs);
    Counter c = bucket.counters.computeIfAbsent(bwKey, k -> new Counter(0, now));

	// reset if window passed
    if (now - c.windowStart >= periodMs) {
        c.count = 0;
        c.windowStart = now;
    }
    // if already at capacity => block
    if (c.count >= capacity) {
        result.allowed = false;
        long remainingMs = periodMs - (now - c.windowStart);
        long retryAfter = Math.max(1, (long) Math.ceil(remainingMs / 1000.0));
        maxRetryAfter = Math.max(maxRetryAfter, retryAfter);
    }

    long remaining = Math.max(0, capacity - c.count);
    // capture the tightest remaining window for headers
     if (remaining < minRemaining) {
        minRemaining = remaining;
        chosenLimit = capacity;
        chosenWindowSec = Math.max(1, periodMs / 1000);
    }
}


            // If we’re allowed, increment all counters atomically
            if (result.allowed) {
                for (var entry : bucket.counters.entrySet()) {
                    Counter c = entry.getValue();
                    // ensure window still valid (another thread might have updated)
                    if (now - c.windowStart >= entry.getKey().periodMs) {
                        c.count = 0;
                        c.windowStart = now;
                    }
                    c.count++;
                }
            } else {
                result.retryAfterSeconds = maxRetryAfter;
            }

            result.limit = chosenLimit;
            result.remaining = Math.max(0, minRemaining - (result.allowed ? 0 : 0)); // already accounted
            result.windowSeconds = chosenWindowSec;
        }

        return result;
    }

    private void setHeaders(HttpServletResponse response, AllowResult r) {
        if (r.limit > -1) {
            response.setHeader("X-RateLimit-Limit", String.valueOf(r.limit));
        }
        if (r.remaining > -1) {
            response.setHeader("X-RateLimit-Remaining", String.valueOf(r.remaining));
        }
        if (r.windowSeconds > -1) {
            response.setHeader("X-RateLimit-Window", String.valueOf(r.windowSeconds));
        }
    }

    private void maybeEvictStale() {
        // sample-based eviction to keep overhead tiny
        if (++requestCountSinceEvict % EVICT_EVERY_N_REQUESTS != 0) return;

        long cutoff = System.currentTimeMillis() - EVICT_AFTER_IDLE_MS;
        counters.entrySet().removeIf(e -> {
            RequestBucket b = e.getValue();
            return b.lastSeen < cutoff;
        });
    }

    private String resolveUserKeyOrFallbackToIp(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.startsWith(authHeader, "Bearer ")) {
            String jwt = authHeader.substring(7);
            try {
                if (tokenService.isTokenBlacklisted(jwt) || jwtService.isTokenExpired(jwt)) {
                    return "ip:" + extractClientIp(request);
                }
                String username = jwtService.extractUsername(jwt);
                if (StringUtils.isNotBlank(username)) return "user:" + username;
            } catch (Exception ignored) {}
        }
        return "ip:" + extractClientIp(request);
    }

    private String extractClientIp(HttpServletRequest request) {
        // Prefer common proxy headers
        String ip = headerFirstNonBlank(
                request,
                "CF-Connecting-IP",
                "X-Forwarded-For",
                "X-Real-IP"
        );
        if (StringUtils.isBlank(ip)) ip = request.getRemoteAddr();
        // X-Forwarded-For may contain a list
        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();
        return ip;
    }

    private String headerFirstNonBlank(HttpServletRequest req, String... names) {
        for (String n : names) {
            String v = req.getHeader(n);
            if (StringUtils.isNotBlank(v)) return v;
        }
        return null;
    }

    private String normalizePath(String path) {
        String p = path.replaceAll("/{2,}", "/");
        if (p.length() > 1 && p.endsWith("/")) p = p.substring(0, p.length() - 1);
        return p;
    }

    private static final class RequestBucket {
        final Map<BandwidthKey, Counter> counters = new ConcurrentHashMap<>();
        long lastSeen;
    }

    private static final class Counter {
        long count;
        long windowStart;

        Counter(long count, long windowStart) {
            this.count = count;
            this.windowStart = windowStart;
        }
    }

    // Key object derived from Bandwidth: {capacity, periodMs}
    private static final class BandwidthKey {
        final long capacity;
        final long periodMs;

        BandwidthKey(long capacity, long periodMs) {
            this.capacity = capacity;
            this.periodMs = periodMs;
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BandwidthKey)) return false;
            BandwidthKey that = (BandwidthKey) o;
            return capacity == that.capacity && periodMs == that.periodMs;
        }

        @Override public int hashCode() {
            return Objects.hash(capacity, periodMs);
        }
    }
}
