package com.mp.web.service;

import com.mp.web.exception.CoreApiClientException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Service layer for Role operations via core-api
 * Handles REST calls and translates exceptions to user-friendly messages
 */
@Service
public class RoleWebService {
    
    private static final Logger LOG = LogManager.getLogger(RoleWebService.class);
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${core.api.url}")
    private String coreApiUrl;

    /**
     * Get all roles from core-api
     */
    public List<Map<String, Object>> getAllRoles() {
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(
                coreApiUrl + "/api/roles",
                List.class
            );
            return response.getBody();
            
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            LOG.error("Failed to get roles: {}", ex.getMessage());
            throw new CoreApiClientException(
                "ไม่สามารถดึงข้อมูล roles ได้",
                ex.getStatusCode(),
                "/roles",
                ex
            );
        }
    }

    /**
     * Get role by ID
     */
    public Map<String, Object> getRoleById(String roleId) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                coreApiUrl + "/api/roles/" + roleId,
                Map.class
            );
            return response.getBody();
            
        } catch (HttpClientErrorException.NotFound ex) {
            throw new CoreApiClientException(
                "ไม่พบ role ที่ต้องการ",
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                "/roles"
            );
            
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new CoreApiClientException(
                "ไม่สามารถดึงข้อมูล role ได้",
                ex.getStatusCode(),
                "/roles",
                ex
            );
        }
    }

    /**
     * Create new role via core-api
     */
    public Map<String, Object> createRole(Map<String, Object> roleForm) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(roleForm, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                coreApiUrl + "/api/roles",
                request,
                Map.class
            );
            
            LOG.info("Role created successfully");
            return response.getBody();
            
        } catch (HttpClientErrorException.BadRequest ex) {
            LOG.warn("Validation error creating role: {}", ex.getResponseBodyAsString());
            throw new CoreApiClientException(
                "ข้อมูลไม่ถูกต้อง กรุณาตรวจสอบอีกครั้ง",
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "/roles/create"
            );
            
        } catch (HttpClientErrorException.Conflict ex) {
            LOG.warn("Duplicate role: {}", ex.getResponseBodyAsString());
            throw new CoreApiClientException(
                "ชื่อ role นี้มีอยู่แล้ว",
                HttpStatus.CONFLICT,
                "DUPLICATE_RESOURCE",
                "/roles/create"
            );
            
        } catch (HttpClientErrorException.Forbidden ex) {
            throw new CoreApiClientException(
                "คุณไม่มีสิทธิ์สร้าง role",
                HttpStatus.FORBIDDEN,
                "INSUFFICIENT_PERMISSION",
                "/roles"
            );
            
        } catch (HttpClientErrorException ex) {
            LOG.error("Client error creating role: {}", ex.getMessage());
            throw new CoreApiClientException(
                "ไม่สามารถสร้าง role ได้ กรุณาลองใหม่อีกครั้ง",
                ex.getStatusCode(),
                "/roles/create",
                ex
            );
            
        } catch (HttpServerErrorException ex) {
            LOG.error("Server error creating role: {}", ex.getMessage());
            throw new CoreApiClientException(
                "เกิดข้อผิดพลาดจากระบบ กรุณาลองใหม่อีกครั้ง",
                ex.getStatusCode(),
                "/roles/create",
                ex
            );
        }
    }

    /**
     * Update existing role
     */
    public Map<String, Object> updateRole(String roleId, Map<String, Object> roleForm) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(roleForm, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                coreApiUrl + "/api/roles/" + roleId,
                HttpMethod.PUT,
                request,
                Map.class
            );
            
            LOG.info("Role {} updated successfully", roleId);
            return response.getBody();
            
        } catch (HttpClientErrorException.NotFound ex) {
            throw new CoreApiClientException(
                "ไม่พบ role ที่ต้องการแก้ไข",
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                "/roles"
            );
            
        } catch (HttpClientErrorException.Conflict ex) {
            throw new CoreApiClientException(
                "ชื่อ role นี้มีคนใช้แล้ว",
                HttpStatus.CONFLICT,
                "DUPLICATE_RESOURCE",
                "/roles/" + roleId + "/edit"
            );
            
        } catch (HttpClientErrorException.Forbidden ex) {
            throw new CoreApiClientException(
                "คุณไม่มีสิทธิ์แก้ไข role นี้",
                HttpStatus.FORBIDDEN,
                "INSUFFICIENT_PERMISSION",
                "/roles"
            );
            
        } catch (HttpClientErrorException ex) {
            LOG.error("Client error updating role {}: {}", roleId, ex.getMessage());
            throw new CoreApiClientException(
                "ไม่สามารถแก้ไขข้อมูลได้ กรุณาตรวจสอบอีกครั้ง",
                ex.getStatusCode(),
                "/roles/" + roleId + "/edit",
                ex
            );
            
        } catch (HttpServerErrorException ex) {
            LOG.error("Server error updating role {}: {}", roleId, ex.getMessage());
            throw new CoreApiClientException(
                "เกิดข้อผิดพลาดจากระบบ กรุณาลองใหม่อีกครั้ง",
                ex.getStatusCode(),
                "/roles/" + roleId + "/edit",
                ex
            );
        }
    }

    /**
     * Delete role by ID
     */
    public void deleteRole(String roleId) {
        try {
            restTemplate.delete(coreApiUrl + "/api/roles/" + roleId);
            LOG.info("Role {} deleted successfully", roleId);
            
        } catch (HttpClientErrorException.NotFound ex) {
            throw new CoreApiClientException(
                "ไม่พบ role ที่ต้องการลบ",
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                "/roles"
            );
            
        } catch (HttpClientErrorException.Conflict ex) {
            throw new CoreApiClientException(
                "ไม่สามารถลบ role นี้ได้ เนื่องจากมีข้อมูลที่เกี่ยวข้อง",
                HttpStatus.CONFLICT,
                "BUSINESS_VALIDATION_ERROR",
                "/roles"
            );
            
        } catch (HttpClientErrorException.Forbidden ex) {
            throw new CoreApiClientException(
                "คุณไม่มีสิทธิ์ลบ role นี้",
                HttpStatus.FORBIDDEN,
                "INSUFFICIENT_PERMISSION",
                "/roles"
            );
            
        } catch (HttpClientErrorException ex) {
            LOG.error("Client error deleting role {}: {}", roleId, ex.getMessage());
            throw new CoreApiClientException(
                "ไม่สามารถลบ role ได้",
                ex.getStatusCode(),
                "/roles",
                ex
            );
            
        } catch (HttpServerErrorException ex) {
            LOG.error("Server error deleting role {}: {}", roleId, ex.getMessage());
            throw new CoreApiClientException(
                "เกิดข้อผิดพลาดจากระบบ กรุณาลองใหม่อีกครั้ง",
                ex.getStatusCode(),
                "/roles",
                ex
            );
        }
    }

    /**
     * Get permissions for a role
     */
    public List<Map<String, Object>> getRolePermissions(String roleId) {
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(
                coreApiUrl + "/api/roles/" + roleId + "/permissions",
                List.class
            );
            return response.getBody();
            
        } catch (HttpClientErrorException.NotFound ex) {
            throw new CoreApiClientException(
                "ไม่พบ role ที่ต้องการ",
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                "/roles"
            );
            
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new CoreApiClientException(
                "ไม่สามารถดึงข้อมูล permissions ได้",
                ex.getStatusCode(),
                "/roles/" + roleId,
                ex
            );
        }
    }

    /**
     * Assign permission to role
     */
    public void assignPermission(String roleId, String permissionId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> request = Map.of("permissionId", permissionId);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
            
            restTemplate.postForEntity(
                coreApiUrl + "/api/roles/" + roleId + "/permissions",
                entity,
                Void.class
            );
            
            LOG.info("Permission {} assigned to role {}", permissionId, roleId);
            
        } catch (HttpClientErrorException.NotFound ex) {
            throw new CoreApiClientException(
                "ไม่พบ role หรือ permission ที่เลือก",
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                "/roles/" + roleId
            );
            
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new CoreApiClientException(
                "ไม่สามารถกำหนด permission ได้",
                ex.getStatusCode(),
                "/roles/" + roleId,
                ex
            );
        }
    }

    /**
     * Remove permission from role
     */
    public void removePermission(String roleId, String permissionId) {
        try {
            restTemplate.delete(
                coreApiUrl + "/api/roles/" + roleId + "/permissions/" + permissionId
            );
            
            LOG.info("Permission {} removed from role {}", permissionId, roleId);
            
        } catch (HttpClientErrorException.NotFound ex) {
            throw new CoreApiClientException(
                "ไม่พบ role หรือ permission ที่เลือก",
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                "/roles/" + roleId
            );
            
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new CoreApiClientException(
                "ไม่สามารถลบ permission ได้",
                ex.getStatusCode(),
                "/roles/" + roleId,
                ex
            );
        }
    }
}
