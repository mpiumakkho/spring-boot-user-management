package com.mp.web.model;

import java.util.Date;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private String userId;
    private String username;
    private String email;
    private boolean enabled;
    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
    private String updatedBy;
    private Set<Role> roles;
} 