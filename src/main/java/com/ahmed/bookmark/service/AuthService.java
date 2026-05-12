package com.ahmed.bookmark.service;

import com.ahmed.bookmark.dto.request.LoginRequest;
import com.ahmed.bookmark.dto.request.RegisterRequest;
import com.ahmed.bookmark.dto.response.AuthResponse;
import com.ahmed.bookmark.entity.User;
import com.ahmed.bookmark.exception.DuplicateResourceException;
import com.ahmed.bookmark.repository.UserRepository;
import com.ahmed.bookmark.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// Service responsible for user registration and login
// Handles credential validation, password hashing, and JWT token generation
@Service
@RequiredArgsConstructor
public class AuthService {

    // Repository for saving and fetching user records
    private final UserRepository userRepository;

    // Encodes plain text passwords before saving to the database
    private final PasswordEncoder passwordEncoder;

    // Utility for generating JWT tokens after successful authentication
    private final JwtUtil jwtUtil;

    // Spring Security's authentication manager — validates credentials during login
    private final AuthenticationManager authenticationManager;

    // Registers a new user account
    // Throws DuplicateResourceException if the email is already in use
    public AuthResponse register(RegisterRequest request) {

        // Check if a user with this email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "An account with email " + request.getEmail() + " already exists"
            );
        }

        // Build and save the new user entity
        // Password is hashed with BCrypt before being stored — never plain text
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        // Generate a JWT token for the newly registered user
        String token = jwtUtil.generateToken(user.getEmail());

        // Return the token and basic user info
        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }

    // Authenticates an existing user and returns a JWT token
    // Throws BadCredentialsException (handled by GlobalExceptionHandler) if credentials are wrong
    public AuthResponse login(LoginRequest request) {

        // Authenticate the user using Spring Security's AuthenticationManager
        // This internally calls UserDetailsServiceImpl.loadUserByUsername()
        // and verifies the password against the stored BCrypt hash
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // If authentication succeeds, fetch the full user entity
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        // Generate a JWT token for the authenticated user
        String token = jwtUtil.generateToken(user.getEmail());

        // Return the token and basic user info
        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }
}