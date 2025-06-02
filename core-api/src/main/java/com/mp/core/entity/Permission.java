package com.mp.core.entity;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "permissions", schema = "sample_app")
@EntityListeners(AuditingEntityListener.class)
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String permissionId;
    private String name;
    private String description;
    private String resource;
    private String action;
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;
    private String createdBy;
    private String updatedBy;

    public String getName() { return name; }
    public String getPermissionId() { return permissionId; }
    public String getResource() { return resource; }
    public String getAction() { return action; }
    public String getDescription() { return description; }
    public String getUpdatedBy() { return updatedBy; }
    public String getCreatedBy() { return createdBy; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setResource(String resource) { this.resource = resource; }
    public void setAction(String action) { this.action = action; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
} 