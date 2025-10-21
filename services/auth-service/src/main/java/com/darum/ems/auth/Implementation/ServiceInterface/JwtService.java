package com.darum.ems.auth.Implementation.ServiceInterface;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface JwtService {
    String extractUsername(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

    Date extractExpiration(String token);

    Boolean isTokenExpired(String token);

    String generateToken(UserDetails userDetails);

    Long getUserId();

    String extractRole(String token);

    String getCurrentUserRole();
}
