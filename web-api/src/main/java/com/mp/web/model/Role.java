package com.mp.web.model;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Role {
    private String roleId;
    private String name;
    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
    private String updatedBy;
} 