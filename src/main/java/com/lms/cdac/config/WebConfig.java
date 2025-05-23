package com.lms.cdac.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${spring.servlet.multipart.location}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve normal static assets from classpath:/static/
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        // Serve uploaded files from the file system
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String uploadUrl = uploadPath.toUri().toASCIIString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadUrl);
    }
}
