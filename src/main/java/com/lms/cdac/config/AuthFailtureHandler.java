package com.lms.cdac.config;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.lms.cdac.helper.Message;
import com.lms.cdac.helper.MessageType;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthFailtureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        HttpSession session = request.getSession();

        // 1) अगर ईमेल DB में नहीं मिला
        if (exception instanceof UsernameNotFoundException) {
            session.setAttribute("message", 
                Message.builder()
                       .content("Email is wrong, please correct.")
                       .type(MessageType.red)
                       .build()
            );
        }
        // 2) अगर पासवर्ड गलत है
        else if (exception instanceof BadCredentialsException) {
            session.setAttribute("message", 
                Message.builder()
                       .content("Wrong password, please correct.")
                       .type(MessageType.red)
                       .build()
            );
        }
        // 3) अगर यूज़र डिसेबल्ड है
        else if (exception instanceof DisabledException) {
            session.setAttribute("message",
                Message.builder()
                       .content("User is disabled. Verification link has been sent to your email!")
                       .type(MessageType.red)
                       .build()
            );
        }
        // 4) किसी अन्य AuthException (Optional – आप चाहें तो और केस जोड़ सकते हैं)
        else {
            session.setAttribute("message", 
                Message.builder()
                       .content("Authentication failed. Please try again.")
                       .type(MessageType.red)
                       .build()
            );
        }

        // किसी भी फेल्योर के बाद लॉगिन पेज पर रीडायरेक्ट
        response.sendRedirect(request.getContextPath() + "/home/login");
    }
}
