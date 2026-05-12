package com.ahmed.bookmark.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// DTO for user login requests
// Only requires email and password — no other fields needed for authentication
@Getter
@Setter
public class LoginRequest {

    // Email is used as the login identifier
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    // Password is required — validation rules are enforced at registration
    // Login only checks the provided password against the stored hash
    @NotBlank(message = "Password is required")
    private String password;
}