package com.lms.cdac.repsitories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.lms.cdac.entities.RoleUser;

@Repository
public interface RoleRepo extends JpaRepository<RoleUser, Long> {
    boolean existsByRoleName(String roleName);  // Check if role with the name exists
    Optional<RoleUser> findByRoleName(String roleName);
    Optional<RoleUser> findById(Long roleId);
    
    
}
