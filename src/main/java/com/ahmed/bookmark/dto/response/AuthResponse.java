package com.ahmed.bookmark.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// DTO returned to the client after successful registration or login
// Contains the JWT token and basic user information
@Getter
@Builder
@AllArgsConstructor
public class AuthResponse {

    // JWT token the client must include in the Authorization header for all protected requests
    private String token;

    // Basic user details returned alongside the token for convenience
    // Saves the client an extra API call to fetch user info after login
    private Long id;
    private String fullName;
    private String email;
}