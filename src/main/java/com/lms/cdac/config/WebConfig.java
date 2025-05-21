package com.lms.cdac.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve normal static assets from classpath:/static/
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        // Serve uploaded files from classpath:/static/uploads/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("classpath:/static/uploads/");
    }
}
