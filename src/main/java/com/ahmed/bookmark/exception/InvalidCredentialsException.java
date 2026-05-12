package com.ahmed.bookmark.exception;

// Thrown when authentication fails — wrong email or password during login
// Maps to HTTP 401 Unauthorized
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}