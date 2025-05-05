package com.lms.cdac.repsitories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    

}
