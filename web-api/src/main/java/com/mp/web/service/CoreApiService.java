package com.mp.web.service;

import java.util.List;
import java.util.Map;

/**
 * Service interface for making calls to the Core API.
 * Abstracts all HTTP communication details from controllers.
 */
public interface CoreApiService {
    // User operations
    List<Map<String, Object>> getAllUsers();
    Map<String, Object> getUserById(String userId);
    Map<String, Object> createUser(Map<String, Object> user);
    void updateUser(Map<String, Object> user);
    void deleteUser(String userId);
    void assignRoleToUser(String userId, String roleId);
    void removeRoleFromUser(String userId, String roleId);
    
    // Role operations
    List<Map<String, Object>> getAllRoles();
    Map<String, Object> getRoleById(String roleId);
    Map<String, Object> createRole(Map<String, Object> role);
    void updateRole(Map<String, Object> role);
    void deleteRole(String roleId);
    void assignPermissionToRole(String roleId, String permissionId);
    void removePermissionFromRole(String roleId, String permissionId);
    List<Map<String, Object>> getRolePermissions(String roleId);
    
    // Permission operations
    List<Map<String, Object>> getAllPermissions();
    Map<String, Object> getPermissionById(String permissionId);
    Map<String, Object> createPermission(Map<String, Object> permission);
    void updatePermission(Map<String, Object> permission);
    void deletePermission(String permissionId);
    List<Map<String, Object>> findPermissionsByResource(String resource);
    List<Map<String, Object>> findPermissionsByResourceAndAction(String resource, String action);
} 