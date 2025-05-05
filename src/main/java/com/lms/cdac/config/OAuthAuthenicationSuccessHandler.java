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
public class OAuthAuthenicationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger =
        LoggerFactory.getLogger(OAuthAuthenicationSuccessHandler.class);

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

        String email = oauthUser.getAttribute("email");
        String name  = oauthUser.getAttribute("name");
        if (email == null || name == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                               "Missing email or name");
            return;
        }

        // 1) find-or-create JPA User
        User user = userService.findByEmail(email);
        if (user == null) {
            logger.info("Creating new OAuth user: {}", email);
            user = new User();
            user.setEmail(email);
            user.setName(name);                   // correct setter
            user.setEnabled(true);
            user.setEmailVerified(true);
            user.setProvider(Providers.GOOGLE);
            user.setProviderUserId(oauthUser.getName());
            user.setPassword(UUID.randomUUID().toString());

            // satisfy NOT NULL constraint on phone_number
            user.setPhoneNumber("");                 

            user = userService.saveUser(user);
        }

        // 2) ensure at least STUDENT role
        if (user.getAssignedRoles().isEmpty()) {
            userService.assignRole(user.getUserId(), "STUDENT");
            logger.info("Assigned STUDENT role to {}", email);
        }

        // 3) reload user (to pick up roles/fields)
        user = userService.findByEmail(email);

        // 4) build authorities
        List<SimpleGrantedAuthority> authorities = user.getAssignedRoles().stream()
            .map(ar -> new SimpleGrantedAuthority(
                "ROLE_" + ar.getRoleUser().getRoleName().toUpperCase()))
            .collect(Collectors.toList());

        // 5) swap Authentication principal to your JPA User
        UsernamePasswordAuthenticationToken newAuth =
            new UsernamePasswordAuthenticationToken(user, null, authorities);

        // 6) store in SecurityContext & HTTP session
        SecurityContext sc = SecurityContextHolder.createEmptyContext();
        sc.setAuthentication(newAuth);
        request.getSession().setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            sc
        );
        SecurityContextHolder.setContext(sc);

        logger.info("User {} authenticated with roles {}",
                    user.getEmail(), authorities);

        // 7) conditional redirect:
        //    if missing college or phoneNumber, go to complete-profile
        String target = (user.getCollege() == null || user.getPhoneNumber().isBlank())
                      ? "/complete-profile"
                      : "/student/dashboard";
        new DefaultRedirectStrategy()
            .sendRedirect(request, response, target);
    }
}
