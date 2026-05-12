package com.ahmed.bookmark.service;

import com.ahmed.bookmark.entity.User;
import com.ahmed.bookmark.exception.ResourceNotFoundException;
import com.ahmed.bookmark.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

// Service responsible for user-related operations
// Primarily used by other services to fetch the currently authenticated user
@Service
@RequiredArgsConstructor
public class UserService {

    // Repository for fetching user records from the database
    private final UserRepository userRepository;

    // Retrieves the currently authenticated user from the security context
    // The security context holds the authentication set by JwtAuthenticationFilter
    // Throws ResourceNotFoundException if the user no longer exists in the database
    public User getCurrentUser() {

        // Get the authentication object from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Extract the email — stored as the principal name in the JWT token
        String email = authentication.getName();

        // Fetch and return the full user entity from the database
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found with email: " + email
                ));
    }
}