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

	public SecurityConfig(SecurityCustomUserDetailService userDetailService,
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
				// Public pages and API endpoints
				.requestMatchers("/home/test", "/home/about", "/home/register", "/home/services", "/home/do-register")
				.permitAll()
				.requestMatchers("/home/login", "/home/authenticate").permitAll()
				.requestMatchers("/static/**", "templates/**", "/css/**", "/js/**", "/images/**").permitAll()
				.requestMatchers("/access-denied").permitAll()
				.requestMatchers("/login/oauth2/code/google").permitAll()
				.requestMatchers("/auth/forgot-password", "/auth/reset-password").permitAll()
				.requestMatchers("/course-resources/upload/**").permitAll()
				// Allow access to college API endpoints
				.requestMatchers("/api/college/**").permitAll()
				
				// Resource center dashboard accessible only to RESOURCE_CENTER users
				.requestMatchers("/center/dashboard/**").hasAnyRole("RESOURCE_CENTER","STUDENT")
				// Student dashboard accessible only to STUDENT users
				.requestMatchers("/student/dashboard/**").hasRole("STUDENT")
				// Admin dashboard accessible only to ADMIN users
				.requestMatchers("/user/dashboard/**").hasRole("ADMIN")
				// Attendance endpoints
				.requestMatchers("/attendance/upload", "/attendance/faculty/view").hasRole("FACULTY")
				.requestMatchers("/attendance/student/course/**").hasRole("STUDENT")
				// Class Attendance endpoints
				.requestMatchers("/class-attendance/**").hasRole("FACULTY")
				// Other endpoints
				.requestMatchers("/user/**").authenticated()
				.requestMatchers("/admin/**", "/assign", "/course/create/**").hasRole("ADMIN")
				.requestMatchers("/assign/assignrole").hasRole("ADMIN")
				.requestMatchers("/faculty/**").hasRole("FACULTY")
				.requestMatchers("/meity/**").hasRole("MEITY")
				.requestMatchers("/pmu/**").hasAnyRole("PMU_MOHALI", "PMU_NOIDA", "ADMIN")
				.requestMatchers("/pmu/pmu-delhi").hasRole("PMU_MOHALI")
				.requestMatchers("/co-pmu/**").hasRole("CO_PMU")
				.requestMatchers("/rc/**").hasRole("RESOURCE_CENTER")
				.requestMatchers("/course-schedules/create").hasRole("ADMIN")
				.requestMatchers("/resource-center-dashboard").hasAnyRole("RESOURCE_CENTER","PMU_MOHALI","ADMIN", "PMU_NOIDA")

				// Any other request should be authenticated
				.anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/home/login")
						.loginProcessingUrl("/home/authenticate")
						.successHandler(new CustomAuthenticationSuccessHandler())
						.failureUrl("/home/login?error=true")
						.usernameParameter("email")
						.passwordParameter("password")
						.failureHandler(authFailureHandler))
				.logout(logout -> logout.logoutUrl("/do-logout")
						.logoutSuccessUrl("/home/login?logout=true"))
				.oauth2Login(oauth -> oauth.loginPage("/home/login")
						.successHandler(oauthAuthHandler)
						.failureUrl("/home/login?error=true"))
				.csrf(csrf -> csrf.disable())
				.exceptionHandling(exception -> exception.accessDeniedPage("/home/access-denied"));

		return httpSecurity.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
