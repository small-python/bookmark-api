package com.ahmed.bookmark.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// DTO for creating and updating bookmarks
// Same DTO is reused for both create and update operations
@Getter
@Setter
public class BookmarkRequest {

    // Bookmark title is required — cannot be blank
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    // URL is required and must follow a valid URL format
    @NotBlank(message = "URL is required")
    @Pattern(
            regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
            message = "URL must be a valid web address starting with http://, https://, or ftp://"
    )
    private String url;

    // Optional description — no length constraint at DTO level since DB stores as TEXT
    private String description;

    // Optional category ID — null means the bookmark has no category
    private Long categoryId;

    // Optional list of tag IDs to attach to the bookmark
    // Defaults to empty list so callers don't need to send an empty array explicitly
    private List<Long> tagIds = new ArrayList<>();
}