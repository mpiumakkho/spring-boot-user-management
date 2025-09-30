package com.mp.web.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mp.web.service.CoreApiService;

/**
 * Implementation of CoreApiService that makes HTTP calls to the Core API.
 */
@Service
public class CoreApiServiceImpl implements CoreApiService {

    private final RestTemplate restTemplate;
    private final String coreApiUrl;
    private final HttpHeaders headers;

    public CoreApiServiceImpl(
            RestTemplate restTemplate,
            @Value("${core.api.url}") String coreApiUrl) {
        this.restTemplate = restTemplate;
        this.coreApiUrl = coreApiUrl;
        
        this.headers = new HttpHeaders();
        this.headers.setContentType(MediaType.APPLICATION_JSON);
    }

    // User operations
    @Override
    public List<Map<String, Object>> getAllUsers() {
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            coreApiUrl + "/api/users",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        return response.getBody();
    }

    @Override
    public Map<String, Object> getUserById(String userId) {
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("userId", userId), headers);
        return restTemplate.postForObject(
            coreApiUrl + "/api/users/find-by-id",
            request,
            Map.class
        );
    }

    @Override
    public Map<String, Object> createUser(Map<String, Object> user) {
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(user, headers);
        return restTemplate.postForObject(
            coreApiUrl + "/api/users/create",
            request,
            Map.class
        );
    }

    @Override
    public void updateUser(Map<String, Object> user) {
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(user, headers);
        restTemplate.put(coreApiUrl + "/api/users/update", request);
    }

    @Override
    public void deleteUser(String userId) {
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("userId", userId), headers);
        restTemplate.postForObject(
            coreApiUrl + "/api/users/delete",
            request,
            String.class
        );
    }

    @Override
    public void assignRoleToUser(String userId, String roleId) {
        HttpEntity<Map<String, String>> request = new HttpEntity<>(
            Map.of("userId", userId, "roleId", roleId),
            headers
        );
        restTemplate.postForObject(
            coreApiUrl + "/api/users/assign-role",
            request,
            String.class
        );
    }

    @Override
    public void removeRoleFromUser(String userId, String roleId) {
        HttpEntity<Map<String, String>> request = new HttpEntity<>(
            Map.of("userId", userId, "roleId", roleId),
            headers
        );
        restTemplate.postForObject(
            coreApiUrl + "/api/users/remove-role",
            request,
            String.class
        );
    }

    // Role operations
    @Override
    public List<Map<String, Object>> getAllRoles() {
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            coreApiUrl + "/api/roles",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        return response.getBody();
    }

    @Override
    public Map<String, Object> getRoleById(String roleId) {
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("roleId", roleId), headers);
        return restTemplate.postForObject(
            coreApiUrl + "/api/roles/find-by-id",
            request,
            Map.class
        );
    }

    @Override
    public Map<String, Object> createRole(Map<String, Object> role) {
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(role, headers);
        return restTemplate.postForObject(
            coreApiUrl + "/api/roles/create",
            request,
            Map.class
        );
    }

    @Override
    public void updateRole(Map<String, Object> role) {
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(role, headers);
        restTemplate.put(coreApiUrl + "/api/roles/update", request);
    }

    @Override
    public void deleteRole(String roleId) {
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("roleId", roleId), headers);
        restTemplate.postForObject(
            coreApiUrl + "/api/roles/delete",
            request,
            String.class
        );
    }

    @Override
    public void assignPermissionToRole(String roleId, String permissionId) {
        HttpEntity<Map<String, String>> request = new HttpEntity<>(
            Map.of("roleId", roleId, "permissionId", permissionId),
            headers
        );
        restTemplate.postForObject(
            coreApiUrl + "/api/roles/assign-permission",
            request,
            String.class
        );
    }

    @Override
    public void removePermissionFromRole(String roleId, String permissionId) {
        HttpEntity<Map<String, String>> request = new HttpEntity<>(
            Map.of("roleId", roleId, "permissionId", permissionId),
            headers
        );
        restTemplate.postForObject(
            coreApiUrl + "/api/roles/remove-permission",
            request,
            String.class
        );
    }

    @Override
    public List<Map<String, Object>> getRolePermissions(String roleId) {
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("roleId", roleId), headers);
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            coreApiUrl + "/api/roles/get-permissions",
            HttpMethod.POST,
            request,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        return response.getBody();
    }

    // Permission operations
    @Override
    public List<Map<String, Object>> getAllPermissions() {
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            coreApiUrl + "/api/permissions",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        return response.getBody();
    }

    @Override
    public Map<String, Object> getPermissionById(String permissionId) {
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("permissionId", permissionId), headers);
        return restTemplate.postForObject(
            coreApiUrl + "/api/permissions/find-by-id",
            request,
            Map.class
        );
    }

    @Override
    public Map<String, Object> createPermission(Map<String, Object> permission) {
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(permission, headers);
        return restTemplate.postForObject(
            coreApiUrl + "/api/permissions/create",
            request,
            Map.class
        );
    }

    @Override
    public void updatePermission(Map<String, Object> permission) {
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(permission, headers);
        restTemplate.put(coreApiUrl + "/api/permissions/update", request);
    }

    @Override
    public void deletePermission(String permissionId) {
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("permissionId", permissionId), headers);
        restTemplate.postForObject(
            coreApiUrl + "/api/permissions/delete",
            request,
            String.class
        );
    }

    @Override
    public List<Map<String, Object>> findPermissionsByResource(String resource) {
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("resource", resource), headers);
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            coreApiUrl + "/api/permissions/find-by-resource",
            HttpMethod.POST,
            request,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        return response.getBody();
    }

    @Override
    public List<Map<String, Object>> findPermissionsByResourceAndAction(String resource, String action) {
        HttpEntity<Map<String, String>> request = new HttpEntity<>(
            Map.of("resource", resource, "action", action),
            headers
        );
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            coreApiUrl + "/api/permissions/find-by-resource-and-action",
            HttpMethod.POST,
            request,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        return response.getBody();
    }
} 