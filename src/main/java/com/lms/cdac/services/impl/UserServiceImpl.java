package com.lms.cdac.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.lms.cdac.entities.RoleUser;
import com.lms.cdac.entities.User;
import com.lms.cdac.helper.ResourceNotFoundException;
import com.lms.cdac.repsitories.RoleRepo;
import com.lms.cdac.repsitories.UserRepositories;
import com.lms.cdac.services.EmailService;
import com.lms.cdac.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepositories userRepo;
    private final RoleRepo roleRepo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder; // PasswordEncoder Injected ✅

    @Override
    public User saveUser(User user) {
        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword())); // 🔒 Password Encryption
        User savedUser = userRepo.save(user);
        emailService.sendEmail(user.getEmail(), "Welcome to LMS", "Your account has been created successfully.");
        return savedUser;
    }

    @Override
    public Optional<User> getUserById(String id) {
        return userRepo.findById(id);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Optional<User> updateUser(User user) {
        int retryCount = 3;
        while (retryCount > 0) {
            try {
                User existingUser = userRepo.findById(user.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + user.getUserId()));

                existingUser.setName(user.getName());
                existingUser.setEmail(user.getEmail());
                existingUser.setPhoneNumber(user.getPhoneNumber());

                // Encrypt new password only if it's not null or empty
                if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(user.getPassword())); // 🔒 Password Encryption
                }

                if (existingUser.getVersion() == null) {
                    existingUser.setVersion(0L);
                }

                return Optional.of(userRepo.save(existingUser));
            } catch (ObjectOptimisticLockingFailureException ex) {
                retryCount--;
                if (retryCount == 0) {
                    log.error("Optimistic locking failed after multiple attempts for User ID: {}", user.getUserId());
                    throw new ResourceNotFoundException("The user record has been updated by another process. Please retry the operation.");
                }
                log.warn("Optimistic locking failed, retrying... Attempt {}", 4 - retryCount);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean isUserExist(String userId) {
        return userRepo.existsById(userId);
    }

    @Override
    public boolean isUserExistByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public void deleteUser(String userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        userRepo.deleteById(userId);
    }

    @Override
    public void deleteUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        userRepo.delete(user);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username));
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable);
    }

    @Override
    public List<User> getUsersByCollege(String college) {
        return userRepo.findByCollege(college);
    }

    @Override
    public List<User> getUsersByResourceCenter(String resourceCenter) {
        return userRepo.findByResourceCenter(resourceCenter);
    }

    @Override
    @Transactional
    public void assignRole(String userId, String roleName) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        RoleUser roleUser = roleRepo.findByRoleName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("RoleUser not found: " + roleName));

        if (!user.hasRole(roleName)) {
            user.addRole(roleUser);
            userRepo.save(user);
            log.info("Assigned role {} to user with ID: {}", roleName, userId);
        } else {
            log.info("User with ID: {} already has role: {}", userId, roleName);
        }
    }

    @Override
    public List<String> getUserRoles(String userId) {
        return userRepo.findById(userId)
                .map(user -> user.getAssignedRoles().stream()
                        .map(assignRole -> assignRole.getRoleUser().getRoleName())
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    @Override
    public boolean hasRole(String userId, String roleName) {
        return userRepo.findById(userId)
                .map(user -> user.getAssignedRoles().stream()
                        .anyMatch(assignRole -> assignRole.getRoleUser().getRoleName().equalsIgnoreCase(roleName)))
                .orElse(false);
    }

    @Override
    @Transactional
    public void removeRole(String userId, String roleName) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        RoleUser roleUser = roleRepo.findByRoleName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("RoleUser not found: " + roleName));

        if (user.hasRole(roleName)) {
            user.removeRole(roleUser);
            userRepo.save(user);
            log.info("Removed role {} from user with ID: {}", roleName, userId);
        } else {
            log.info("User with ID: {} does not have role: {}", userId, roleName);
        }
    }

    @Override
    public void deleteUserById(String id) {
        if (userRepo.existsById(id)) {
            userRepo.deleteById(id);
        } else {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
    }

    @Override
    public Optional<User> findById(String userId) {
        return userRepo.findById(userId);
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }

    @Override
    public Optional<User> findByVerificationToken(String token) {
        return userRepo.findByVerificationToken(token);
    }
}
