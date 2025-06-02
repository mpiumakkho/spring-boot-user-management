package com.mp.core.service;

import java.util.List;
import java.util.Optional;

import com.mp.core.entity.Permission;

public interface PermissionService {
    Permission createPermission(Permission permission);
    Permission updatePermission(Permission permission);
    void deletePermission(String id);
    Optional<Permission> getPermissionById(String id);
    Optional<Permission> getPermissionByName(String name);
    List<Permission> getAllPermissions();
    List<Permission> getPermissionsByResource(String resource);
    List<Permission> getPermissionsByResourceAndAction(String resource, String action);
} 