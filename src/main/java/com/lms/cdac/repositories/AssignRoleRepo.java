package com.lms.cdac.repositories;
import com.lms.cdac.dto.AnalyticsDTO;
import com.lms.cdac.entities.AssignRole;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lms.cdac.entities.AssignRole;

import com.lms.cdac.entities.RoleUser;
import com.lms.cdac.entities.User;

public interface AssignRoleRepo extends JpaRepository<AssignRole, Long> {
   // boolean existsByUserAndRole(User user, Role role);
    
    boolean existsByUserAndRoleUser(User user, RoleUser roleUser);
       
//    @Query("SELECT new com.lms.cdac.dto.AnalyticsDTO(ar.roleUser.name, COUNT(ar.user)) " +
//            "FROM AssignRole ar " +
//            "GROUP BY ar.roleUser.name")
//     List<AnalyticsDTO> findUserRoleDistribution();
    
    
    /**
     * Count distinct users per resourceCenter.
     * Returns AnalyticsDTO with:
     *   - category = resourceCenter name
     *   - value    = # of distinct users assigned there
     */
    @Query("""
      SELECT new com.lms.cdac.dto.AnalyticsDTO(
               ar.resourceCenter,
               COUNT(DISTINCT ar.user.id)
             )
      FROM AssignRole ar
      GROUP BY ar.resourceCenter
      ORDER BY ar.resourceCenter
    """)
    List<AnalyticsDTO> countUsersByResourceCenter();



   }
