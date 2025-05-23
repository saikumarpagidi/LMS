package com.lms.cdac.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MultipartConfig {

    @Value("${spring.servlet.multipart.location:${java.io.tmpdir}}")
    private String uploadLocation;

    @PostConstruct
    public void init() {
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadLocation);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created upload directory: " + uploadPath.toAbsolutePath());
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not create upload directory: " + uploadLocation, e);
        }
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        
        // Ensure the directory exists
        File uploadDir = new File(uploadLocation);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        factory.setLocation(uploadLocation);
        factory.setMaxFileSize(DataSize.ofMegabytes(20));
        factory.setMaxRequestSize(DataSize.ofMegabytes(50));
        
        return factory.createMultipartConfig();
    }
}