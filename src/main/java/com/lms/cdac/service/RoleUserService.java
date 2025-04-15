package com.lms.cdac.service;

import com.lms.cdac.entities.RoleUser;
import java.util.List;
import java.util.Optional;

public interface RoleUserService {
    Optional<RoleUser> findByRoleName(String roleName);
    RoleUser createRoleUser(String roleName);
    List<RoleUser> getAllRoles();
    void updateRole(Long id, String roleName);
    void deleteRole(Long id);
    
    Optional<RoleUser> getRoleById(Long id); // Fixed this method
    Optional<RoleUser> findById(Long roleId);
    
    List<RoleUser> getAllRoleUsers();
    void addRole(RoleUser roleUser); 
    //<RoleUser> getRolesByEmail(String email);
     
}
