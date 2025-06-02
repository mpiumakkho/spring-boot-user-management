package com.mp.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.mp.web.utils.ViewAttributeUtils;

import jakarta.servlet.http.HttpSession;

/**
 * Main page and dashboard controller.
 * Shows system stats from core API.
 */
@Controller
public class HomeController {

    private static final Logger LOG = LogManager.getLogger(HomeController.class);

    @Value("${core.api.url}")
    private String coreApiUrl;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Main page handler.
     * Shows login page or goes to dashboard.
     *
     * @param session User session
     * @param model View data
     * @return Login page or dashboard redirect
     */
    @GetMapping({"/", "/login"})
    public String home(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) {
            // LOG.info("[LOGIN PAGE] Guest accessed login page");
            ViewAttributeUtils.addCommonAttributes(model, "/login");
            return "auth/login";  // Maintain direct view rendering for UI compatibility
        }
        // LOG.info("[LOGIN PAGE] User {} accessed root and is already logged in", session.getAttribute("username"));
        return "redirect:/dashboard";
    }

    /**
     * Shows dashboard with system stats.
     * Gets user, role and permission counts.
     *
     * @param session User session
     * @param model View data
     * @return Dashboard page or login redirect
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) {
            // LOG.info("[DASHBOARD] Guest tried to access dashboard, redirect to login");
            return "redirect:/";  // Redirect to root instead of /login for consistency
        }
        // LOG.info("[DASHBOARD] User {} accessed dashboard", session.getAttribute("username"));

        try {
            // LOG.info("Loading dashboard data from core-api: {}", coreApiUrl);

            Map<String, Object> stats = new HashMap<>();
            
            try {
                ResponseEntity<List<Map<String, Object>>> usersResponse = restTemplate.exchange(
                    coreApiUrl + "/api/users",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
                );
                stats.put("totalUsers", usersResponse.getBody() != null ? usersResponse.getBody().size() : 0);
            } catch (Exception e) {
                // LOG.warn("Could not fetch users count: {}", e.getMessage());
                stats.put("totalUsers", 0);
            }

            try {
                ResponseEntity<List<Map<String, Object>>> rolesResponse = restTemplate.exchange(
                    coreApiUrl + "/api/roles",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
                );
                stats.put("totalRoles", rolesResponse.getBody() != null ? rolesResponse.getBody().size() : 0);
            } catch (Exception e) {
                // LOG.warn("Could not fetch roles count: {}", e.getMessage());
                stats.put("totalRoles", 0);
            }

            try {
                ResponseEntity<List<Map<String, Object>>> permissionsResponse = restTemplate.exchange(
                    coreApiUrl + "/api/permissions",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
                );
                stats.put("totalPermissions", permissionsResponse.getBody() != null ? permissionsResponse.getBody().size() : 0);
            } catch (Exception e) {
                // LOG.warn("Could not fetch permissions count: {}", e.getMessage());
                stats.put("totalPermissions", 0);
            }

            model.addAttribute("stats", stats);
            model.addAttribute("coreApiUrl", coreApiUrl);
            ViewAttributeUtils.addCommonAttributes(model, "/dashboard");
            
            // LOG.info("Dashboard loaded successfully with stats: {}", stats);

        } catch (Exception e) {
            // LOG.error("Error loading dashboard: {}", e.getMessage(), e);
            model.addAttribute("error", "Could not load dashboard data: " + e.getMessage());
        }

        return "dashboard/index";
    }
} 