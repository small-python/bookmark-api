package com.ahmed.bookmark.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Intercepts every HTTP request exactly once to validate the JWT token
// Extends OncePerRequestFilter to guarantee single execution per request
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Utility class for JWT operations — extracting email, validating token
    private final JwtUtil jwtUtil;

    // Loads user details from the database by email
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Extract the Authorization header from the incoming request
        String authHeader = request.getHeader("Authorization");

        // Skip JWT processing if the header is missing or does not start with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Strip the "Bearer " prefix to get the raw JWT token string
        String token = authHeader.substring(7);

        // Extract the email from the token to identify the user
        String email = jwtUtil.extractEmail(token);

        // Only authenticate if email was extracted and no authentication exists yet
        // Prevents re-authenticating an already authenticated request
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load the full user details from the database using the extracted email
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Validate the token against the loaded user details
            if (jwtUtil.isTokenValid(token, userDetails.getUsername())) {

                // Create an authentication token with the user's details and authorities
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Attach request details (IP address, session) to the authentication
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Store the authentication in the security context
                // This marks the request as authenticated for the rest of the filter chain
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continue processing the request through the remaining filters
        filterChain.doFilter(request, response);
    }
}