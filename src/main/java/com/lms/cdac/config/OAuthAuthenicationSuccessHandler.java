package com.lms.cdac.config;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;

import com.lms.cdac.entities.Providers;
import com.lms.cdac.entities.User;
import com.lms.cdac.services.impl.UserServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Qualifier("oauthAuthHandler")
public class OAuthAuthenicationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserServiceImpl userService;

    private static final Logger logger = LoggerFactory.getLogger(OAuthAuthenicationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        logger.info("OAuth Authentication Success Handler triggered");

        OAuth2AuthenticationToken oauth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String provider = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();
        logger.info("OAuth provider: {}", provider);

        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();

        // Log all OAuth attributes
        oauthUser.getAttributes().forEach((key, value) -> logger.info("OAuth Attribute - {} : {}", key, value));

        // Extract email and name from OAuth response
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        if (email == null || name == null) {
            logger.error("Email or name attribute not found for the user");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email or name not found in OAuth response");
            return;
        }

        // Check if user already exists
        User user = userService.findByEmail(email);

        if (user == null) {
            // ✅ First-time login -> Save user (No role assignment yet)
            logger.info("Creating new user with email: {}", email);

            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setEnabled(true);
            user.setEmailVerified(true);
            user.setPassword(null); // No password for OAuth users
            user.setProviderUserId(oauthUser.getName()); // Provider user id
            user.setProvider(Providers.GOOGLE); // Google as the provider

            userService.saveUser(user);  // Save user to the database
            logger.info("New user created and saved: {}", email);
        }

        // ✅ Fetch assigned roles (If user already exists)
        List<SimpleGrantedAuthority> authorities = user.getAssignedRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleUser().getRoleName().toUpperCase()))
                .collect(Collectors.toList());

        // ✅ Create authentication token and update SecurityContext
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                oauthUser, null, authorities);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authToken);
        SecurityContextHolder.setContext(securityContext);

        logger.info("User session updated with roles: {}", authorities);

        // ✅ Redirect to dashboard
        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/dashboard");
    }
}

 
