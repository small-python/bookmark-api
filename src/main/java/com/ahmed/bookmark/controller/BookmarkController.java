package com.ahmed.bookmark.controller;

import com.ahmed.bookmark.dto.request.BookmarkRequest;
import com.ahmed.bookmark.dto.response.BookmarkResponse;
import com.ahmed.bookmark.dto.response.PageResponse;
import com.ahmed.bookmark.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Handles all bookmark endpoints
// All endpoints require a valid JWT token
@RestController
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
@Tag(name = "Bookmarks", description = "Endpoints for managing bookmarks")
@SecurityRequirement(name = "bearerAuth")
public class BookmarkController {

    // Service that handles all bookmark business logic
    private final BookmarkService bookmarkService;

    // Retrieves a paginated list of all bookmarks for the current user
    // Supports sorting by any field in ascending or descending order
    // GET /api/v1/bookmarks?page=0&size=20&sortBy=createdAt&sortDir=desc
    @GetMapping
    @Operation(
            summary = "Get all bookmarks",
            description = "Returns a paginated list of all bookmarks for the current user"
    )
    public ResponseEntity<PageResponse<BookmarkResponse>> getAllBookmarks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        return ResponseEntity.ok(
                bookmarkService.getAllBookmarks(page, size, sortBy, sortDir));
    }

    // Retrieves a single bookmark by ID
    // GET /api/v1/bookmarks/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Get bookmark by ID", description = "Returns a single bookmark by its ID")
    public ResponseEntity<BookmarkResponse> getBookmarkById(@PathVariable Long id) {
        return ResponseEntity.ok(bookmarkService.getBookmarkById(id));
    }

    // Creates a new bookmark for the currently authenticated user
    // POST /api/v1/bookmarks
    @PostMapping
    @Operation(summary = "Create bookmark", description = "Creates a new bookmark for the current user")
    public ResponseEntity<BookmarkResponse> createBookmark(
            @Valid @RequestBody BookmarkRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookmarkService.createBookmark(request));
    }

    // Updates an existing bookmark by ID
    // PUT /api/v1/bookmarks/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Update bookmark", description = "Updates an existing bookmark by its ID")
    public ResponseEntity<BookmarkResponse> updateBookmark(
            @PathVariable Long id,
            @Valid @RequestBody BookmarkRequest request) {
        return ResponseEntity.ok(bookmarkService.updateBookmark(id, request));
    }

    // Soft deletes a bookmark by ID
    // The record is not removed from the database — isDeleted is set to true
    // DELETE /api/v1/bookmarks/{id}
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete bookmark",
            description = "Soft deletes a bookmark by its ID — the record is hidden but not removed"
    )
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long id) {
        bookmarkService.deleteBookmark(id);
        return ResponseEntity.noContent().build();
    }

    // Searches bookmarks by keyword in title or URL — case insensitive
    // GET /api/v1/bookmarks/search?keyword=github&page=0&size=20
    @GetMapping("/search")
    @Operation(
            summary = "Search bookmarks",
            description = "Searches bookmarks by keyword in title or URL"
    )
    public ResponseEntity<PageResponse<BookmarkResponse>> searchBookmarks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        return ResponseEntity.ok(
                bookmarkService.searchBookmarks(keyword, page, size, sortBy, sortDir));
    }

    // Filters bookmarks by category ID with pagination
    // GET /api/v1/bookmarks/category/{categoryId}?page=0&size=20
    @GetMapping("/category/{categoryId}")
    @Operation(
            summary = "Get bookmarks by category",
            description = "Returns paginated bookmarks filtered by category ID"
    )
    public ResponseEntity<PageResponse<BookmarkResponse>> getBookmarksByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        return ResponseEntity.ok(
                bookmarkService.getBookmarksByCategory(categoryId, page, size, sortBy, sortDir));
    }

    // Filters bookmarks by tag ID with pagination
    // GET /api/v1/bookmarks/tag/{tagId}?page=0&size=20
    @GetMapping("/tag/{tagId}")
    @Operation(
            summary = "Get bookmarks by tag",
            description = "Returns paginated bookmarks filtered by tag ID"
    )
    public ResponseEntity<PageResponse<BookmarkResponse>> getBookmarksByTag(
            @PathVariable Long tagId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        return ResponseEntity.ok(
                bookmarkService.getBookmarksByTag(tagId, page, size, sortBy, sortDir));
    }
}