package com.ahmed.bookmark.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

// Generic wrapper DTO for paginated responses
// Uses a type parameter T so it can wrap any response type (BookmarkResponse, CategoryResponse etc.)
@Getter
@Builder
@AllArgsConstructor
public class PageResponse<T> {

    // The actual list of items for the current page
    private List<T> content;

    // Current page number — zero-based internally but can be displayed as 1-based in the UI
    private int page;

    // Number of items per page
    private int size;

    // Total number of items across all pages
    private long totalElements;

    // Total number of pages available
    private int totalPages;

    // Indicates whether this is the last page
    private boolean last;

    // Indicates whether this is the first page
    private boolean first;
}