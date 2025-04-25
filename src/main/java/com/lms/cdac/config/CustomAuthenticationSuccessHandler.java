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
		
		// Check roles in order of priority
		if (user.hasRole("ADMIN")) {
			response.sendRedirect(request.getContextPath() + "/user/dashboard");
		}  else if (user.hasRole("RESOURCE_CENTER")) {
			response.sendRedirect(request.getContextPath() + "/center/dashboard");
		} else if (user.hasRole("FACULTY")) {
			response.sendRedirect(request.getContextPath() + "/faculty/dashboard");
		} else if (user.hasRole("PMU_NOIDA")) {
			response.sendRedirect(request.getContextPath() + "/pmu/noida/dashboard");
		} else if (user.hasRole("PMU_MOHALI")) {
			response.sendRedirect(request.getContextPath() + "/pmu/mohali/dashboard");
		} else if (user.hasRole("STUDENT")) {
			response.sendRedirect(request.getContextPath() + "/student/dashboard");
		}
		else { 
			// Default fallback
			response.sendRedirect(request.getContextPath() + "/user/dashboard");
		}
	}
}
