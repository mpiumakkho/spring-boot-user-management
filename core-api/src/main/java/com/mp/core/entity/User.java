package com.mp.core.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users", schema = "sample_app")
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;
    
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String status = "inactive";
    
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        schema = "sample_app",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = "inactive";
        }
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getUserId() { return userId; }
    public String getStatus() { return status; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getUpdatedBy() { return updatedBy; }
    public String getCreatedBy() { return createdBy; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public Set<Role> getRoles() { return roles; }
    
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setStatus(String status) { this.status = status; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
} 