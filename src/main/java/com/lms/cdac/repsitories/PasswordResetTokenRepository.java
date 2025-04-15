package com.lms.cdac.repsitories;

import com.lms.cdac.entities.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
    // Find a password reset token by its token string
    PasswordResetToken findByToken(String token);
}
