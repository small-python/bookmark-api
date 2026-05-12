package com.ahmed.bookmark.exception;

// Thrown when a client attempts to access or modify a resource they do not own
// Example: trying to delete another user's bookmark
// Maps to HTTP 403 Forbidden
public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}