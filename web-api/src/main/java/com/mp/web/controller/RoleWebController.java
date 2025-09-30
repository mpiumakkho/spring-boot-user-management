package com.mp.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mp.web.dto.PermissionDto;
import com.mp.web.dto.RoleDto;
import com.mp.web.exception.FormSubmissionException;
import com.mp.web.exception.WebApiException;
import com.mp.web.mapper.DtoMapper;
import com.mp.web.service.CoreApiService;

/**
 * Manages all role-related web pages and forms.
 * Lets staff create, view, update and delete system roles.
 * Also handles role-permission assignments.
 */
@Controller
@RequestMapping("/roles")
public class RoleWebController {

    private static final Logger LOG = LogManager.getLogger(RoleWebController.class);
    private static final String CURRENT_PATH = "/roles";

    private final CoreApiService coreApiService;

    public RoleWebController(CoreApiService coreApiService) {
        this.coreApiService = coreApiService;
    }

    @GetMapping
    public String listRoles(Model model) {
        try {
            // LOG.info("Fetching roles from core-api");
            List<RoleDto> roles = coreApiService.getAllRoles().stream()
                .map(DtoMapper::toRoleDto)
                .collect(Collectors.toList());
            model.addAttribute("roles", roles);
            model.addAttribute("currentPath", CURRENT_PATH);
            // LOG.info("Fetched {} roles", roles != null ? roles.size() : 0);
            return "roles/list";
        } catch (Exception e) {
            throw new WebApiException("Could not load roles: " + e.getMessage(), CURRENT_PATH, "roles/list", e);
        }
    }

    @GetMapping("/create")
    public String createRoleForm(Model model) {
        model.addAttribute("currentPath", CURRENT_PATH);
        return "roles/create";
    }

    @PostMapping("/create")
    public String createRole(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {
        
        try {
            // LOG.info("Creating role: {}", name);
            RoleDto role = new RoleDto();
            role.setName(name);
            role.setDescription(description);

            coreApiService.createRole(DtoMapper.toMap(role));
            // LOG.info("Role created successfully: {}", name);
            redirectAttributes.addFlashAttribute("success", "Role created successfully!");
            return "redirect:/roles";
        } catch (Exception e) {
            throw new FormSubmissionException("Could not create role: " + e.getMessage(), "/roles", e);
        }
    }

    @PostMapping("/view")
    public String viewRole(@RequestParam String roleId, Model model) {
        try {
            // LOG.info("Fetching role details for ID: {}", roleId);
            RoleDto role = DtoMapper.toRoleDto(coreApiService.getRoleById(roleId));
            model.addAttribute("role", role);
            // LOG.info("Role details fetched successfully for ID: {}", roleId);
            return "roles/detail";
        } catch (Exception e) {
            throw new WebApiException("Could not load role details: " + e.getMessage(), CURRENT_PATH, "roles/detail", e);
        }
    }

    @PostMapping("/edit-form")
    public String editRoleForm(@RequestParam String roleId, Model model) {
        try {
            // LOG.info("Loading edit form for role ID: {}", roleId);
            RoleDto role = DtoMapper.toRoleDto(coreApiService.getRoleById(roleId));
            model.addAttribute("role", role);
            return "roles/edit";
        } catch (Exception e) {
            throw new WebApiException("Could not load role for editing: " + e.getMessage(), CURRENT_PATH, "roles/edit", e);
        }
    }

    @PostMapping("/update")
    public String updateRole(
            @RequestParam String roleId,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {
        
        try {
            // LOG.info("Updating role: {}", roleId);
            RoleDto role = new RoleDto();
            role.setRoleId(roleId);
            role.setName(name);
            role.setDescription(description);

            coreApiService.updateRole(DtoMapper.toMap(role));
            // LOG.info("Role updated successfully: {}", roleId);
            redirectAttributes.addFlashAttribute("success", "Role updated successfully!");
            return "redirect:/roles";
        } catch (Exception e) {
            throw new FormSubmissionException("Could not update role: " + e.getMessage(), "/roles", e);
        }
    }

    @PostMapping("/delete")
    public String deleteRole(@RequestParam String roleId, RedirectAttributes redirectAttributes) {
        try {
            // LOG.info("Deleting role: {}", roleId);
            coreApiService.deleteRole(roleId);
            // LOG.info("Role deleted successfully: {}", roleId);
            redirectAttributes.addFlashAttribute("success", "Role deleted successfully!");
            return "redirect:/roles";
        } catch (Exception e) {
            throw new FormSubmissionException("Could not delete role: " + e.getMessage(), "/roles", e);
        }
    }

    @PostMapping("/permissions-form")
    public String manageRolePermissions(@RequestParam String roleId, Model model) {
        try {
            // LOG.info("Loading permissions management for role ID: {}", roleId);
            RoleDto role = DtoMapper.toRoleDto(coreApiService.getRoleById(roleId));
            List<PermissionDto> rolePermissions = coreApiService.getRolePermissions(roleId).stream()
                .map(DtoMapper::toPermissionDto)
                .collect(Collectors.toList());
            List<PermissionDto> allPermissions = coreApiService.getAllPermissions().stream()
                .map(DtoMapper::toPermissionDto)
                .collect(Collectors.toList());

            model.addAttribute("role", role);
            model.addAttribute("rolePermissions", rolePermissions);
            model.addAttribute("allPermissions", allPermissions);
            return "roles/permissions";
        } catch (Exception e) {
            throw new WebApiException("Could not load permissions management: " + e.getMessage(), CURRENT_PATH, "roles/permissions", e);
        }
    }

    @PostMapping("/assign-permission")
    public String assignPermissionToRole(
            @RequestParam String roleId,
            @RequestParam String permissionId,
            RedirectAttributes redirectAttributes) {
        
        try {
            // LOG.info("Assigning permission {} to role {}", permissionId, roleId);
            coreApiService.assignPermissionToRole(roleId, permissionId);
            redirectAttributes.addFlashAttribute("success", "Permission assigned successfully!");
            return "redirect:/roles";
        } catch (Exception e) {
            throw new FormSubmissionException("Could not assign permission: " + e.getMessage(), "/roles", e);
        }
    }

    @PostMapping("/remove-permission")
    public String removePermissionFromRole(
            @RequestParam String roleId,
            @RequestParam String permissionId,
            RedirectAttributes redirectAttributes) {
        
        try {
            // LOG.info("Removing permission {} from role {}", permissionId, roleId);
            coreApiService.removePermissionFromRole(roleId, permissionId);
            redirectAttributes.addFlashAttribute("success", "Permission removed successfully!");
            return "redirect:/roles";
        } catch (Exception e) {
            throw new FormSubmissionException("Could not remove permission: " + e.getMessage(), "/roles", e);
        }
    }
} 