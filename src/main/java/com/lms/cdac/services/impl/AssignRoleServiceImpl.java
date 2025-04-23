package com.lms.cdac.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.cdac.entities.AssignRole;
import com.lms.cdac.entities.RoleUser;
import com.lms.cdac.entities.User;
import com.lms.cdac.repositories.AssignRoleRepo;
import com.lms.cdac.repsitories.RoleRepo;
import com.lms.cdac.repsitories.UserRepositories;
import com.lms.cdac.services.AssignRoleService;

@Service
public class AssignRoleServiceImpl implements AssignRoleService {

    private final AssignRoleRepo assignRoleRepository;
    private final UserRepositories userRepository;
    private final RoleRepo roleUserRepository;

    @Autowired
    public AssignRoleServiceImpl(AssignRoleRepo assignRoleRepository, 
                                 UserRepositories userRepository, 
                                 RoleRepo roleUserRepository) {
        this.assignRoleRepository = assignRoleRepository;
        this.userRepository = userRepository;
        this.roleUserRepository = roleUserRepository;
    }

    @Override
    @Transactional
    public List<AssignRole> getAllAssignedRoles() {
        return assignRoleRepository.findAll();
    }

    @Override
    @Transactional
    public AssignRole saveAssignRole(AssignRole assignRole) {
        if (assignRole.getUser() == null || assignRole.getRoleUser() == null) {
            throw new IllegalArgumentException("User or RoleUser is null");
        }

        Optional<User> userOptional = userRepository.findById(assignRole.getUser().getUserId());
        Optional<RoleUser> roleOptional = roleUserRepository.findById(assignRole.getRoleUser().getId());

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + assignRole.getUser().getUserId());
        }
        if (roleOptional.isEmpty()) {
            throw new IllegalArgumentException("RoleUser not found with ID: " + assignRole.getRoleUser().getId());
        }

        assignRole.setUser(userOptional.get());
        assignRole.setRoleUser(roleOptional.get());

        return assignRoleRepository.save(assignRole);
    }

    @Override
    public void deleteAssignRole(Long id) {
        assignRoleRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return assignRoleRepository.existsById(id);
    }

    @Override
    public boolean existsByUserAndRoleUser(User user, RoleUser roleUser) {
        return assignRoleRepository.existsByUserAndRoleUser(user, roleUser);
    }

    @Override
    public void assignRoleToUser(String userId, Long roleId, String resourceCenter) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<RoleUser> roleUserOptional = roleUserRepository.findById(roleId);

        if (userOptional.isPresent() && roleUserOptional.isPresent()) {
            User user = userOptional.get();
            RoleUser roleUser = roleUserOptional.get();

            if (!assignRoleRepository.existsByUserAndRoleUser(user, roleUser)) {
                AssignRole assignRole = new AssignRole();
                assignRole.setUser(user);
                assignRole.setRoleUser(roleUser);
                assignRole.setResourceCenter(resourceCenter);
                assignRoleRepository.save(assignRole);
            }
        } else {
            throw new RuntimeException("User or Role not found");
        }
    }
}
