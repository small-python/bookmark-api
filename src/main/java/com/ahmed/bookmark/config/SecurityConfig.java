package com.ahmed.bookmark.config;

import com.ahmed.bookmark.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Marks this as a Spring configuration class
// EnableWebSecurity activates Spring Security's web security support
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // JWT filter that intercepts every request to validate the token
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Service that loads user details from the database
    private final UserDetailsService userDetailsService;

    // Defines the security filter chain — the core of Spring Security configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF — not needed for stateless REST APIs using JWT
                .csrf(AbstractHttpConfigurer::disable)

                // Define which endpoints are public and which require authentication
                .authorizeHttpRequests(auth -> auth
                        // Allow unauthenticated access to registration and login
                        .requestMatchers(
                                "/api/v1/auth/**",
                                // Allow Swagger UI and OpenAPI docs without authentication
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api-docs/**"
                        ).permitAll()
                        // All other endpoints require a valid JWT token
                        .anyRequest().authenticated()
                )

                // Use stateless session — no HTTP session is created or used
                // Every request must carry a JWT token for authentication
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Register the authentication provider
                .authenticationProvider(authenticationProvider())

                // Add JWT filter before the default username/password filter
                // This ensures JWT validation happens first on every request
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // Configures the authentication provider with our UserDetailsService and password encoder
    // DaoAuthenticationProvider fetches user details from the database for authentication
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Exposes the AuthenticationManager bean — used in the login service to authenticate users
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // BCrypt password encoder — hashes passwords before storing them in the database
    // BCrypt is a strong one-way hashing algorithm designed specifically for passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}