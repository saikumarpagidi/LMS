package com.lms.cdac.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import com.lms.cdac.entities.Providers;
import com.lms.cdac.entities.User;
import com.lms.cdac.services.impl.UserServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Qualifier("oauthAuthHandler")
public class OAuthAuthenicationSuccessHandler  implements AuthenticationSuccessHandler {

    private static final Logger logger =
        LoggerFactory.getLogger(OAuthAuthenicationSuccessHandler .class);

    @Autowired
    private UserServiceImpl userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
                                        throws IOException, ServletException {

        logger.info("OAuth Success Handler triggered");

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();

        // Google से मिल रहा ईमेल/नाम
        String email = oauthUser.getAttribute("email");
        String name  = oauthUser.getAttribute("name");
        if (email == null || name == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing email or name");
            return;
        }

        // 1) JPA user load या create
        User user = userService.findByEmail(email);
        if (user == null) {
            logger.info("Creating new OAuth user: {}", email);
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setEnabled(true);
            user.setEmailVerified(true);
            user.setProvider(Providers.GOOGLE);
            user.setProviderUserId(oauthUser.getName());
            user.setPassword(UUID.randomUUID().toString());
            // phoneNumber NOT NULL इसलिए खाली सेट करते हैं
            user.setPhoneNumber("");
            user = userService.saveUser(user);
        }

        // 2) कम से कम STUDENT रोल हो
        if (user.getAssignedRoles().isEmpty()) {
            userService.assignRole(user.getUserId(), "STUDENT");
            logger.info("Assigned STUDENT role to {}", email);
        }

        // 3) रीलोड करें ताकि रोल्स मिल जाएँ
        user = userService.findByEmail(email);

        // 4) authorities तैयार करें
        List<SimpleGrantedAuthority> authorities = user.getAssignedRoles().stream()
            .map(ar -> new SimpleGrantedAuthority(
                "ROLE_" + ar.getRoleUser().getRoleName().toUpperCase()))
            .collect(Collectors.toList());

        // 5) Authentication principal बदलें
        UsernamePasswordAuthenticationToken newAuth =
            new UsernamePasswordAuthenticationToken(user, null, authorities);

        // 6) नया Authentication context & session में स्टोर करें
        SecurityContext sc = SecurityContextHolder.createEmptyContext();
        sc.setAuthentication(newAuth);
        request.getSession().setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
        SecurityContextHolder.setContext(sc);

        logger.info("User {} authenticated with roles {}", user.getEmail(), authorities);

        // 7) रोल्स के आधार पर रीडायरेक्ट
        String target;
        if (user.getCollege() == null || user.getPhoneNumber().isBlank()) {
            // प्रोफ़ाइल पूरा नहीं → complete-profile
            target = "/complete-profile";
        } else if (user.hasRole("ADMIN")) {
            target = "/user/dashboard";
        } else if (user.hasRole("RESOURCE_CENTER")) {
            target = "/center/dashboard";
        } else if (user.hasRole("FACULTY")) {
            target = "/faculty/dashboard";
        } else if (user.hasRole("PMU_NOIDA")) {
            target = "/pmu/noida/dashboard";
        } else if (user.hasRole("PMU_MOHALI")) {
            target = "/pmu/mohali/dashboard";
        } else {
            // default छात्र डैशबोर्ड
            target = "/student/dashboard";
        }

        new DefaultRedirectStrategy().sendRedirect(request, response, target);
    }
}
