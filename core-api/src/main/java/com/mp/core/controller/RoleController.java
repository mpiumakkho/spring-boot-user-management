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
import com.mp.core.entity.Role;
import com.mp.core.service.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final static Logger LOG = LogManager.getLogger(RoleController.class);

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<?> getAllRoles() {
        try {
            LOG.info("Getting all roles");
            List<Role> roles = roleService.getAllRoles();
            LOG.info("Found {} roles", roles.size());
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            LOG.error("Error retrieving roles: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error retrieving roles");
        }
    }

    @PostMapping("/find-by-id")
    public ResponseEntity<?> getRoleById(@RequestBody String request) {
        try {
            JSONObject json = new JSONObject(request);
            String roleId = json.optString("roleId");
            
            LOG.info("Finding role by ID: {}", roleId);
            
            if (roleId == null || roleId.trim().isEmpty()) {
                LOG.warn("Role ID is required but not provided");
                return ResponseEntity.badRequest().body("Role ID required");
            }

            Optional<Role> role = roleService.getRoleById(roleId);
            if (role.isPresent()) {
                LOG.info("Role found: {}", roleId);
                return ResponseEntity.ok(role.get());
            } else {
                LOG.warn("Role not found: {}", roleId);
                return ResponseEntity.status(404).body("Role not found with ID: " + roleId);
            }
        } catch (Exception e) {
            LOG.error("Error finding role: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error finding role: " + e.getMessage());
        }
    }

    @PostMapping("/find-by-name")
    public ResponseEntity<?> getRoleByName(@RequestBody String request) {
        try {
            JSONObject json = new JSONObject(request);
            String name = json.optString("name");
            
            LOG.info("Finding role by name: {}", name);
            
            if (name == null || name.trim().isEmpty()) {
                LOG.warn("Role name is required but not provided");
                return ResponseEntity.badRequest().body("Role name required");
            }

            Optional<Role> role = roleService.getRoleByName(name);
            if (role.isPresent()) {
                LOG.info("Role found by name: {}", name);
                return ResponseEntity.ok(role.get());
            } else {
                LOG.warn("Role not found by name: {}", name);
                return ResponseEntity.status(404).body("Role not found with name: " + name);
            }
        } catch (Exception e) {
            LOG.error("Error finding role by name: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error finding role by name: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRole(@RequestBody Role role) {
        LOG.info("Creating new role: {}", role.getName());
        
        if (role.getName() == null || role.getName().trim().isEmpty()) {
            LOG.warn("Role name is required but not provided");
            return ResponseEntity.badRequest().body("Role name required");
        }

        try {
            Role createdRole = roleService.createRole(role);
            LOG.info("Role created successfully: {} with ID: {}", role.getName(), createdRole.getRoleId());
            return ResponseEntity.ok(createdRole);
        } catch (IllegalArgumentException e) {
            LOG.warn("Role creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Role creation failed: " + e.getMessage());
        } catch (Exception e) {
            LOG.error("Error creating role: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error creating role: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateRole(@RequestBody Role role) {
        LOG.info("Updating role: {}", role.getRoleId());
        
        if (role.getRoleId() == null || role.getRoleId().trim().isEmpty()) {
            LOG.warn("Role ID is required but not provided");
            return ResponseEntity.badRequest().body("Role ID required");
        }

        try {
            Role updatedRole = roleService.updateRole(role);
            LOG.info("Role updated successfully: {}", role.getRoleId());
            return ResponseEntity.ok(updatedRole);
        } catch (RuntimeException e) {
            LOG.warn("Role update failed: {}", e.getMessage());
            if (e.getMessage().contains("Role not found")) {
                return ResponseEntity.status(404).body("Role not found with ID: " + role.getRoleId());
            } else {
                return ResponseEntity.badRequest().body("Role update failed: " + e.getMessage());
            }
        } catch (Exception e) {
            LOG.error("Error updating role: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error updating role: " + e.getMessage());
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteRole(@RequestBody String request) {
        try {
            JSONObject json = new JSONObject(request);
            String roleId = json.optString("roleId");
            
            LOG.info("Deleting role: {}", roleId);
            
            if (roleId == null || roleId.trim().isEmpty()) {
                LOG.warn("Role ID is required but not provided");
                return ResponseEntity.badRequest().body("Role ID required");
            }

            // check role before delete
            Optional<Role> role = roleService.getRoleById(roleId);
            if (!role.isPresent()) {
                LOG.warn("Role not found for deletion: {}", roleId);
                return ResponseEntity.status(404).body("Role not found with ID: " + roleId);
            }
            
            roleService.deleteRole(roleId);
            LOG.info("Role deleted successfully: {}", roleId);
            return ResponseEntity.ok("Role deleted successfully with ID: " + roleId);
        } catch (Exception e) {
            LOG.error("Error deleting role: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error deleting role: " + e.getMessage());
        }
    }

    @PostMapping("/assign-permission")
    public ResponseEntity<?> assignPermissionToRole(@RequestBody String request) {
        String roleId = null;
        String permissionId = null;
        
        try {
            JSONObject json = new JSONObject(request);
            roleId = json.optString("roleId");
            permissionId = json.optString("permissionId");
            
            LOG.info("Assigning permission {} to role {}", permissionId, roleId);
            
            if (roleId == null || roleId.trim().isEmpty() || permissionId == null || permissionId.trim().isEmpty()) {
                LOG.warn("Role ID and Permission ID are required but not provided");
                return ResponseEntity.badRequest().body("Role ID and Permission ID required");
            }

            roleService.assignPermissionToRole(roleId, permissionId);
            LOG.info("Permission {} assigned successfully to role {}", permissionId, roleId);
            return ResponseEntity.ok("Permission assigned successfully to role ID: " + roleId);
        } catch (RuntimeException e) {
            LOG.warn("Permission assignment failed: {}", e.getMessage());
            if (e.getMessage().contains("Role not found")) {
                return ResponseEntity.status(404).body("Role not found with ID: " + roleId);
            } else if (e.getMessage().contains("Permission not found")) {
                return ResponseEntity.status(404).body("Permission not found");
            } else {
                return ResponseEntity.badRequest().body("Permission assignment failed: " + e.getMessage());
            }
        } catch (Exception e) {
            LOG.error("Error assigning permission: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error assigning permission: " + e.getMessage());
        }
    }

    @PostMapping("/remove-permission")
    public ResponseEntity<?> removePermissionFromRole(@RequestBody String request) {
        String roleId = null;
        String permissionId = null;
        
        try {
            JSONObject json = new JSONObject(request);
            roleId = json.optString("roleId");
            permissionId = json.optString("permissionId");
            
            LOG.info("Removing permission {} from role {}", permissionId, roleId);
            
            if (roleId == null || roleId.trim().isEmpty() || permissionId == null || permissionId.trim().isEmpty()) {
                LOG.warn("Role ID and Permission ID are required but not provided");
                return ResponseEntity.badRequest().body("Role ID and Permission ID required");
            }

            roleService.removePermissionFromRole(roleId, permissionId);
            LOG.info("Permission {} removed successfully from role {}", permissionId, roleId);
            return ResponseEntity.ok("Permission removed successfully from role ID: " + roleId);
        } catch (RuntimeException e) {
            LOG.warn("Permission removal failed: {}", e.getMessage());
            if (e.getMessage().contains("Role not found")) {
                return ResponseEntity.status(404).body("Role not found with ID: " + roleId);
            } else if (e.getMessage().contains("Permission not found")) {
                return ResponseEntity.status(404).body("Permission not found");
            } else {
                return ResponseEntity.badRequest().body("Permission removal failed: " + e.getMessage());
            }
        } catch (Exception e) {
            LOG.error("Error removing permission: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error removing permission: " + e.getMessage());
        }
    }

    @PostMapping("/get-permissions")
    public ResponseEntity<?> getRolePermissions(@RequestBody String request) {
        String roleId = null;
        
        try {
            JSONObject json = new JSONObject(request);
            roleId = json.optString("roleId");
            
            LOG.info("Getting permissions for role: {}", roleId);
            
            if (roleId == null || roleId.trim().isEmpty()) {
                LOG.warn("Role ID is required but not provided");
                return ResponseEntity.badRequest().body("Role ID required");
            }

            List<Permission> permissions = roleService.getRolePermissions(roleId);
            LOG.info("Found {} permissions for role: {}", permissions.size(), roleId);
            return ResponseEntity.ok(permissions);
        } catch (RuntimeException e) {
            LOG.warn("Get permissions failed: {}", e.getMessage());
            return ResponseEntity.status(404).body("Role not found with ID: " + roleId);
        } catch (Exception e) {
            LOG.error("Error getting role permissions: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error getting role permissions: " + e.getMessage());
        }
    }
} 