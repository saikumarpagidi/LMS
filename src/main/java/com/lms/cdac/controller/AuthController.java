package com.lms.cdac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import com.lms.cdac.entities.User;
import com.lms.cdac.helper.Message;
import com.lms.cdac.helper.MessageType;
import com.lms.cdac.repsitories.UserRepositories;
import com.lms.cdac.services.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepositories userRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;  // Fixed issue (changed from BCryptPasswordEncoder)

    // Forgot Password: Display Forgot Password Page
    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "user/forgot-password"; // Points to user/forgot-password.html template
    }

    // Forgot Password: Process Forgot Password Request
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, HttpSession session) {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String token = UUID.randomUUID().toString();  // Generate a unique token
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));  // Set expiry time (1 hour)
            userRepo.save(user);

            // Send email with reset link
            String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;
            emailService.sendEmailWithHtml(user.getEmail(), "Password Reset Request",
                    "<p>Click the link to reset your password: <a href='" + resetLink + "'>Reset Password</a></p>");

            session.setAttribute("message", Message.builder()
                    .type(MessageType.green)
                    .content("A password reset link has been sent to your email.")
                    .build());
        } else {
            session.setAttribute("message", Message.builder()
                    .type(MessageType.red)
                    .content("No account found with this email.")
                    .build());
        }
        return "user/forgot-password"; // Return to the forgot password page after processing
    }

    // Show Reset Password Form
    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token, Model model, HttpSession session) {
        User user = userRepo.findByResetToken(token).orElse(null);
        if (user == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            session.setAttribute("message", Message.builder()
                    .type(MessageType.red)
                    .content("Invalid or expired token.")
                    .build());
            return "error_page"; // Show error page if token is invalid or expired
        }
        model.addAttribute("token", token); // Pass the token to the reset password form
        return "user/reset-password"; // Return to the reset password page
    }

    // Process Password Reset
    @PostMapping("/update-password")
    public String updatePassword(@RequestParam("token") String token,
                                 @RequestParam("newPassword") String newPassword,
                                 HttpSession session) {
        User user = userRepo.findByResetToken(token).orElse(null);
        if (user != null && user.getResetTokenExpiry().isAfter(LocalDateTime.now())) {
            user.setPassword(passwordEncoder.encode(newPassword));  // Encrypt the new password before saving
            user.setResetToken(null);  // Clear reset token after password update
            userRepo.save(user);

            session.setAttribute("message", Message.builder()
                    .type(MessageType.green)
                    .content("Your password has been reset successfully.")
                    .build());
            return "redirect:/login"; // Redirect to login page after successful password reset
        }

        session.setAttribute("message", Message.builder()
                .type(MessageType.red)
                .content("Invalid or expired token.")
                .build());
        return "error_page"; // Show error if token is invalid or expired
    }
    
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied"; // Thymeleaf template name without .html
    }
}
