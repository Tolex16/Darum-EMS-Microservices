package com.darum.ems.auth.Implementation.ServiceInterface;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class TokenService {

	private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private final Map<String, Instant> tokenMap = new ConcurrentHashMap<>();

    public void addToken(String token, Instant expiryTime) {
        tokenMap.put(token, expiryTime);
		logger.debug("Added token: {}", token);
    }

  public boolean isTokenBlacklisted(String token) {
        Instant expiryTime = tokenMap.get(token);
        if (expiryTime == null) {
            return false;
        }
        // Check if the token has expired
        if (expiryTime.isBefore(Instant.now())) {
            // Optionally remove the token from the map
            tokenMap.remove(token);
            logger.debug("Removed token: {}", token);
            return false;
        }
        return true;
    }

    public Set<String> getTokens() {
        return tokenMap.keySet();
    }

    public Map<String, Instant> getTokenMap() {
        return tokenMap;
    }

    // Scheduled method to clean up expired tokens
    @Scheduled(fixedRate = 60000) // Run every minute
    public void cleanUpExpiredTokens() {
        Instant now = Instant.now();
        Iterator<Map.Entry<String, Instant>> iterator = tokenMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Instant> entry = iterator.next();
            if (entry.getValue().isBefore(now)) {
                iterator.remove();
				logger.debug("Expired token removed: {}", entry.getKey());
            }
        }
    }
}

