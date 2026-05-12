package com.ahmed.bookmark.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// DTO for creating and updating tags
// Same DTO is reused for both create and update operations
@Getter
@Setter
public class TagRequest {

    // Tag name is required — cannot be blank
    @NotBlank(message = "Tag name is required")
    @Size(max = 50, message = "Tag name cannot exceed 50 characters")
    private String name;
}