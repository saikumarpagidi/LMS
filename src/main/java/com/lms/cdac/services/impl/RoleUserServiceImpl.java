package com.lms.cdac.services.impl;

import com.lms.cdac.entities.RoleUser;
import com.lms.cdac.repsitories.RoleRepo;
import com.lms.cdac.service.RoleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleUserServiceImpl implements RoleUserService {

    @Autowired
    private RoleRepo roleRepo;

    @Override
    public Optional<RoleUser> findByRoleName(String roleName) {
        return roleRepo.findByRoleName(roleName);
    }

    @Override
    public RoleUser createRoleUser(String roleName) {
        return roleRepo.findByRoleName(roleName)
                .orElseGet(() -> {
                    RoleUser roleUser = new RoleUser();
                    roleUser.setRoleName(roleName);
                    return roleRepo.save(roleUser);
                });
    }

    @Override
    public List<RoleUser> getAllRoles() {
        return roleRepo.findAll();
    }

    @Override
    public void updateRole(Long id, String roleName) {
        RoleUser role = roleRepo.findById(id).orElseThrow(() -> 
                new RuntimeException("Role not found with ID: " + id));
        role.setRoleName(roleName);
        roleRepo.save(role);
    }

    @Override
    public void deleteRole(Long id) {
        if (!roleRepo.existsById(id)) {
            throw new RuntimeException("Role not found with ID: " + id);
        }
        roleRepo.deleteById(id);
    }
    
    @Override
    public Optional<RoleUser> getRoleById(Long id) {
        return roleRepo.findById(id); // Directly return Optional
    }


        @Override
    public List<RoleUser> getAllRoleUsers() {
        return roleRepo.findAll();
    }

    @Override
    public Optional<RoleUser> findById(Long roleId) {
        return roleRepo.findById(roleId);
    }

    @Override
    public void addRole(RoleUser roleUser) {
        // Check if role already exists
        if (roleRepo.existsByRoleName(roleUser.getRoleName())) {
            throw new RuntimeException("Role already exists: " + roleUser.getRoleName());
        }
        // Save the role to the database
        roleRepo.save(roleUser);  // This method is sufficient
    }

	
    
    
   
}
