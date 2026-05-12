package com.ahmed.bookmark.service;

import com.ahmed.bookmark.dto.request.BookmarkRequest;
import com.ahmed.bookmark.dto.response.BookmarkResponse;
import com.ahmed.bookmark.dto.response.CategoryResponse;
import com.ahmed.bookmark.dto.response.PageResponse;
import com.ahmed.bookmark.dto.response.TagResponse;
import com.ahmed.bookmark.entity.Bookmark;
import com.ahmed.bookmark.entity.Category;
import com.ahmed.bookmark.entity.Tag;
import com.ahmed.bookmark.entity.User;
import com.ahmed.bookmark.exception.ResourceNotFoundException;
import com.ahmed.bookmark.exception.UnauthorizedAccessException;
import com.ahmed.bookmark.repository.BookmarkRepository;
import com.ahmed.bookmark.repository.CategoryRepository;
import com.ahmed.bookmark.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Service responsible for all bookmark operations
// Handles CRUD, soft delete, search, filtering by category and tag, and pagination
@Service
@RequiredArgsConstructor
public class BookmarkService {

    // Repository for bookmark database operations
    private final BookmarkRepository bookmarkRepository;

    // Repository for validating category ownership during bookmark creation
    private final CategoryRepository categoryRepository;

    // Repository for fetching tags during bookmark creation and update
    private final TagRepository tagRepository;

    // Service for fetching the currently authenticated user
    private final UserService userService;

    // Retrieves a paginated list of all non-deleted bookmarks for the current user
    // Supports sorting by any field in ascending or descending order
    public PageResponse<BookmarkResponse> getAllBookmarks(int page, int size, String sortBy, String sortDir) {
        User currentUser = userService.getCurrentUser();
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        Page<Bookmark> bookmarkPage = bookmarkRepository
                .findByUserIdAndIsDeletedFalse(currentUser.getId(), pageable);
        return mapToPageResponse(bookmarkPage);
    }

    // Retrieves a single bookmark by ID
    // Throws ResourceNotFoundException if the bookmark does not exist or is soft deleted
    // Throws UnauthorizedAccessException if the bookmark belongs to another user
    public BookmarkResponse getBookmarkById(Long id) {
        User currentUser = userService.getCurrentUser();
        Bookmark bookmark = findBookmarkOwnedByUser(id, currentUser.getId());
        return mapToResponse(bookmark);
    }

    // Creates a new bookmark for the currently authenticated user
    // Optionally links to a category and multiple tags if provided in the request
    @Transactional
    public BookmarkResponse createBookmark(BookmarkRequest request) {
        User currentUser = userService.getCurrentUser();

        // Resolve category if provided — validates that it exists and belongs to the user
        Category category = resolveCategory(request.getCategoryId(), currentUser.getId());

        // Resolve tags if provided — validates that they exist and belong to the user
        List<Tag> tags = resolveTags(request.getTagIds(), currentUser.getId());

        Bookmark bookmark = Bookmark.builder()
                .title(request.getTitle())
                .url(request.getUrl())
                .description(request.getDescription())
                .user(currentUser)
                .category(category)
                .tags(tags)
                .build();

        return mapToResponse(bookmarkRepository.save(bookmark));
    }

    // Updates an existing bookmark
    // Replaces all fields including category and tag associations
    // Throws ResourceNotFoundException if the bookmark does not exist
    // Throws UnauthorizedAccessException if the bookmark belongs to another user
    @Transactional
    public BookmarkResponse updateBookmark(Long id, BookmarkRequest request) {
        User currentUser = userService.getCurrentUser();
        Bookmark bookmark = findBookmarkOwnedByUser(id, currentUser.getId());

        // Resolve updated category and tags
        Category category = resolveCategory(request.getCategoryId(), currentUser.getId());
        List<Tag> tags = resolveTags(request.getTagIds(), currentUser.getId());

        // Update all fields
        bookmark.setTitle(request.getTitle());
        bookmark.setUrl(request.getUrl());
        bookmark.setDescription(request.getDescription());
        bookmark.setCategory(category);
        bookmark.setTags(tags);

        return mapToResponse(bookmarkRepository.save(bookmark));
    }

    // Soft deletes a bookmark by setting isDeleted to true
    // The record remains in the database but is excluded from all queries
    // Throws ResourceNotFoundException if the bookmark does not exist
    // Throws UnauthorizedAccessException if the bookmark belongs to another user
    @Transactional
    public void deleteBookmark(Long id) {
        User currentUser = userService.getCurrentUser();
        Bookmark bookmark = findBookmarkOwnedByUser(id, currentUser.getId());

        // Mark as deleted instead of removing from the database
        bookmark.setIsDeleted(true);
        bookmarkRepository.save(bookmark);
    }

    // Searches bookmarks by keyword in title or URL — case-insensitive
    // Returns paginated results
    public PageResponse<BookmarkResponse> searchBookmarks(
            String keyword, int page, int size, String sortBy, String sortDir) {
        User currentUser = userService.getCurrentUser();
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        Page<Bookmark> bookmarkPage = bookmarkRepository
                .searchByKeyword(currentUser.getId(), keyword, pageable);
        return mapToPageResponse(bookmarkPage);
    }

    // Filters bookmarks by category ID — returns paginated results
    // Throws ResourceNotFoundException if the category does not exist
    public PageResponse<BookmarkResponse> getBookmarksByCategory(
            Long categoryId, int page, int size, String sortBy, String sortDir) {
        User currentUser = userService.getCurrentUser();

        // Validate that the category exists and belongs to the current user
        categoryRepository.findByIdAndUserId(categoryId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + categoryId
                ));

        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        Page<Bookmark> bookmarkPage = bookmarkRepository
                .findByUserIdAndCategoryIdAndIsDeletedFalse(
                        currentUser.getId(), categoryId, pageable);
        return mapToPageResponse(bookmarkPage);
    }

    // Filters bookmarks by tag ID — returns paginated results
    // Throws ResourceNotFoundException if the tag does not exist
    public PageResponse<BookmarkResponse> getBookmarksByTag(
            Long tagId, int page, int size, String sortBy, String sortDir) {
        User currentUser = userService.getCurrentUser();

        // Validate that the tag exists and belongs to the current user
        tagRepository.findByIdAndUserId(tagId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tag not found with id: " + tagId
                ));

        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        Page<Bookmark> bookmarkPage = bookmarkRepository
                .findByUserIdAndTagIdAndIsDeletedFalse(
                        currentUser.getId(), tagId, pageable);
        return mapToPageResponse(bookmarkPage);
    }

    // Helper — finds a non-deleted bookmark by ID and verifies ownership
    private Bookmark findBookmarkOwnedByUser(Long bookmarkId, Long userId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bookmark not found with id: " + bookmarkId
                ));

        // Reject soft-deleted bookmarks as if they don't exist
        if (bookmark.getIsDeleted()) {
            throw new ResourceNotFoundException(
                    "Bookmark not found with id: " + bookmarkId
            );
        }

        // Verify ownership
        if (!bookmark.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException(
                    "You do not have permission to access this bookmark"
            );
        }

        return bookmark;
    }

    // Helper — resolves a category by ID if provided
    // Returns null if no categoryId is provided — bookmark will be uncategorized
    private Category resolveCategory(Long categoryId, Long userId) {
        if (categoryId == null) return null;
        return categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + categoryId
                ));
    }

    // Helper — resolves a list of tags by their IDs
    // Returns empty list if no tagIds are provided
    private List<Tag> resolveTags(List<Long> tagIds, Long userId) {
        if (tagIds == null || tagIds.isEmpty()) return List.of();
        return tagRepository.findByIdInAndUserId(tagIds, userId);
    }

    // Helper — builds a Pageable object from the request parameters
    // Defaults to descending sort if an invalid direction is provided
    private Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }

    // Helper — maps a Page<Bookmark> to a PageResponse<BookmarkResponse>
    private PageResponse<BookmarkResponse> mapToPageResponse(Page<Bookmark> bookmarkPage) {
        List<BookmarkResponse> content = bookmarkPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PageResponse.<BookmarkResponse>builder()
                .content(content)
                .page(bookmarkPage.getNumber())
                .size(bookmarkPage.getSize())
                .totalElements(bookmarkPage.getTotalElements())
                .totalPages(bookmarkPage.getTotalPages())
                .first(bookmarkPage.isFirst())
                .last(bookmarkPage.isLast())
                .build();
    }

    // Helper — maps a Bookmark entity to a BookmarkResponse DTO
    private BookmarkResponse mapToResponse(Bookmark bookmark) {

        // Map category to CategoryResponse if present
        CategoryResponse categoryResponse = null;
        if (bookmark.getCategory() != null) {
            categoryResponse = CategoryResponse.builder()
                    .id(bookmark.getCategory().getId())
                    .name(bookmark.getCategory().getName())
                    .description(bookmark.getCategory().getDescription())
                    .createdAt(bookmark.getCategory().getCreatedAt())
                    .updatedAt(bookmark.getCategory().getUpdatedAt())
                    .build();
        }

        // Map tags to TagResponse list
        List<TagResponse> tagResponses = bookmark.getTags()
                .stream()
                .map(tag -> TagResponse.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .createdAt(tag.getCreatedAt())
                        .updatedAt(tag.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return BookmarkResponse.builder()
                .id(bookmark.getId())
                .title(bookmark.getTitle())
                .url(bookmark.getUrl())
                .description(bookmark.getDescription())
                .category(categoryResponse)
                .tags(tagResponses)
                .createdAt(bookmark.getCreatedAt())
                .updatedAt(bookmark.getUpdatedAt())
                .build();
    }
}