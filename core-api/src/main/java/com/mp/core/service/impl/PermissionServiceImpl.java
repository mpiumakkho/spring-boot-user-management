package com.mp.core.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mp.core.entity.Permission;
import com.mp.core.repository.PermissionRepository;
import com.mp.core.service.PermissionService;

@Service
public class PermissionServiceImpl implements PermissionService {

    private static final Logger log = LogManager.getLogger(PermissionServiceImpl.class);
    
    // Pattern for valid permission format: resource:action
    private static final Pattern PERMISSION_PATTERN = Pattern.compile("^[a-zA-Z_]+:[a-zA-Z_]+$");
    
    private final PermissionRepository permRepo;

    public PermissionServiceImpl(PermissionRepository permRepo) {
        this.permRepo = permRepo;
    }

    @Override
    @Transactional
    public Permission createPermission(Permission permission) {
        // Basic validation
        if (permission.getName() == null || permission.getName().trim().isEmpty()) {
            log.warn("Attempt to create permission with empty name");
            throw new IllegalArgumentException("Permission name is required");
        }

        // Format validation
        String permName = permission.getName().toLowerCase();
        if (!PERMISSION_PATTERN.matcher(permName).matches()) {
            log.warn("Invalid permission format: {}", permName);
            throw new IllegalArgumentException(
                "Permission must be in format 'resource:action' using only letters and underscores"
            );
        }

        // Check for duplicates
        if (permRepo.existsByName(permName)) {
            log.warn("Permission already exists: {}", permName);
            throw new IllegalArgumentException("This permission already exists");
        }

        // Parse and set resource/action
        String[] parts = permName.split(":");
        permission.setResource(parts[0]);
        permission.setAction(parts[1]);
        permission.setName(permName); // Store normalized name

        log.info("Creating new permission: {}", permName);
        return permRepo.save(permission);
    }

    @Override
    @Transactional
    public Permission updatePermission(Permission permission) {
        // Find existing permission
        Permission existing = permRepo.findById(permission.getPermissionId())
            .orElseThrow(() -> {
                log.warn("Attempt to update non-existent permission: {}", permission.getPermissionId());
                return new IllegalArgumentException("Permission not found");
            });

        String newName = permission.getName().toLowerCase();
        
        // If name is changing, validate new name
        if (!existing.getName().equals(newName)) {
            if (!PERMISSION_PATTERN.matcher(newName).matches()) {
                throw new IllegalArgumentException(
                    "Permission must be in format 'resource:action' using only letters and underscores"
                );
            }
            
            if (permRepo.existsByName(newName)) {
                log.warn("Permission name conflict: {}", newName);
                throw new IllegalArgumentException("This permission name is already taken");
            }

            String[] parts = newName.split(":");
            existing.setResource(parts[0]);
            existing.setAction(parts[1]);
            existing.setName(newName);
        }

        // Update other fields
        if (permission.getDescription() != null) {
            existing.setDescription(permission.getDescription());
        }
        
        existing.setUpdatedBy(permission.getUpdatedBy());

        log.info("Updating permission: {}", existing.getPermissionId());
        return permRepo.save(existing);
    }

    @Override
    @Transactional
    public void deletePermission(String id) {
        try {
            if (!permRepo.existsById(id)) {
                log.warn("Attempt to delete non-existent permission: {}", id);
                throw new IllegalArgumentException("Permission not found");
            }

            // Check if this is a system permission
            Optional<Permission> perm = permRepo.findById(id);
            if (perm.isPresent() && isSystemPermission(perm.get())) {
                log.error("Attempt to delete system permission: {}", perm.get().getName());
                throw new IllegalStateException("Cannot delete system permission");
            }

            permRepo.deleteById(id);
            log.info("Permission deleted: {}", id);
            
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
                throw e;
            }
            log.error("Failed to delete permission: " + id, e);
            throw new RuntimeException("Could not delete permission", e);
        }
    }

    private boolean isSystemPermission(Permission permission) {
        String name = permission.getName().toLowerCase();
        return name.startsWith("system:") || 
               name.contains(":admin") || 
               name.equals("user:create") ||
               name.equals("user:read");
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Permission> getPermissionById(String id) {
        return permRepo.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Permission> getPermissionByName(String name) {
        return permRepo.findByName(name.toLowerCase());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getAllPermissions() {
        return permRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getPermissionsByResource(String resource) {
        if (resource == null || resource.trim().isEmpty()) {
            log.warn("Attempt to search permissions with empty resource");
            throw new IllegalArgumentException("Resource name is required");
        }
        return permRepo.findByResource(resource.toLowerCase());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getPermissionsByResourceAndAction(String resource, String action) {
        if (resource == null || resource.trim().isEmpty()) {
            throw new IllegalArgumentException("Resource name is required");
        }
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException("Action name is required");
        }
        
        return permRepo.findByResourceAndAction(
            resource.toLowerCase(), 
            action.toLowerCase()
        );
    }
} 