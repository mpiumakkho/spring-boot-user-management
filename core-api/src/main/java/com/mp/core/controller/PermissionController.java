package com.mp.core.controller;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mp.core.entity.Permission;
import com.mp.core.service.PermissionService;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final static Logger LOG = LogManager.getLogger(PermissionController.class);

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public ResponseEntity<?> getAllPermissions() {
        try {
            LOG.info("Getting all permissions");
            List<Permission> permissions = permissionService.getAllPermissions();
            // LOG.info("Found {} permissions", permissions.size());
            return ResponseEntity.ok(permissions);
        } catch (Exception e) {
            // LOG.error("Error retrieving permissions: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error retrieving permissions");
        }
    }

    @PostMapping("/find-by-id")
    public ResponseEntity<?> getPermissionById(@RequestBody String request) {
        try {
            JSONObject json = new JSONObject(request);
            String permissionId = json.optString("permissionId");
            
            // LOG.info("Finding permission by ID: {}", permissionId);
            
            if (permissionId == null || permissionId.trim().isEmpty()) {
                // LOG.warn("Permission ID is required but not provided");
                return ResponseEntity.badRequest().body("Permission ID required");
            }

            Optional<Permission> permission = permissionService.getPermissionById(permissionId);
            if (permission.isPresent()) {
                // LOG.info("Permission found: {}", permissionId);
                return ResponseEntity.ok(permission.get());
            } else {
                // LOG.warn("Permission not found: {}", permissionId);
                return ResponseEntity.status(404).body("Permission not found with ID: " + permissionId);
            }
        } catch (Exception e) {
            // LOG.error("Error finding permission: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error finding permission: " + e.getMessage());
        }
    }

    @PostMapping("/find-by-name")
    public ResponseEntity<?> getPermissionByName(@RequestBody String request) {
        try {
            JSONObject json = new JSONObject(request);
            String name = json.optString("name");
            
            // LOG.info("Finding permission by name: {}", name);
            
            if (name == null || name.trim().isEmpty()) {
                // LOG.warn("Permission name is required but not provided");
                return ResponseEntity.badRequest().body("Permission name required");
            }

            Optional<Permission> permission = permissionService.getPermissionByName(name);
            if (permission.isPresent()) {
                // LOG.info("Permission found by name: {}", name);
                return ResponseEntity.ok(permission.get());
            } else {
                // LOG.warn("Permission not found by name: {}", name);
                return ResponseEntity.status(404).body("Permission not found with name: " + name);
            }
        } catch (Exception e) {
            // LOG.error("Error finding permission by name: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error finding permission by name: " + e.getMessage());
        }
    }

    @PostMapping("/find-by-resource")
    public ResponseEntity<?> getPermissionsByResource(@RequestBody String request) {
        try {
            JSONObject json = new JSONObject(request);
            String resource = json.optString("resource");
            
            // LOG.info("Finding permissions by resource: {}", resource);
            
            if (resource == null || resource.trim().isEmpty()) {
                // LOG.warn("Resource is required but not provided");
                return ResponseEntity.badRequest().body("Resource required");
            }

            List<Permission> permissions = permissionService.getPermissionsByResource(resource);
            // LOG.info("Found {} permissions for resource: {}", permissions.size(), resource);
            return ResponseEntity.ok(permissions);
        } catch (Exception e) {
            // LOG.error("Error finding permissions by resource: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error finding permissions by resource: " + e.getMessage());
        }
    }

    @PostMapping("/find-by-resource-and-action")
    public ResponseEntity<?> getPermissionsByResourceAndAction(@RequestBody String request) {
        try {
            JSONObject json = new JSONObject(request);
            String resource = json.optString("resource");
            String action = json.optString("action");
            
            // LOG.info("Finding permissions by resource: {} and action: {}", resource, action);
            
            if (resource == null || resource.trim().isEmpty() || action == null || action.trim().isEmpty()) {
                // LOG.warn("Resource and action are required but not provided");
                return ResponseEntity.badRequest().body("Resource and action required");
            }

            List<Permission> permissions = permissionService.getPermissionsByResourceAndAction(resource, action);
            // LOG.info("Found {} permissions for resource: {} and action: {}", permissions.size(), resource, action);
            return ResponseEntity.ok(permissions);
        } catch (Exception e) {
            // LOG.error("Error finding permissions by resource and action: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error finding permissions by resource and action: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPermission(@RequestBody Permission permission) {
        LOG.info("Creating new permission: {}", permission.getName());
        
        if (permission.getName() == null || permission.getName().trim().isEmpty()) {
            // LOG.warn("Permission name is required but not provided");
            return ResponseEntity.badRequest().body("Permission name required");
        }
        if (permission.getResource() == null || permission.getResource().trim().isEmpty()) {
            // LOG.warn("Permission resource is required but not provided");
            return ResponseEntity.badRequest().body("Permission resource required");
        }
        if (permission.getAction() == null || permission.getAction().trim().isEmpty()) {
            // LOG.warn("Permission action is required but not provided");
            return ResponseEntity.badRequest().body("Permission action required");
        }

        try {
            Permission createdPermission = permissionService.createPermission(permission);
            // LOG.info("Permission created successfully: {} with ID: {}", permission.getName(), createdPermission.getPermissionId());
            return ResponseEntity.ok(createdPermission);
        } catch (IllegalArgumentException e) {
            // LOG.warn("Permission creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Permission creation failed: " + e.getMessage());
        } catch (Exception e) {
            // LOG.error("Error creating permission: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error creating permission: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updatePermission(@RequestBody Permission permission) {
        LOG.info("Updating permission: {}", permission.getPermissionId());
        
        if (permission.getPermissionId() == null || permission.getPermissionId().trim().isEmpty()) {
            // LOG.warn("Permission ID is required but not provided");
            return ResponseEntity.badRequest().body("Permission ID required");
        }

        try {
            Permission updatedPermission = permissionService.updatePermission(permission);
            // LOG.info("Permission updated successfully: {}", permission.getPermissionId());
            return ResponseEntity.ok(updatedPermission);
        } catch (RuntimeException e) {
            // LOG.warn("Permission update failed: {}", e.getMessage());
            if (e.getMessage().contains("Permission not found")) {
                return ResponseEntity.status(404).body("Permission not found with ID: " + permission.getPermissionId());
            } else {
                return ResponseEntity.badRequest().body("Permission update failed: " + e.getMessage());
            }
        } catch (Exception e) {
            // LOG.error("Error updating permission: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error updating permission: " + e.getMessage());
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deletePermission(@RequestBody String request) {
        try {
            JSONObject json = new JSONObject(request);
            String permissionId = json.optString("permissionId");
            
            // LOG.info("Deleting permission: {}", permissionId);
            
            if (permissionId == null || permissionId.trim().isEmpty()) {
                // LOG.warn("Permission ID is required but not provided");
                return ResponseEntity.badRequest().body("Permission ID required");
            }

            // check permission before delete
            Optional<Permission> permission = permissionService.getPermissionById(permissionId);
            if (!permission.isPresent()) {
                // LOG.warn("Permission not found for deletion: {}", permissionId);
                return ResponseEntity.status(404).body("Permission not found with ID: " + permissionId);
            }
            
            permissionService.deletePermission(permissionId);
            // LOG.info("Permission deleted successfully: {}", permissionId);
            return ResponseEntity.ok("Permission deleted successfully with ID: " + permissionId);
        } catch (Exception e) {
            // LOG.error("Error deleting permission: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error deleting permission: " + e.getMessage());
        }
    }
} 