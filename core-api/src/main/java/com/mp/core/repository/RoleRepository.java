package com.mp.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mp.core.entity.Role;

public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
} 