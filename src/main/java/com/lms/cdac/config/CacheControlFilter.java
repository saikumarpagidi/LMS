package com.lms.cdac.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter to add cache control headers to sensitive pages like login and register
 * to prevent browser back button issues after authentication
 */
@Component
public class CacheControlFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        
        // Check if this is a sensitive page that should not be cached
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/login") || 
            requestURI.contains("/register") || 
            requestURI.contains("/authenticate") ||
            requestURI.contains("/do-register")) {
            
            // Set headers to prevent caching for these sensitive pages
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
        }
        
        chain.doFilter(request, response);
    }
} 