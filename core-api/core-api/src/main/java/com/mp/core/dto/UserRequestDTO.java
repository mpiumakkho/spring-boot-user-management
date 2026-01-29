package com.mp.core.dto;

import com.mp.core.validation.ValidateUsernameOnCreate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@ValidateUsernameOnCreate
public class UserRequestDTO {
    @NotBlank(message = "User ID is required for this operation")
    private String userId;
    
    private String username;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String password;
    private String status;
    private String roleId;

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }
} 