package com.ahmed.bookmark.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// DTO returned to the client when tag data is requested
// Excludes internal fields like user_id — client only needs what is relevant to display
@Getter
@Builder
@AllArgsConstructor
public class TagResponse {

    // Unique identifier of the tag
    private Long id;

    // Tag name
    private String name;

    // Timestamps for when the tag was created and last updated
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}