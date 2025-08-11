package com.mp.core.security;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mp.core.service.UserSessionService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(TokenFilter.class);

    private static final Set<String> WHITELIST = Set.of(
        "/api/users/login",
        "/api/sessions/validate",
        "/api/sessions/keep-alive",
        "/api/sessions/logout",
        "/actuator/health"
    );

    private final UserSessionService sessionService;

    public TokenFilter(UserSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();

        // Skip non-API paths and whitelisted endpoints
        if (!path.startsWith("/api/") || isWhitelisted(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String tokenHeader = request.getHeader("X-Session-Token");

        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7).trim();
        } else if (tokenHeader != null && !tokenHeader.isBlank()) {
            token = tokenHeader.trim();
        }

        if (token == null || token.isBlank()) {
            unauthorized(response, "Missing token");
            return;
        }

        try {
            if (sessionService.isSessionValid(token)) {
                sessionService.updateActivity(token);
                filterChain.doFilter(request, response);
            } else {
                unauthorized(response, "Invalid or expired token");
            }
        } catch (Exception e) {
            LOG.error("Token validation error: {}", e.getMessage());
            unauthorized(response, "Token validation error");
        }
    }

    private boolean isWhitelisted(String path) {
        if (WHITELIST.contains(path)) {
            return true;
        }
        // Swagger/OpenAPI
        return path.startsWith("/swagger-ui/") || path.startsWith("/v3/api-docs");
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}

