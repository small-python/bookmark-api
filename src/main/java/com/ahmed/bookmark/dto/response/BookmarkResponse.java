package com.ahmed.bookmark.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

// DTO returned to the client when bookmark data is requested
// Excludes internal fields like user_id and is_deleted
@Getter
@Builder
@AllArgsConstructor
public class BookmarkResponse {

    // Unique identifier of the bookmark
    private Long id;

    // Title the user gave to the bookmark
    private String title;

    // The actual URL being bookmarked
    private String url;

    // Optional notes attached to the bookmark
    private String description;

    // Category the bookmark belongs to — null if uncategorized
    private CategoryResponse category;

    // List of tags attached to this bookmark — empty list if no tags
    private List<TagResponse> tags;

    // Timestamps for when the bookmark was created and last updated
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}