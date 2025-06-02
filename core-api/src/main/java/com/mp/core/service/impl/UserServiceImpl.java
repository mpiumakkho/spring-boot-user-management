package com.mp.core.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mp.core.entity.Role;
import com.mp.core.entity.User;
import com.mp.core.repository.RoleRepository;
import com.mp.core.repository.UserRepository;
import com.mp.core.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LogManager.getLogger(UserServiceImpl.class);
    
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;
    
    private static final int MAX_LOGIN_ATTEMPTS = 5;

    public UserServiceImpl(
            UserRepository userRepo, 
            RoleRepository roleRepo,
            PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public User createUser(User user) {
        if (userRepo.existsByUsername(user.getUsername())) {
            log.warn("Username {} is already taken", user.getUsername());
            throw new IllegalArgumentException("This username is not available");
        }
        
        String email = user.getEmail();
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        if (userRepo.existsByEmail(email)) {
            log.warn("Email {} is already registered", email);
            throw new IllegalArgumentException("This email is already registered");
        }

        if (user.getPassword() != null) {
            user.setPassword(encoder.encode(user.getPassword()));
        }

        user.setStatus("pending");
        
        log.info("Creating new user account for: {}", user.getUsername());
        return userRepo.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        User existing = userRepo.findById(user.getUserId())
            .orElseThrow(() -> new NoSuchElementException("User not found: " + user.getUserId()));
        
        String newUsername = user.getUsername();
        if (!existing.getUsername().equals(newUsername)) {
            if (userRepo.existsByUsername(newUsername)) {
                throw new IllegalArgumentException("Username already taken");
            }
            existing.setUsername(newUsername);
        }
        
        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        
        String newPassword = user.getPassword();
        if (newPassword != null && !newPassword.isEmpty()) {
            existing.setPassword(encoder.encode(newPassword));
        }
        
        existing.setUpdatedBy(user.getUpdatedBy());
        
        log.debug("Updating user: {}", existing.getUserId());
        return userRepo.save(existing);
    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        if (!userRepo.existsById(id)) {
            log.warn("Attempted to delete non-existent user: {}", id);
            throw new NoSuchElementException("User not found");
        }
        
        try {
            userRepo.deleteById(id);
            log.info("User {} deleted successfully", id);
        } catch (Exception e) {
            log.error("Failed to delete user: " + id, e);
            throw new RuntimeException("Could not delete user", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(String id) {
        return userRepo.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    @Transactional
    public void assignRoleToUser(String userId, String roleId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found"));
            
        Role role = roleRepo.findById(roleId)
            .orElseThrow(() -> new NoSuchElementException("Role not found"));

        if (user.getRoles().contains(role)) {
            log.info("User {} already has role {}", userId, roleId);
            return;
        }

        user.getRoles().add(role);
        userRepo.save(user);
        log.info("Role {} assigned to user {}", roleId, userId);
    }

    @Override
    @Transactional
    public void removeRoleFromUser(String userId, String roleId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found"));
            
        Role role = roleRepo.findById(roleId)
            .orElseThrow(() -> new NoSuchElementException("Role not found"));

        if (user.getRoles().remove(role)) {
            userRepo.save(user);
            log.info("Role {} removed from user {}", roleId, userId);
        } else {
            log.warn("User {} did not have role {}", userId, roleId);
        }
    }

    @Override
    @Transactional
    public User updateUserStatus(String userId, String status) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found"));
            
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        user.setStatus(status);
        
        return userRepo.save(user);
    }

    private boolean isValidStatus(String status) {
        return status != null && List.of("active", "inactive", "pending", "locked").contains(status);
    }

    @Override
    @Transactional
    public User activateUser(String userId) {
        return updateUserStatus(userId, "active");
    }

    @Override
    @Transactional
    public User deactivateUser(String userId) {
        return updateUserStatus(userId, "inactive");
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getPendingUsers() {
        return userRepo.findByStatus("pending");
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByStatus(String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status filter: " + status);
        }
        return userRepo.findByStatus(status);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }
} 