package com.mp.web.mapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.mp.web.dto.PermissionDto;
import com.mp.web.dto.RoleDto;
import com.mp.web.dto.UserDto;

// Utility class to map between DTOs and Maps
public class DtoMapper {
    
    // User mapping methods
    public static UserDto toUserDto(Map<String, Object> map) {
        if (map == null) return null;
        
        UserDto dto = new UserDto();
        dto.setUserId((String) map.get("userId"));
        dto.setUsername((String) map.get("username"));
        dto.setEmail((String) map.get("email"));
        dto.setFirstName((String) map.get("firstName"));
        dto.setLastName((String) map.get("lastName"));
        dto.setStatus((String) map.get("status"));
        dto.setPassword((String) map.get("password"));
        dto.setCreatedAt((String) map.get("createdAt"));
        
        Object rolesObj = map.get("roles");
        if (rolesObj instanceof Collection<?>) {
            @SuppressWarnings("unchecked")
            Collection<Map<String, Object>> roles = (Collection<Map<String, Object>>) rolesObj;
            dto.setRoles(roles.stream()
                .map(DtoMapper::toRoleDto)
                .collect(Collectors.toSet()));
        }
        
        return dto;
    }

    public static Map<String, Object> toMap(UserDto dto) {
        if (dto == null) return null;
        
        Map<String, Object> map = new HashMap<>();
        map.put("userId", dto.getUserId());
        map.put("username", dto.getUsername());
        map.put("email", dto.getEmail());
        map.put("firstName", dto.getFirstName());
        map.put("lastName", dto.getLastName());
        map.put("status", dto.getStatus());
        
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            map.put("password", dto.getPassword());
        }
        
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            map.put("roles", dto.getRoles().stream()
                .map(DtoMapper::toMap)
                .collect(Collectors.toSet()));
        }
        
        return map;
    }

    // Role mapping methods
    public static RoleDto toRoleDto(Map<String, Object> map) {
        if (map == null) return null;
        
        RoleDto dto = new RoleDto();
        dto.setRoleId((String) map.get("roleId"));
        dto.setName((String) map.get("name"));
        dto.setDescription((String) map.get("description"));
        dto.setCreatedAt((String) map.get("createdAt"));
        
        Object permissionsObj = map.get("permissions");
        if (permissionsObj instanceof Collection<?>) {
            @SuppressWarnings("unchecked")
            Collection<Map<String, Object>> permissions = (Collection<Map<String, Object>>) permissionsObj;
            dto.setPermissions(permissions.stream()
                .map(DtoMapper::toPermissionDto)
                .collect(Collectors.toSet()));
        }
        
        return dto;
    }

    public static Map<String, Object> toMap(RoleDto dto) {
        if (dto == null) return null;
        
        Map<String, Object> map = new HashMap<>();
        map.put("roleId", dto.getRoleId());
        map.put("name", dto.getName());
        map.put("description", dto.getDescription());
        
        if (dto.getPermissions() != null && !dto.getPermissions().isEmpty()) {
            map.put("permissions", dto.getPermissions().stream()
                .map(DtoMapper::toMap)
                .collect(Collectors.toSet()));
        }
        
        return map;
    }

    // Permission mapping methods
    public static PermissionDto toPermissionDto(Map<String, Object> map) {
        if (map == null) return null;
        
        PermissionDto dto = new PermissionDto();
        dto.setPermissionId((String) map.get("permissionId"));
        dto.setName((String) map.get("name"));
        dto.setResource((String) map.get("resource"));
        dto.setAction((String) map.get("action"));
        dto.setDescription((String) map.get("description"));
        dto.setCreatedAt((String) map.get("createdAt"));
        
        return dto;
    }

    public static Map<String, Object> toMap(PermissionDto dto) {
        if (dto == null) return null;
        
        Map<String, Object> map = new HashMap<>();
        map.put("permissionId", dto.getPermissionId());
        map.put("name", dto.getName());
        map.put("resource", dto.getResource());
        map.put("action", dto.getAction());
        map.put("description", dto.getDescription());
        map.put("createdAt", dto.getCreatedAt());
        
        return map;
    }
} 