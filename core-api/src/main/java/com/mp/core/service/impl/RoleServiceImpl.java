package com.mp.core.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mp.core.entity.Permission;
import com.mp.core.entity.Role;
import com.mp.core.repository.PermissionRepository;
import com.mp.core.repository.RoleRepository;
import com.mp.core.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger LOG = LogManager.getLogger(RoleServiceImpl.class);
    
    private final RoleRepository roleRepo;
    private final PermissionRepository permRepo;

    public RoleServiceImpl(RoleRepository roleRepo, PermissionRepository permRepo) {
        this.roleRepo = roleRepo;
        this.permRepo = permRepo;
    }

    @Override
    @Transactional
    public Role createRole(Role role) {
        String roleName = role.getName();
        
        if (roleName == null || roleName.trim().isEmpty()) {
            LOG.warn("Attempt to create role with empty name");
            throw new IllegalArgumentException("Role name cannot be empty");
        }
        
        if (roleRepo.existsByName(roleName)) {
            LOG.warn("Role name '{}' already exists", roleName);
            throw new IllegalArgumentException("Role with this name already exists");
        }

        role.setName(roleName.toUpperCase());
        
        LOG.info("Creating new role: {}", roleName);
        return roleRepo.save(role);
    }

    @Override
    @Transactional
    public Role updateRole(Role role) {
        Role existing = roleRepo.findById(role.getRoleId())
            .orElseThrow(() -> {
                LOG.warn("Attempt to update non-existent role: {}", role.getRoleId());
                return new IllegalArgumentException("Role not found");
            });
        
        String newName = role.getName();
        
        if (!existing.getName().equals(newName)) {
            if (roleRepo.existsByName(newName)) {
                LOG.warn("Role name '{}' is already taken", newName);
            throw new IllegalArgumentException("Role name already exists");
            }
            existing.setName(newName.toUpperCase());
        }
        
        existing.setDescription(role.getDescription());
        existing.setUpdatedBy(role.getUpdatedBy());

        LOG.info("Updating role: {}", existing.getRoleId());
        return roleRepo.save(existing);
    }

    @Override
    @Transactional
    public void deleteRole(String id) {
        try {
            if (!roleRepo.existsById(id)) {
                LOG.warn("Attempt to delete non-existent role: {}", id);
                throw new IllegalArgumentException("Role not found");
            }
            
            Optional<Role> role = roleRepo.findById(id);
            if (role.isPresent() && isSystemRole(role.get().getName())) {
                LOG.error("Attempt to delete system role: {}", role.get().getName());
                throw new IllegalStateException("Cannot delete system role");
            }
            
            roleRepo.deleteById(id);
            LOG.info("Role deleted successfully: {}", id);
            
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
                throw e;
            }
            LOG.error("Failed to delete role: " + id, e);
            throw new RuntimeException("Could not delete role", e);
        }
    }

    private boolean isSystemRole(String roleName) {
        Set<String> systemRoles = Set.of("ADMIN", "USER", "SYSTEM");
        return systemRoles.contains(roleName.toUpperCase());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> getRoleById(String id) {
        return roleRepo.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> getRoleByName(String name) {
        return roleRepo.findByName(name.toUpperCase());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepo.findAll();
    }

    @Override
    @Transactional
    public void assignPermissionToRole(String roleId, String permissionId) {
        Role role = roleRepo.findById(roleId)
            .orElseThrow(() -> {
                LOG.warn("Role not found: {}", roleId);
                return new IllegalArgumentException("Role not found");
            });

        Permission permission = permRepo.findById(permissionId)
            .orElseThrow(() -> {
                LOG.warn("Permission not found: {}", permissionId);
                return new IllegalArgumentException("Permission not found");
            });

        if (role.getPermissions().contains(permission)) {
            LOG.debug("Permission {} is already assigned to role {}", permissionId, roleId);
            return;
        }

        role.getPermissions().add(permission);
        roleRepo.save(role);
        LOG.info("Permission {} assigned to role {}", permissionId, roleId);
    }

    @Override
    @Transactional
    public void removePermissionFromRole(String roleId, String permissionId) {
        Role role = roleRepo.findById(roleId)
            .orElseThrow(() -> new IllegalArgumentException("Role not found"));
            
        Permission permission = permRepo.findById(permissionId)
            .orElseThrow(() -> new IllegalArgumentException("Permission not found"));

        if (isSystemRole(role.getName()) && isCriticalPermission(permission)) {
            LOG.warn("Attempt to remove critical permission from system role");
            throw new IllegalStateException("Cannot remove critical permission from system role");
        }

        if (role.getPermissions().remove(permission)) {
            roleRepo.save(role);
            LOG.info("Permission {} removed from role {}", permissionId, roleId);
        } else {
            LOG.debug("Permission {} was not assigned to role {}", permissionId, roleId);
        }
    }

    private boolean isCriticalPermission(Permission permission) {
        return permission.getName().toUpperCase().contains("ADMIN") ||
               permission.getName().toUpperCase().contains("SYSTEM");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getRolePermissions(String roleId) {
        return roleRepo.findById(roleId)
            .map(role -> role.getPermissions().stream()
                .sorted((p1, p2) -> p1.getName().compareTo(p2.getName()))
                .collect(Collectors.toList()))
            .orElseThrow(() -> new IllegalArgumentException("Role not found"));
    }
} 