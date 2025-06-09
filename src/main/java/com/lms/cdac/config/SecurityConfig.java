package com.lms.cdac.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.lms.cdac.services.impl.SecurityCustomUserDetailService;

@Configuration
public class SecurityConfig {

    private final SecurityCustomUserDetailService userDetailService;
    private final AuthenticationFailureHandler authFailureHandler;
    private final AuthenticationSuccessHandler oauthAuthHandler;
    private final CaptchaFilter captchaFilter;

    public SecurityConfig(
            SecurityCustomUserDetailService userDetailService,
            AuthenticationFailureHandler authFailureHandler,
            @Qualifier("oauthAuthHandler") AuthenticationSuccessHandler oauthAuthHandler,
            CaptchaFilter captchaFilter) {
        this.userDetailService = userDetailService;
        this.authFailureHandler = authFailureHandler;
        this.oauthAuthHandler = oauthAuthHandler;
        this.captchaFilter = captchaFilter;
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
        httpSecurity
            // CAPTCHA filter को UsernamePasswordAuthenticationFilter से पहले लगाएँ
            .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(authorize -> authorize
                // Public URLs जिन्हें बिना लॉगिन के एक्सेस किया जा सकता है
                .requestMatchers(
                        "/",
                        "/home/test", "/home/courses", "/home/about",
                        "/home/oneDayProgram", "/home/course/ai-healthcare-intro",
                        "/home/register", "/home/services", "/home/do-register",
                        "/home/login", "/home/authenticate",
                        "/home/refreshCaptcha",
                        "/static/**", "/templates/**", "/css/**", "/js/**", "/images/**",
                        "/home/access-denied",
                        "/login/oauth2/code/google",
                        "/auth/forgot-password", "/auth/reset-password",
                        "/course-resources/upload/**",
                        "/api/college/**", "/auth/..."
                ).permitAll()

                // Role-based URLs
                .requestMatchers("/center/dashboard/**").hasAnyRole("RESOURCE_CENTER", "STUDENT")
                .requestMatchers("/student/dashboard", "/student/dashboard/**").hasRole("STUDENT")
                .requestMatchers("/user/dashboard/**").hasRole("ADMIN")
                .requestMatchers("/attendance/upload", "/attendance/faculty/view").hasRole("FACULTY")
                .requestMatchers("/attendance/student/course/**").hasRole("STUDENT")
                .requestMatchers("/class-attendance/**").hasRole("FACULTY")
                .requestMatchers("/admin/**", "/assign").hasRole("ADMIN")
                .requestMatchers("/assign/assignrole").hasRole("ADMIN")
                .requestMatchers("/live-classes/**").hasRole("ADMIN")
                .requestMatchers("/analytics/dashboard**").hasRole("ADMIN")

                .requestMatchers("/faculty/**").hasRole("FACULTY")
                .requestMatchers("/meity/**").hasRole("MEITY")

                // PMU या ADMIN को course-schedules/create दोनों GET+POST के लिए allow
                .requestMatchers(
                    "/pmu/**",
                    "/course/create/**",
                    "/course-design/**",
                    "/course-schedules/create/**"
                ).hasAnyRole("PMU_MOHALI", "PMU_NOIDA", "ADMIN")

                .requestMatchers("/pmu/pmu-delhi").hasRole("PMU_MOHALI")
                .requestMatchers("/co-pmu/**").hasRole("CO_PMU")
                .requestMatchers("/rc/**").hasRole("RESOURCE_CENTER")
                .requestMatchers("/resource-center-dashboard").hasAnyRole(
                        "RESOURCE_CENTER", "PMU_MOHALI", "ADMIN", "PMU_NOIDA"
                )

                // बाकी सभी authenticated हों
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/home/login")
                .loginProcessingUrl("/home/authenticate")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(new com.lms.cdac.config.CustomAuthenticationSuccessHandler())
                .failureHandler(authFailureHandler)
            )
            .logout(logout -> logout
                .logoutUrl("/do-logout")
                .logoutSuccessUrl("/home/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
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
