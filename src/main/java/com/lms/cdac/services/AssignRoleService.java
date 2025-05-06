package com.lms.cdac.services;

import com.lms.cdac.entities.AssignRole;
import com.lms.cdac.entities.RoleUser;
import com.lms.cdac.entities.User;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssignRoleService {

    List<AssignRole> getAllAssignedRoles();

    AssignRole saveAssignRole(AssignRole assignRole);

    void deleteAssignRole(Long id);
    
    boolean existsById(Long id);
    
    boolean existsByUserAndRoleUser(User user, RoleUser roleUser);

    void assignRoleToUser(String userId, Long roleId, String resourceCenter);
    
    Page<AssignRole> getAssignedRoles(Pageable pageable);
}
