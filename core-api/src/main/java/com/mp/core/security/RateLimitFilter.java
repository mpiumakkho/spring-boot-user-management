package com.mp.core.security;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Simple in-memory rate limiter for sensitive endpoints (login, session).
 * Limits requests per IP address within a sliding time window.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Value("${app.security.rate-limit.max-requests:10}")
    private int maxRequests;

    @Value("${app.security.rate-limit.window-seconds:60}")
    private int windowSeconds;

    private final ConcurrentHashMap<String, ConcurrentLinkedDeque<Long>> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String clientIp = getClientIp(request);
        long now = System.currentTimeMillis();
        long windowStart = now - (windowSeconds * 1000L);

        ConcurrentLinkedDeque<Long> timestamps = requestCounts.computeIfAbsent(clientIp, k -> new ConcurrentLinkedDeque<>());

        // Remove expired entries
        while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
            timestamps.pollFirst();
        }

        if (timestamps.size() >= maxRequests) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too many requests. Please try again later.\"}");
            return;
        }

        timestamps.addLast(now);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        // Only rate-limit login and session endpoints
        return !path.equals("/api/users/login") &&
               !path.equals("/api/users/login-encrypt") &&
               !path.startsWith("/api/sessions/");
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
