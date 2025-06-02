package com.mp.web.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(SessionFilter.class);
    
    // pages that don't need login
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/demo",
        "/demo/",
        "/demo/login",
        "/demo/auth/login",
        "/error"
    );
    
    // files that don't need login
    private static final List<String> PUBLIC_RESOURCE_PATHS = Arrays.asList(
        "/demo/resources/",
        "/demo/static/",
        "/demo/css/",
        "/demo/js/",
        "/demo/images/",
        "/demo/fonts/",
        "/demo/favicon.ico"
    );
    
    @Value("${core.api.url}")
    private String coreApiUrl;
    
    private final RestTemplate restTemplate;
    
    public SessionFilter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // skip check for public pages
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // skip check for public files
        if (isPublicResource(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("sessionToken") != null) {
            String token = (String) session.getAttribute("sessionToken");
            
            try {
                // check token with core-api
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                Map<String, String> req = Map.of("token", token);
                HttpEntity<Map<String, String>> entity = new HttpEntity<>(req, headers);
                
                ResponseEntity<String> validateResponse = restTemplate.postForEntity(
                    coreApiUrl + "/api/sessions/validate", 
                    entity,
                    String.class
                );
                
                if (validateResponse.getStatusCode().is2xxSuccessful()) {
                    // update last activity time
                    restTemplate.postForEntity(
                        coreApiUrl + "/api/sessions/keep-alive",
                        entity,
                        String.class
                    );
                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (Exception e) {
                logger.error("error checking session: {}", e.getMessage());
            }
            
            // clear invalid session
            session.invalidate();
        }
        
        // go to login page
        response.sendRedirect("/demo");
    }
    
    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.contains(path);
    }
    
    private boolean isPublicResource(String path) {
        return PUBLIC_RESOURCE_PATHS.stream()
                .anyMatch(prefix -> path.startsWith(prefix));
    }
} 