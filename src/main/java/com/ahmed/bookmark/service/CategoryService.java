package com.ahmed.bookmark.service;

import com.ahmed.bookmark.dto.request.CategoryRequest;
import com.ahmed.bookmark.dto.response.CategoryResponse;
import com.ahmed.bookmark.entity.Category;
import com.ahmed.bookmark.entity.User;
import com.ahmed.bookmark.exception.DuplicateResourceException;
import com.ahmed.bookmark.exception.ResourceNotFoundException;
import com.ahmed.bookmark.exception.UnauthorizedAccessException;
import com.ahmed.bookmark.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Service responsible for all category operations
// Enforces ownership rules — users can only access and modify their own categories
@Service
@RequiredArgsConstructor
public class CategoryService {

    // Repository for category database operations
    private final CategoryRepository categoryRepository;

    // Service for fetching the currently authenticated user
    private final UserService userService;

    // Retrieves all categories belonging to the currently authenticated user
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        User currentUser = userService.getCurrentUser();
        return categoryRepository.findByUserId(currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Retrieves a single category by ID
    // Throws ResourceNotFoundException if the category does not exist
    // Throws UnauthorizedAccessException if the category belongs to another user
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        User currentUser = userService.getCurrentUser();
        Category category = findCategoryOwnedByUser(id, currentUser.getId());
        return mapToResponse(category);
    }

    // Creates a new category for the currently authenticated user
    // Throws DuplicateResourceException if a category with the same name already exists
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        User currentUser = userService.getCurrentUser();

        // Prevent duplicate category names for the same user
        if (categoryRepository.existsByNameAndUserId(request.getName(), currentUser.getId())) {
            throw new DuplicateResourceException(
                    "A category with name '" + request.getName() + "' already exists"
            );
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .user(currentUser)
                .build();

        return mapToResponse(categoryRepository.save(category));
    }

    // Updates an existing category
    // Throws ResourceNotFoundException if the category does not exist
    // Throws UnauthorizedAccessException if the category belongs to another user
    // Throws DuplicateResourceException if the new name conflicts with an existing category
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        User currentUser = userService.getCurrentUser();
        Category category = findCategoryOwnedByUser(id, currentUser.getId());

        // Check for duplicate name only if the name is actually changing
        if (!category.getName().equals(request.getName()) &&
                categoryRepository.existsByNameAndUserId(request.getName(), currentUser.getId())) {
            throw new DuplicateResourceException(
                    "A category with name '" + request.getName() + "' already exists"
            );
        }

        // Update the fields
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return mapToResponse(categoryRepository.save(category));
    }

    // Deletes a category by ID
    // Bookmarks in this category will have their category set to null — not deleted
    // Throws ResourceNotFoundException if the category does not exist
    // Throws UnauthorizedAccessException if the category belongs to another user
    @Transactional
    public void deleteCategory(Long id) {
        User currentUser = userService.getCurrentUser();
        Category category = findCategoryOwnedByUser(id, currentUser.getId());
        categoryRepository.delete(category);
    }

    // Helper — finds a category by ID and verifies the current user owns it
    // Separates the not-found case from the unauthorized case for clear error messages
    private Category findCategoryOwnedByUser(Long categoryId, Long userId) {

        // First check if the category exists at all
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + categoryId
                ));

        // Then check if the current user owns it
        if (!category.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException(
                    "You do not have permission to access this category"
            );
        }

        return category;
    }

    // Maps a Category entity to a CategoryResponse DTO
    // Counts only non-deleted bookmarks for the bookmark count
    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .bookmarkCount((int) category.getBookmarks()
                        .stream()
                        .filter(b -> !b.getIsDeleted())
                        .count())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}