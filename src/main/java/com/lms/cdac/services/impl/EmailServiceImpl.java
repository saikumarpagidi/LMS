// src/main/java/com/lms/cdac/services/impl/EmailServiceImpl.java
package com.lms.cdac.services.impl;

import com.lms.cdac.services.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender emailSender;

    @Override 
    public void sendEmail(String to, String subject, String body) {
        if (!EmailValidator.getInstance().isValid(to)) {
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
            logger.info("Plain-text email sent to {}", to);
        } catch (MailException e) {
            logger.error("Error sending plain-text email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Error sending email.", e);
        }
    }

    @Override
    public void sendEmailWithHtml(String to, String subject, String htmlBody) {
        if (!EmailValidator.getInstance().isValid(to)) {
            logger.error("Invalid email address: {}", to);
            throw new IllegalArgumentException("Invalid email address: " + to);
        }
        try {
            MimeMessage message = emailSender.createMimeMessage();
            // true = multipart
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            // Use the htmlBody passed in directly
            helper.setText(htmlBody, true);
            helper.setFrom("adityabhargavcdac@gmail.com");

            emailSender.send(message);
            logger.info("HTML email sent successfully to {}", to);
        } catch (Exception e) {
            logger.error("Error sending HTML email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Error sending HTML email.", e);
        }
    }

    @Override
    public void sendEmailWithAttachment(String to, String subject, String body, String attachmentPath) {
        logger.warn("Email with attachment functionality not yet implemented.");
    }
}
