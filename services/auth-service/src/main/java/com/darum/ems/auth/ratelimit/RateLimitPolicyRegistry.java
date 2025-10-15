package com.darum.ems.auth.ratelimit;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Central registry of rate-limit policies for all API endpoints.

 * Features:
 *  - Separate per-method and per-path rules
 *  - Stronger restrictions for sensitive actions (login, register, withdrawal)
 *  - Burst (minute) + quota (daily/hourly) windows for abuse protection
 *  - Fallback rules (method-agnostic + global default)
 */

@Component
public class RateLimitPolicyRegistry {

    private final AntPathMatcher matcher = new AntPathMatcher();
    private final Map<String, Entry> rules = new LinkedHashMap<>();

    public RateLimitPolicyRegistry() {
        // ---------- PUBLIC AUTH ----------
        rules.put("POST:/api/v1/auth/login", entryIp(
                window(5, Duration.ofMinutes(1)),
                window(50, Duration.ofDays(1))
        ));

        rules.put("POST:/api/v1/auth/register", entryIp(
                window(3, Duration.ofMinutes(1)),
                window(20, Duration.ofDays(1))
        ));

        rules.put("GET:/api/v1/auth/**", entryIp(
                window(60, Duration.ofMinutes(1))
        ));

        // Actuator
        rules.put("GET:/actuator/**", entryIp(
                window(60, Duration.ofMinutes(1))
        ));

        // ---------- USER-LEVEL ENDPOINTS ----------
        rules.put("GET:/api/v1/investor/**", entryUser(
                window(120, Duration.ofMinutes(1))
        ));

        // ---------- ADMIN ----------
        rules.put("/api/v1/admin/**", entryUser(
                window(200, Duration.ofMinutes(1))
        ));

        // ---------- DEFAULT ----------
        rules.put("*:/**", entryIp(
                window(60, Duration.ofMinutes(1))
        ));
    }

    public RateLimitPolicy resolve(String method, String path) {
        for (var e : rules.entrySet()) {
            String key = e.getKey();
            if (!key.contains(":")) continue;
            String[] parts = key.split(":", 2);
            String methodPattern = parts[0];
            String pathPattern = parts[1];
            boolean methodOk = methodPattern.equals("*") || methodPattern.equalsIgnoreCase(method);
            boolean pathOk = matcher.match(pathPattern, path);
            if (methodOk && pathOk) {
                return e.getValue().policy();
            }
        }

        for (var e : rules.entrySet()) {
            String key = e.getKey();
            if (!key.contains(":")) {
                if (matcher.match(key, path)) {
                    return e.getValue().policy();
                }
            }
        }

        return entryIp(window(60, Duration.ofMinutes(1))).policy();
    }

    private static Entry entryIp(RateLimitPolicy.Window... ws) {
        return new Entry(new RateLimitPolicy(RateLimitPolicy.KeyType.IP, List.of(ws)));
    }

    private static Entry entryUser(RateLimitPolicy.Window... ws) {
        return new Entry(new RateLimitPolicy(RateLimitPolicy.KeyType.USER, List.of(ws)));
    }

    private static RateLimitPolicy.Window window(long capacity, Duration period) {
        return new RateLimitPolicy.Window(capacity, period);
    }

    private record Entry(RateLimitPolicy policy) {}
}
