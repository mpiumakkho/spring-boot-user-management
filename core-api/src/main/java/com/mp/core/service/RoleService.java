package com.mp.core.service;

import java.util.List;
import java.util.Optional;

import com.mp.core.entity.Permission;
import com.mp.core.entity.Role;

public interface RoleService {
    Role createRole(Role role);
    Role updateRole(Role role);
    void deleteRole(String id);
    Optional<Role> getRoleById(String id);
    Optional<Role> getRoleByName(String name);
    List<Role> getAllRoles();
    void assignPermissionToRole(String roleId, String permissionId);
    void removePermissionFromRole(String roleId, String permissionId);
    List<Permission> getRolePermissions(String roleId);
} 