package com.mp.web.dto;

import java.util.HashSet;
import java.util.Set;

// Data Transfer Object for User information
public class UserDto {
    private String userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String status;
    private String password;
    private String createdAt;
    private Set<RoleDto> roles;

    public UserDto() {this.roles = new HashSet<>();}

    public String getUserId() {return userId;}
    public void setUserId(String userId) {this.userId = userId;}
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public String getFirstName() {return firstName;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public String getLastName() {return lastName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
    public String getCreatedAt() {return createdAt;}
    public void setCreatedAt(String createdAt) {this.createdAt = createdAt;}
    public Set<RoleDto> getRoles() {return roles;}
    public void setRoles(Set<RoleDto> roles) {this.roles = roles;}
    public void addRole(RoleDto role) {this.roles.add(role);}
    public void removeRole(RoleDto role) {this.roles.remove(role);}
} 