package com.lms.cdac.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // Lombok builder pattern
public class PasswordResetToken {

    @Id
    private String token; // Token is the primary key

    private String userId; // The User ID associated with the reset token

    private Date expiryDate; // The expiry date for the reset token

    // Method to check if the token has expired
    public boolean isExpired() {
        return expiryDate != null && expiryDate.before(new Date());
    }

    // Set expiry time (e.g., 1 hour from now)
    public void setExpiryDate() {
        this.expiryDate = new Date(System.currentTimeMillis() + 1000 * 60 * 60); // 1 hour expiry
    }

    // Invalidate/reset the token (clear method)
    public void clear() {
        this.token = null;
        this.userId = null;
        this.expiryDate = null;
    }
}
