package com.lms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.lms.cdac.LmsApplication;
import com.lms.cdac.services.EmailService;

@ContextConfiguration(classes = LmsApplication.class)
@SpringBootTest
class LmsApplicationTests {

    @Autowired
    private EmailService service;

    @Test
    void contextLoads() {
        // Verifies that the context loads successfully
    }

    @Test
    void sendEmailSend() {
        // Test for sending an email
        service.sendEmail("adityabhargav049@gmail.com", "Testing", "Just Testing purpose");
    }
}
