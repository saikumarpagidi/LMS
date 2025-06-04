package com.lms.cdac.repsitories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lms.cdac.entities.User;

@Repository
public interface UserRepositories extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndPassword(String email, String password);
    Optional<User> findByEmailToken(String emailToken);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findByName(String name);
    Optional<User> findByResetToken(String resetToken);
    List<User> findByCollege(String college);
    List<User> findByResourceCenter(String resourceCenter);
    Optional<User> findById(String id);
    Optional<User> findByVerificationToken(String verificationToken);
    
    @Query("SELECT DISTINCT u.resourceCenter FROM User u WHERE u.resourceCenter IS NOT NULL")
    List<String> findDistinctResourceCenters();
    
    @Query("SELECT COUNT(u) FROM User u")
    Long countAllUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.college IN :names")
    long countByCollegeIn(List<String> names);

    @Query("SELECT COUNT(u) FROM User u WHERE u.resourceCenter IN :rcNames")
    long countByResourceCenterIn(List<String> rcNames);
    
    // ────────────── EXISTING QUERY METHODS ──────────────

    /** RC में कुल Students */
    @Query("SELECT u.resourceCenter AS category, COUNT(u) AS value FROM User u GROUP BY u.resourceCenter")
    List<Object[]> countStudentsByResourceCenter();
    
    @Query("SELECT u.college AS category, COUNT(u) AS value FROM User u GROUP BY u.college")
    List<Object[]> countStudentsByCollege();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.resourceCenter = :rc")
    long countByResourceCenter(@Param("rc") String resourceCenter);

    @Query("SELECT u.college AS category, COUNT(u) AS value " +
            "FROM User u " +
            "WHERE u.resourceCenter = :rc " +
            "GROUP BY u.college")
    List<Object[]> countStudentsByCollegeInResourceCenter(@Param("rc") String resourceCenter);
    
    /**
     * पूरे सिस्टम के STUDENT रोल वाले Users को नाम और Resource Center के हिसाब से गिनेगा।
     * (यह डेटा बिना किसी फ़िल्टर के सभी Resource Centers के लिए लौटाएगा)
     */
    @Query(value = 
        "SELECT u.user_name, u.resource_center, COUNT(*) " +
        "FROM users u " +
        "JOIN assign_roles ar ON u.user_id = ar.user_id " +
        "JOIN role_users r   ON ar.role_user_id = r.id " +
        "WHERE r.role_name = 'STUDENT' " +
        "GROUP BY u.user_name, u.resource_center",
        nativeQuery = true
    )
    List<Object[]> countStudentsByNameAndResourceCenter();

    /**
     * केवल उस Resource Center के STUDENT Users को गिनेगा जिनका रोल STUDENT है।
     * :rcCenter पैरामीटर द्वारा फ़िल्टर करेगा ।
     */
    @Query(value = 
        "SELECT u.user_name, u.resource_center, COUNT(*) " +
        "FROM users u " +
        "JOIN assign_roles ar ON u.user_id = ar.user_id " +
        "JOIN role_users r   ON ar.role_user_id = r.id " +
        "WHERE r.role_name = 'STUDENT' " +
        "  AND u.resource_center = :rcCenter " +
        "GROUP BY u.user_name, u.resource_center",
        nativeQuery = true
    )
    List<Object[]> countStudentsByNameAndSpecificResourceCenter(@Param("rcCenter") String resourceCenter);
}
