package com.lms.cdac.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.cdac.entities.AssignRole;
import com.lms.cdac.entities.RoleUser;
import com.lms.cdac.entities.User;
import com.lms.cdac.repositories.AssignRoleRepo;
import com.lms.cdac.repsitories.RoleRepo;
import com.lms.cdac.repsitories.UserRepositories;
import com.lms.cdac.services.AssignRoleService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class AssignRoleServiceImpl implements AssignRoleService {

    private final AssignRoleRepo assignRoleRepository;
    private final UserRepositories userRepository;
    private final RoleRepo roleUserRepository;

    @Autowired
    public AssignRoleServiceImpl(AssignRoleRepo assignRoleRepository, 
                                 UserRepositories userRepository, 
                                 RoleRepo roleUserRepository) {
        this.assignRoleRepository = assignRoleRepository;
        this.userRepository       = userRepository;
        this.roleUserRepository   = roleUserRepository;
    }

    @Override
    public List<AssignRole> getAllAssignedRoles() {
        return assignRoleRepository.findAll();
    }

    @Override
    public Page<AssignRole> getAssignedRoles(Pageable pageable) {
        return assignRoleRepository.findAll(pageable);
    }

    @Override
    public AssignRole saveAssignRole(AssignRole assignRole) {
        if (assignRole.getUser() == null || assignRole.getRoleUser() == null) {
            throw new IllegalArgumentException("User or RoleUser is null");
        }

        Optional<User> userOpt      = userRepository.findById(assignRole.getUser().getUserId());
        Optional<RoleUser> roleOpt  = roleUserRepository.findById(assignRole.getRoleUser().getId());

        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + assignRole.getUser().getUserId());
        }
        if (roleOpt.isEmpty()) {
            throw new IllegalArgumentException("RoleUser not found with ID: " + assignRole.getRoleUser().getId());
        }

        assignRole.setUser(userOpt.get());
        assignRole.setRoleUser(roleOpt.get());

        return assignRoleRepository.save(assignRole);
    }

    @Override
    public void deleteAssignRole(Long id) {
        if (!assignRoleRepository.existsById(id)) {
            throw new EntityNotFoundException("Assigned role not found with id " + id);
        }
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
        Optional<User> userOpt        = userRepository.findById(userId);
        Optional<RoleUser> roleOpt    = roleUserRepository.findById(roleId);

        if (userOpt.isPresent() && roleOpt.isPresent()) {
            User user         = userOpt.get();
            RoleUser roleUser = roleOpt.get();

            if (!assignRoleRepository.existsByUserAndRoleUser(user, roleUser)) {
                AssignRole ar = new AssignRole();
                ar.setUser(user);
                ar.setRoleUser(roleUser);
                ar.setResourceCenter(resourceCenter);
                assignRoleRepository.save(ar);
            }
        } else {
            throw new EntityNotFoundException("User or RoleUser not found");
        }
    }
}
