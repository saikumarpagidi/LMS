package com.lms.cdac.repositories;
import com.lms.cdac.entities.AssignRole;


import org.springframework.data.jpa.repository.JpaRepository;
import com.lms.cdac.entities.AssignRole;

import com.lms.cdac.entities.RoleUser;
import com.lms.cdac.entities.User;

public interface AssignRoleRepo extends JpaRepository<AssignRole, Long> {
   // boolean existsByUserAndRole(User user, Role role);
    
    boolean existsByUserAndRoleUser(User user, RoleUser roleUser);

   }
