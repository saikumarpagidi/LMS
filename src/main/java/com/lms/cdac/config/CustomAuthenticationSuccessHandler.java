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
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		User user = (User) authentication.getPrincipal();
		if (user.hasRole("RESOURCE_CENTER")) {
			response.sendRedirect(request.getContextPath() + "/center/dashboard");
		} else if (user.hasRole("STUDENT")) {
			response.sendRedirect(request.getContextPath() + "/student/dashboard");
		} else if (user.hasRole("FACULTY")) {
			response.sendRedirect(request.getContextPath() + "/faculty/dashboard");
		} else if (user.hasRole("PMU_NOIDA")) { // New role check "PMU_NOIDA"
            response.sendRedirect(request.getContextPath() + "/pmu/noida/dashboard"); // Redirect to Noida dashboard
        } else if (user.hasRole("PMU_MOHALI")) { // New role check
            response.sendRedirect(request.getContextPath() + "/pmu/mohali/dashboard"); // Redirect to Mohali dashboard
		} else {
			response.sendRedirect(request.getContextPath() + "/user/dashboard");
		}
	}
}
