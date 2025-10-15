package com.darum.ems.auth.Config;

import com.darum.ems.auth.Implementation.ServiceInterface.JwtService;
import com.darum.ems.auth.Implementation.ServiceInterface.TokenService;
import com.darum.ems.auth.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtservice;

    @Autowired
    private final UserService userService;

	  @Autowired
	  private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(("Authorization"));
        final String jwt;
        final String username;

        if (!org.apache.commons.lang3.StringUtils.startsWith(authHeader, "Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);

		String ipAddress = request.getHeader("X-FORWARDED-FOR");

		if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr(); // Fallback to direct request IP
            } else if (ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim(); // Extract the real client IP
             }

       // Now ipAddress contains the correct client IP
       logger.info("User logged in from IP: " + ipAddress);

       if (jwt != null && !tokenService.isTokenBlacklisted(jwt) && !jwtservice.isTokenExpired(jwt)) {
        username = jwtservice.extractUsername(jwt);

        if (StringUtils.hasLength(username) && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(username);

            if (jwtservice.isTokenValid(jwt, userDetails)) {
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(token);
                SecurityContextHolder.setContext(securityContext);
            }
        }
	}

        filterChain.doFilter(request, response);
    }

}
