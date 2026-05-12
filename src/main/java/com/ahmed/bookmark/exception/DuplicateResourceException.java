package com.ahmed.bookmark.exception;

// Thrown when a client attempts to create a resource that already exists
// Example: registering with an email that is already taken
// Maps to HTTP 409 Conflict
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}