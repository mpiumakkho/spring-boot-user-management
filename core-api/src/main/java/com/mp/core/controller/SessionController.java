package com.mp.core.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mp.core.service.UserSessionService;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {
    
    private static final Logger log = LogManager.getLogger(SessionController.class);
    private final UserSessionService sessionService;
    
    public SessionController(UserSessionService sessionService) {
        this.sessionService = sessionService;
    }
    
    @PostMapping("/validate")
    public ResponseEntity<?> validateSession(@RequestBody String request) {
        try {
            JSONObject json = new JSONObject(request);
            String token = json.optString("token");
            
            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Token required");
            }
            
            boolean isValid = sessionService.isSessionValid(token);
            if (isValid) {
                // Update last activity time
                sessionService.updateActivity(token);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(401).body("Session expired");
            }
            
        } catch (Exception e) {
            log.error("Error validating session", e);
            return ResponseEntity.internalServerError().body("Error validating session");
        }
    }
    
    @PostMapping("/keep-alive")
    public ResponseEntity<?> keepAlive(@RequestBody String request) {
        try {
            JSONObject json = new JSONObject(request);
            String token = json.optString("token");
            
            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Token required");
            }
            
            if (sessionService.isSessionValid(token)) {
                sessionService.updateActivity(token);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(401).body("Session expired");
            }
            
        } catch (Exception e) {
            log.error("Error updating session activity", e);
            return ResponseEntity.internalServerError().body("Error updating session activity");
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody String request) {
        try {
            JSONObject json = new JSONObject(request);
            String token = json.optString("token");
            
            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Token required");
            }
            
            sessionService.invalidateSession(token);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            log.error("Error logging out", e);
            return ResponseEntity.internalServerError().body("Error logging out");
        }
    }
} 