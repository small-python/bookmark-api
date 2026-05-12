package com.ahmed.bookmark.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// DTO for user registration requests
// Contains only the fields a client needs to send to create an account
@Getter
@Setter
public class RegisterRequest {

    // Full name is required — cannot be blank
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    // Email must be a valid format and is required
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    // Password must meet the following requirements:
    // - At least 8 characters
    // - At least one uppercase letter (A-Z)
    // - At least one digit (0-9)
    // - At least one special character from the standard accepted set
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$",
            message = "Password must contain at least one uppercase letter, one digit, and one special character"
    )
    private String password;
}