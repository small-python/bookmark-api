package com.ahmed.bookmark.controller;

import com.ahmed.bookmark.dto.request.LoginRequest;
import com.ahmed.bookmark.dto.request.RegisterRequest;
import com.ahmed.bookmark.dto.response.AuthResponse;
import com.ahmed.bookmark.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Handles all authentication endpoints — registration and login
// These endpoints are public — no JWT token required
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    // Service that handles registration and login business logic
    private final AuthService authService;

    // Registers a new user account and returns a JWT token
    // POST /api/v1/auth/register
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new account and returns a JWT token")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    // Authenticates an existing user and returns a JWT token
    // POST /api/v1/auth/login
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user and returns a JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}