package com.ahmed.bookmark.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// DTO for creating and updating categories
// Same DTO is reused for both create and update operations
@Getter
@Setter
public class CategoryRequest {

    // Category name is required — cannot be blank
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    private String name;

    // Description is optional — no validation constraints needed
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
}