package com.ahmed.bookmark.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// DTO returned to the client when category data is requested
// Excludes internal fields like user_id — client only needs what is relevant to display
@Getter
@Builder
@AllArgsConstructor
public class CategoryResponse {

    // Unique identifier of the category
    private Long id;

    // Category name
    private String name;

    // Optional description of the category
    private String description;

    // Number of bookmarks in this category — useful for display in the UI
    private int bookmarkCount;

    // Timestamps for when the category was created and last updated
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}