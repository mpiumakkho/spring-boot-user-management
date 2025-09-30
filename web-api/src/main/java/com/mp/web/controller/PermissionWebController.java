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
import com.mp.web.exception.FormSubmissionException;
import com.mp.web.exception.WebApiException;
import com.mp.web.mapper.DtoMapper;
import com.mp.web.service.CoreApiService;

/**
 * Manages all permission-related web pages and forms.
 * Lets staff create, view, update and delete system permissions.
 * Permissions define what actions can be performed on resources.
 */
@Controller
@RequestMapping("/permissions")
public class PermissionWebController {

    private static final Logger LOG = LogManager.getLogger(PermissionWebController.class);
    private static final String CURRENT_PATH = "/permissions";

    private final CoreApiService coreApiService;

    public PermissionWebController(CoreApiService coreApiService) {
        this.coreApiService = coreApiService;
    }

    @GetMapping
    public String listPermissions(Model model) {
        try {
            // LOG.info("Fetching permissions from core-api");
            List<PermissionDto> permissions = coreApiService.getAllPermissions().stream()
                .map(DtoMapper::toPermissionDto)
                .collect(Collectors.toList());
            model.addAttribute("permissions", permissions);
            model.addAttribute("currentPath", CURRENT_PATH);
            // LOG.info("Fetched {} permissions", permissions != null ? permissions.size() : 0);
            return "permissions/list";
        } catch (Exception e) {
            throw new WebApiException("Could not load permissions: " + e.getMessage(), CURRENT_PATH, "permissions/list", e);
        }
    }

    @GetMapping("/create")
    public String createPermissionForm(Model model) {
        model.addAttribute("currentPath", CURRENT_PATH);
        return "permissions/create";
    }

    @PostMapping("/create")
    public String createPermission(
            @RequestParam String name,
            @RequestParam String resource,
            @RequestParam String action,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {
        
        try {
            // LOG.info("Creating permission: {}", name);
            PermissionDto permission = new PermissionDto();
            permission.setName(name);
            permission.setResource(resource);
            permission.setAction(action);
            permission.setDescription(description);

            coreApiService.createPermission(DtoMapper.toMap(permission));
            // LOG.info("Permission created successfully: {}", name);
            redirectAttributes.addFlashAttribute("success", "Permission created successfully!");
            return "redirect:/permissions";
        } catch (Exception e) {
            throw new FormSubmissionException("Could not create permission: " + e.getMessage(), "/permissions", e);
        }
    }

    @PostMapping("/view")
    public String viewPermission(@RequestParam String permissionId, Model model) {
        try {
            // LOG.info("Fetching permission details for ID: {}", permissionId);
            PermissionDto permission = DtoMapper.toPermissionDto(coreApiService.getPermissionById(permissionId));
            model.addAttribute("permission", permission);
            // LOG.info("Permission details fetched successfully for ID: {}", permissionId);
            return "permissions/detail";
        } catch (Exception e) {
            throw new WebApiException("Could not load permission details: " + e.getMessage(), CURRENT_PATH, "permissions/detail", e);
        }
    }

    @PostMapping("/edit-form")
    public String editPermissionForm(@RequestParam String permissionId, Model model) {
        try {
            // LOG.info("Loading edit form for permission ID: {}", permissionId);
            PermissionDto permission = DtoMapper.toPermissionDto(coreApiService.getPermissionById(permissionId));
            model.addAttribute("permission", permission);
            return "permissions/edit";
        } catch (Exception e) {
            throw new WebApiException("Could not load permission for editing: " + e.getMessage(), CURRENT_PATH, "permissions/edit", e);
        }
    }

    @PostMapping("/update")
    public String updatePermission(
            @RequestParam String permissionId,
            @RequestParam String name,
            @RequestParam String resource,
            @RequestParam String action,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {
        
        try {
            // LOG.info("Updating permission: {}", permissionId);
            PermissionDto permission = new PermissionDto();
            permission.setPermissionId(permissionId);
            permission.setName(name);
            permission.setResource(resource);
            permission.setAction(action);
            permission.setDescription(description);

            coreApiService.updatePermission(DtoMapper.toMap(permission));
            // LOG.info("Permission updated successfully: {}", permissionId);
            redirectAttributes.addFlashAttribute("success", "Permission updated successfully!");
            return "redirect:/permissions";
        } catch (Exception e) {
            throw new FormSubmissionException("Could not update permission: " + e.getMessage(), "/permissions", e);
        }
    }

    @PostMapping("/delete")
    public String deletePermission(@RequestParam String permissionId, RedirectAttributes redirectAttributes) {
        try {
            // LOG.info("Deleting permission: {}", permissionId);
            coreApiService.deletePermission(permissionId);
            // LOG.info("Permission deleted successfully: {}", permissionId);
            redirectAttributes.addFlashAttribute("success", "Permission deleted successfully!");
            return "redirect:/permissions";
        } catch (Exception e) {
            throw new FormSubmissionException("Could not delete permission: " + e.getMessage(), "/permissions", e);
        }
    }

    @GetMapping("/search")
    public String searchPermissions(
            @RequestParam(required = false) String resource,
            @RequestParam(required = false) String action,
            Model model) {
        
        try {
            // LOG.info("Searching permissions by resource: {} and action: {}", resource, action);
            List<PermissionDto> searchResults;

            if (resource != null && !resource.trim().isEmpty() && action != null && !action.trim().isEmpty()) {
                searchResults = coreApiService.findPermissionsByResourceAndAction(resource.trim(), action.trim()).stream()
                    .map(DtoMapper::toPermissionDto)
                    .collect(Collectors.toList());
            } else if (resource != null && !resource.trim().isEmpty()) {
                searchResults = coreApiService.findPermissionsByResource(resource.trim()).stream()
                    .map(DtoMapper::toPermissionDto)
                    .collect(Collectors.toList());
            } else {
                searchResults = coreApiService.getAllPermissions().stream()
                    .map(DtoMapper::toPermissionDto)
                    .collect(Collectors.toList());
            }

            model.addAttribute("permissions", searchResults);
            model.addAttribute("searchResource", resource);
            model.addAttribute("searchAction", action);
            // LOG.info("Found {} permissions for search criteria", searchResults != null ? searchResults.size() : 0);
            return "permissions/search";
        } catch (Exception e) {
            throw new WebApiException("Could not search permissions: " + e.getMessage(), CURRENT_PATH, "permissions/search", e);
        }
    }
} 