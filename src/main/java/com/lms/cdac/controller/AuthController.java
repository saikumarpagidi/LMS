// src/main/java/com/lms/cdac/controller/AuthController.java
package com.lms.cdac.controller;

import com.lms.cdac.entities.User;
import com.lms.cdac.helper.Message;
import com.lms.cdac.helper.MessageType;
import com.lms.cdac.repsitories.UserRepositories;
import com.lms.cdac.services.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    private PasswordEncoder passwordEncoder;

    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "user/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email,
                                        HttpSession session) {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
            userRepo.save(user);

            String resetLink = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/auth/reset-password")
                    .queryParam("token", token)
                    .toUriString();

            String html = "<!DOCTYPE html>" +
                    "<html><body style='font-family: sans-serif;'>" +
                    "<h2>Password Reset Request</h2>" +
                    "<p>Hi " + user.getName() + ",</p>" +
                    "<p>We received a request to reset your password. " +
                    "<a href='" + resetLink + "'>Click here</a> to choose a new one.</p>" +
                    "<p>If you didn’t request a password reset, disregard this email.</p>" +
                    "<br/><p>Thanks,<br/>Your LMS Team</p>" +
                    "</body></html>";

            emailService.sendEmailWithHtml(user.getEmail(), "Password Reset Request", html);

            session.setAttribute("message", Message.builder()
                    .type(MessageType.green)
                    .content("A reset link has been sent to your email.")
                    .build());
        } else {
            session.setAttribute("message", Message.builder()
                    .type(MessageType.red)
                    .content("No account found with that email.")
                    .build());
        }
        return "redirect:/auth/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam("token") String token,
                                    Model model,
                                    HttpSession session) {
        Optional<User> opt = userRepo.findByResetToken(token);
        if (opt.isEmpty() || opt.get().getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            session.setAttribute("message", Message.builder()
                    .type(MessageType.red)
                    .content("Invalid or expired link.")
                    .build());
            return "redirect:/auth/forgot-password";
        }
        model.addAttribute("token", token);
        return "user/reset-password";
    }

    @PostMapping("/update-password")
    public String updatePassword(@RequestParam("token") String token,
                                 @RequestParam("newPassword") String newPassword,
                                 HttpSession session) {
        Optional<User> opt = userRepo.findByResetToken(token);
        if (opt.isPresent() && opt.get().getResetTokenExpiry().isAfter(LocalDateTime.now())) {
            User user = opt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepo.save(user);

            session.setAttribute("message", Message.builder()
                    .type(MessageType.green)
                    .content("Your password has been reset successfully.")
                    .build());
            return "redirect:/auth/login";
        } else {
            session.setAttribute("message", Message.builder()
                    .type(MessageType.red)
                    .content("Invalid or expired link.")
                    .build());
            return "redirect:/auth/forgot-password";
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
