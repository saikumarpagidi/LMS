package com.lms.cdac.services.impl;

import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import com.lms.cdac.services.EmailService;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {
        if (to == null || !EmailValidator.getInstance().isValid(to)) {
            logger.error("Invalid email address: {}", to);
            throw new IllegalArgumentException("Invalid email address: " + to);
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("adityabhargavcdac@gmail.com");

            emailSender.send(message);
            logger.info("Email sent successfully to {}", to);
        } catch (MailException e) {
            logger.error("Error occurred while sending email: {}", e.getMessage());
            throw new RuntimeException("Error sending email.", e);
        }
    }

    @Override
    public void sendEmailWithHtml(String to, String subject, String verificationLink) {
        if (to == null || !EmailValidator.getInstance().isValid(to)) {
            logger.error("Invalid email address: {}", to);
            throw new IllegalArgumentException("Invalid email address: " + to);
        }

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);

            String htmlBody = "<html><body style='font-family: Arial, sans-serif; color: #333;'>" +
                    "<h2>Welcome to LMS!</h2>" +
                    "<p>Your account has been created successfully.</p>" +
                    "<p>Click the link below to verify your email:</p>" +
                    "<a href='" + verificationLink + "' style='display: inline-block; background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Verify Email</a>" +
                    "<p>If you did not request this, please ignore this email.</p>" +
                    "</body></html>";

            helper.setText(htmlBody, true);
            helper.setFrom("adityabhargavcdac@gmail.com");

            emailSender.send(message);
            logger.info("HTML email sent successfully to {}", to);
        } catch (Exception e) {
            logger.error("Error occurred while sending HTML email: {}", e.getMessage());
            throw new RuntimeException("Error sending HTML email.", e);
        }
    }

    @Override
    public void sendEmailWithAttachment(String to, String subject, String body, String attachmentPath) {
        logger.warn("Email with attachment functionality not yet implemented.");
    }
}
