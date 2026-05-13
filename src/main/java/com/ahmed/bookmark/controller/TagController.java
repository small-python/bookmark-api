package com.ahmed.bookmark.controller;

import com.ahmed.bookmark.dto.request.TagRequest;
import com.ahmed.bookmark.dto.response.TagResponse;
import com.ahmed.bookmark.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Handles all tag endpoints
// All endpoints require a valid JWT token
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
@Tag(name = "Tags", description = "Endpoints for managing bookmark tags")
@SecurityRequirement(name = "bearerAuth")
public class TagController {

    // Service that handles all tag business logic
    private final TagService tagService;

    // Retrieves all tags for the currently authenticated user
    // GET /api/v1/tags
    @GetMapping
    @Operation(summary = "Get all tags", description = "Returns all tags belonging to the current user")
    public ResponseEntity<List<TagResponse>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    // Retrieves a single tag by ID
    // GET /api/v1/tags/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Get tag by ID", description = "Returns a single tag by its ID")
    public ResponseEntity<TagResponse> getTagById(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    // Creates a new tag for the currently authenticated user
    // POST /api/v1/tags
    @PostMapping
    @Operation(summary = "Create tag", description = "Creates a new tag for the current user")
    public ResponseEntity<TagResponse> createTag(@Valid @RequestBody TagRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tagService.createTag(request));
    }

    // Updates an existing tag by ID
    // PUT /api/v1/tags/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Update tag", description = "Updates an existing tag by its ID")
    public ResponseEntity<TagResponse> updateTag(
            @PathVariable Long id,
            @Valid @RequestBody TagRequest request) {
        return ResponseEntity.ok(tagService.updateTag(id, request));
    }

    // Deletes a tag by ID
    // DELETE /api/v1/tags/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tag", description = "Deletes a tag by its ID")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}