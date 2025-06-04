package com.lms.cdac.config;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CaptchaFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        if ("POST".equalsIgnoreCase(request.getMethod())
            && "/home/authenticate".equals(request.getServletPath())) {
            HttpSession session = request.getSession(false);
            String sessionCaptcha = session != null ? (String) session.getAttribute("CAPTCHA") : null;
            String inputCaptcha = request.getParameter("captchaInput");
            if (sessionCaptcha == null || inputCaptcha == null
                || !sessionCaptcha.equalsIgnoreCase(inputCaptcha)) {
                response.sendRedirect(request.getContextPath() + "/home/login?captchaError=true");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
