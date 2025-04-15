package com.lms.cdac.services;

public interface EmailService {
    void sendEmail(String to, String subject, String body);

   void sendEmailWithHtml(String to, String subject, String body);  // Correct signature

    void sendEmailWithAttachment(String to, String subject, String body, String attachmentPath);
}
