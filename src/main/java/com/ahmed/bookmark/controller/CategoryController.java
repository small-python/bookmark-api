package com.ahmed.bookmark.controller;

import com.ahmed.bookmark.dto.request.CategoryRequest;
import com.ahmed.bookmark.dto.response.CategoryResponse;
import com.ahmed.bookmark.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Handles all category endpoints
// All endpoints require a valid JWT token
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Endpoints for managing bookmark categories")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    // Service that handles all category business logic
    private final CategoryService categoryService;

    // Retrieves all categories for the currently authenticated user
    // GET /api/v1/categories
    @GetMapping
    @Operation(summary = "Get all categories", description = "Returns all categories belonging to the current user")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // Retrieves a single category by ID
    // GET /api/v1/categories/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Returns a single category by its ID")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    // Creates a new category for the currently authenticated user
    // POST /api/v1/categories
    @PostMapping
    @Operation(summary = "Create category", description = "Creates a new category for the current user")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(request));
    }

    // Updates an existing category by ID
    // PUT /api/v1/categories/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Updates an existing category by its ID")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    // Deletes a category by ID
    // DELETE /api/v1/categories/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Deletes a category by its ID")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}