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

import com.mp.web.dto.RoleDto;
import com.mp.web.dto.UserDto;
import com.mp.web.exception.FormSubmissionException;
import com.mp.web.exception.WebApiException;
import com.mp.web.mapper.DtoMapper;
import com.mp.web.service.CoreApiService;

/**
 * Manages all user-related web pages and forms.
 * Lets staff create, view, update and delete system users.
 */
@Controller
@RequestMapping("/users")
public class UserWebController {

    private static final Logger LOG = LogManager.getLogger(UserWebController.class);
    private static final String CURRENT_PATH = "/users";

    private final CoreApiService coreApiService;

    public UserWebController(CoreApiService coreApiService) {
        this.coreApiService = coreApiService;
    }

    @GetMapping
    public String listUsers(Model model) {
        try {
            // LOG.info("Fetching users from core-api");
            List<UserDto> users = coreApiService.getAllUsers().stream()
                .map(DtoMapper::toUserDto)
                .collect(Collectors.toList());
            model.addAttribute("users", users);
            model.addAttribute("currentPath", CURRENT_PATH);
            // LOG.info("Fetched {} users", users != null ? users.size() : 0);
            return "users/list";
        } catch (Exception e) {
            throw new WebApiException("Could not load users: " + e.getMessage(), CURRENT_PATH, "users/list", e);
        }
    }

    @GetMapping("/create")
    public String createUserForm(Model model) {
        return "users/create";
    }

    @PostMapping("/create")
    public String createUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(defaultValue = "inactive") String status,
            RedirectAttributes redirectAttributes) {
        
        try {
            // LOG.info("Creating user: {}", username);
            UserDto user = new UserDto();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setStatus(status);

            coreApiService.createUser(DtoMapper.toMap(user));
            // LOG.info("User created successfully: {}", username);
            redirectAttributes.addFlashAttribute("success", "User created successfully!");
            return "redirect:/users";
        } catch (Exception e) {
            throw new FormSubmissionException("Could not create user: " + e.getMessage(), "/users", e);
        }
    }

    @PostMapping("/view")
    public String viewUser(@RequestParam String userId, Model model) {
        try {
            // LOG.info("Fetching user details for ID: {}", userId);
            UserDto user = DtoMapper.toUserDto(coreApiService.getUserById(userId));
            model.addAttribute("user", user);
            // LOG.info("User details fetched successfully for ID: {}", userId);
            return "users/detail";
        } catch (Exception e) {
            throw new WebApiException("Could not load user details: " + e.getMessage(), CURRENT_PATH, "users/detail", e);
        }
    }

    @PostMapping("/edit-form")
    public String editUserForm(@RequestParam String userId, Model model) {
        try {
            // LOG.info("Loading edit form for user ID: {}", userId);
            UserDto user = DtoMapper.toUserDto(coreApiService.getUserById(userId));
            model.addAttribute("user", user);
            return "users/edit";
        } catch (Exception e) {
            throw new WebApiException("Could not load user for editing: " + e.getMessage(), CURRENT_PATH, "users/edit", e);
        }
    }

    @PostMapping("/update")
    public String updateUser(
            @RequestParam String userId,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam String status,
            @RequestParam(required = false) String password,
            RedirectAttributes redirectAttributes) {
        
        try {
            // LOG.info("Updating user: {}", userId);
            UserDto user = new UserDto();
            user.setUserId(userId);
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setStatus(status);
            
            if (password != null && !password.trim().isEmpty()) {
                user.setPassword(password);
            }

            coreApiService.updateUser(DtoMapper.toMap(user));
            // LOG.info("User updated successfully: {}", userId);
            redirectAttributes.addFlashAttribute("success", "User updated successfully!");
            return "redirect:/users";
        } catch (Exception e) {
            throw new FormSubmissionException("Could not update user: " + e.getMessage(), "/users", e);
        }
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam String userId, RedirectAttributes redirectAttributes) {
        try {
            // LOG.info("Deleting user: {}", userId);
            coreApiService.deleteUser(userId);
            // LOG.info("User deleted successfully: {}", userId);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
            return "redirect:/users";
        } catch (Exception e) {
            throw new FormSubmissionException("Could not delete user: " + e.getMessage(), "/users", e);
        }
    }

    @PostMapping("/roles-form")
    public String manageUserRoles(@RequestParam String userId, Model model) {
        try {
            // LOG.info("Loading roles management for user ID: {}", userId);
            UserDto user = DtoMapper.toUserDto(coreApiService.getUserById(userId));
            List<RoleDto> allRoles = coreApiService.getAllRoles().stream()
                .map(DtoMapper::toRoleDto)
                .collect(Collectors.toList());
            
            model.addAttribute("user", user);
            model.addAttribute("allRoles", allRoles);
            return "users/roles";
        } catch (Exception e) {
            throw new WebApiException("Could not load roles management: " + e.getMessage(), CURRENT_PATH, "users/roles", e);
        }
    }

    @PostMapping("/assign-role")
    public String assignRoleToUser(
            @RequestParam String userId,
            @RequestParam String roleId,
            RedirectAttributes redirectAttributes) {
        
        try {
            // LOG.info("Assigning role {} to user {}", roleId, userId);
            coreApiService.assignRoleToUser(userId, roleId);
            redirectAttributes.addFlashAttribute("success", "Role assigned successfully!");
            return "redirect:/users";
        } catch (Exception e) {
            throw new FormSubmissionException("Could not assign role: " + e.getMessage(), "/users", e);
        }
    }

    @PostMapping("/remove-role")
    public String removeRoleFromUser(
            @RequestParam String userId,
            @RequestParam String roleId,
            RedirectAttributes redirectAttributes) {
        
        try {
            // LOG.info("Removing role {} from user {}", roleId, userId);
            coreApiService.removeRoleFromUser(userId, roleId);
            redirectAttributes.addFlashAttribute("success", "Role removed successfully!");
            return "redirect:/users";
        } catch (Exception e) {
            throw new FormSubmissionException("Could not remove role: " + e.getMessage(), "/users", e);
        }
    }
} 