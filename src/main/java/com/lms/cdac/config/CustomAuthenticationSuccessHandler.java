package com.lms.cdac.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.lms.cdac.entities.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response,
                                        Authentication authentication) 
                                        throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();

        // एक helper method से dashboard URL निकालें
        String dashboardUrl = getDashboardUrl(user);

        // session में एक बार ही डाल दें
        request.getSession().setAttribute("dashboardUrl", dashboardUrl);

        // फिर उसी URL पर redirect कर दें
        response.sendRedirect(request.getContextPath() + dashboardUrl);
    }

    // Role-to-URL mapping logic
    private String getDashboardUrl(User user) {
        if (user.hasRole("ADMIN")) {
            return "/user/dashboard";       // Admin के लिए
        }
        if (user.hasRole("RESOURCE_CENTER")) {
            return "/center/dashboard";
        }
        if (user.hasRole("FACULTY")) {
            return "/faculty/dashboard";
        }
        if (user.hasRole("PMU_NOIDA")) {
            return "/pmu/noida/dashboard";
        }
        if (user.hasRole("PMU_MOHALI")) {
            return "/pmu/mohali/dashboard";
        }
        if (user.hasRole("STUDENT")) {
            return "/student/dashboard";
        }
        // Default fallback
        return "/student/dashboard";
    }
}
