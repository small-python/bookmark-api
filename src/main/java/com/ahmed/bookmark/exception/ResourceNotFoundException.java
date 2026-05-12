package com.ahmed.bookmark.exception;

// Thrown when a requested resource does not exist in the database
// Example: fetching a bookmark by ID that does not exist
// Maps to HTTP 404 Not Found
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}