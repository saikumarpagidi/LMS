package com.lms.cdac.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.lms.cdac.services.impl.SecurityCustomUserDetailService;

@Configuration
public class SecurityConfig {

    private final SecurityCustomUserDetailService userDetailService;
    private final AuthenticationFailureHandler authFailureHandler;
    private final AuthenticationSuccessHandler oauthAuthHandler;

    public SecurityConfig(
            SecurityCustomUserDetailService userDetailService,
            AuthenticationFailureHandler authFailureHandler,
            @Qualifier("oauthAuthHandler") AuthenticationSuccessHandler oauthAuthHandler) {
        this.userDetailService = userDetailService;
        this.authFailureHandler = authFailureHandler;
        this.oauthAuthHandler = oauthAuthHandler;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                        "/home/test", "home/courses", "/home/about", "/home/oneDayProgram", "/home/course/ai-healthcare-intro", "/home/register", "/home/services", "/home/do-register",
                        "/home/login", "/home/authenticate",
                        "/static/**", "/templates/**", "/css/**", "/js/**", "/images/**",
                        "/home/access-denied",  // fixed path for access-denied
                        "/login/oauth2/code/google",
                        "/auth/forgot-password", "/auth/reset-password",
                        "/course-resources/upload/**",
                        "/api/college/**","/auth/...  "
                )
                .permitAll()

                .requestMatchers("/center/dashboard/**").hasAnyRole("RESOURCE_CENTER","STUDENT")
                .requestMatchers("/student/dashboard", "/student/dashboard/**").hasRole("STUDENT")
                .requestMatchers("/user/dashboard/**").hasRole("ADMIN")
                .requestMatchers("/attendance/upload", "/attendance/faculty/view").hasRole("FACULTY")
                .requestMatchers("/attendance/student/course/**").hasRole("STUDENT")
                .requestMatchers("/class-attendance/**").hasRole("FACULTY")
                .requestMatchers("/admin/**", "/assign", "/course/create/**").hasRole("ADMIN")
                .requestMatchers("/assign/assignrole").hasRole("ADMIN")
                .requestMatchers("/live-classes/**").hasRole("ADMIN")
                .requestMatchers("/faculty/**").hasRole("FACULTY")
                .requestMatchers("/meity/**").hasRole("MEITY")
                .requestMatchers("/pmu/**").hasAnyRole("PMU_MOHALI", "PMU_NOIDA", "ADMIN")
                .requestMatchers("/pmu/pmu-delhi").hasRole("PMU_MOHALI")
                .requestMatchers("/co-pmu/**").hasRole("CO_PMU")
                .requestMatchers("/rc/**").hasRole("RESOURCE_CENTER")
                .requestMatchers("/course-schedules/create").hasRole("ADMIN")
                .requestMatchers("/resource-center-dashboard").hasAnyRole(
                        "RESOURCE_CENTER", "PMU_MOHALI", "ADMIN", "PMU_NOIDA"
                )
                .anyRequest().authenticated()
        )
        .formLogin(form -> form
                .loginPage("/home/login")
                .loginProcessingUrl("/home/authenticate")
                .successHandler(new com.lms.cdac.config.CustomAuthenticationSuccessHandler())
                .failureUrl("/home/login?error=true")
                .usernameParameter("email")
                .passwordParameter("password")
                .failureHandler(authFailureHandler)
        )
        .logout(logout -> logout
                .logoutUrl("/do-logout")
                .logoutSuccessUrl("/home/login?logout=true")
                .invalidateHttpSession(true)                 // ✨ destroys session
                .clearAuthentication(true)                   // ✨ clears authentication object
                .deleteCookies("JSESSIONID")  
        )
        .oauth2Login(oauth -> oauth
                .loginPage("/home/login")
                .successHandler(oauthAuthHandler)
                .failureUrl("/home/login?error=true")
        )
        .csrf(csrf -> csrf.disable())
        .exceptionHandling(exception -> exception.accessDeniedPage("/home/access-denied"));

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}