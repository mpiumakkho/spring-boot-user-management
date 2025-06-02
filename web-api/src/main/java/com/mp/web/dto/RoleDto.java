package com.mp.web.dto;

import java.util.HashSet;
import java.util.Set;

// Data Transfer Object for Role information
public class RoleDto {
    private String roleId;
    private String name;
    private String description;
    private String createdAt;
    private Set<PermissionDto> permissions;

    public RoleDto() {this.permissions = new HashSet<>();}

    public String getRoleId() {return roleId;}
    public void setRoleId(String roleId) {this.roleId = roleId;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
    public String getCreatedAt() {return createdAt;}
    public void setCreatedAt(String createdAt) {this.createdAt = createdAt;}

    public Set<PermissionDto> getPermissions() {return permissions;}
    public void setPermissions(Set<PermissionDto> permissions) {this.permissions = permissions;}
    public void addPermission(PermissionDto permission) {this.permissions.add(permission);}
    public void removePermission(PermissionDto permission) {this.permissions.remove(permission);}
} 