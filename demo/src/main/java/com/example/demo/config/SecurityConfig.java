package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Security Configuration: Configures Spring Security for authentication and authorization
 * 
 * Features:
 * - Form-based authentication
 * - CSRF protection
 * - Access control based on roles
 * - Custom UserDetailsService
 * - Password encoding with BCrypt
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    /**
     * Password encoder: BCrypt is used for secure password hashing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication provider: Uses UserDetailsService and PasswordEncoder
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
}

    /**
     * Security filter chain: Configures HTTP security
     * - Permits public access to registration and verification pages
     * - Requires authentication for library access
     * - Admin pages require ROLE_ADMIN
     * - Form-based login with custom login page
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Configuring Spring Security");

        http
                // Disable CSRF for API endpoints (enable for forms)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )

                // Authorization
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints
                        .requestMatchers("/", "/auth/login", "/auth/register", "/auth/verify", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()

                        // Admin endpoints
                        .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")

                        // User endpoints
                        .requestMatchers("/dashboard/**", "/api/books/**", "/api/issuances/**").hasAnyRole("USER", "ADMIN")

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )

                // Form-based login
                .formLogin(login -> login
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .successHandler((request, response, authentication) -> {
    var authorities = authentication.getAuthorities();

    if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
        response.sendRedirect("/dashboard/admin");
    } else {
        response.sendRedirect("/dashboard");
    }
})
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )

                // Logout
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .invalidateHttpSession(true)
                        .permitAll()
                )

                // Exception handling
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/auth/access-denied")
                );

        return http.build();
    }
}
