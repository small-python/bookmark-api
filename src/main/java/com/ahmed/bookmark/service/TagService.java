package com.ahmed.bookmark.service;

import com.ahmed.bookmark.dto.request.TagRequest;
import com.ahmed.bookmark.dto.response.TagResponse;
import com.ahmed.bookmark.entity.Tag;
import com.ahmed.bookmark.entity.User;
import com.ahmed.bookmark.exception.DuplicateResourceException;
import com.ahmed.bookmark.exception.ResourceNotFoundException;
import com.ahmed.bookmark.exception.UnauthorizedAccessException;
import com.ahmed.bookmark.repository.TagRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// Service responsible for all tag operations
// Enforces ownership rules — users can only access and modify their own tags
@Service
@RequiredArgsConstructor
public class TagService {

    // Repository for tag database operations
    private final TagRepository tagRepository;

    // Service for fetching the currently authenticated user
    private final UserService userService;

    // Retrieves all tags belonging to the currently authenticated user
    @Transactional(readOnly = true)
    public List<TagResponse> getAllTags() {
        User currentUser = userService.getCurrentUser();
        return tagRepository.findByUserId(currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Retrieves a single tag by ID
    // Throws ResourceNotFoundException if the tag does not exist
    // Throws UnauthorizedAccessException if the tag belongs to another user
    @Transactional(readOnly = true)
    public TagResponse getTagById(Long id) {
        User currentUser = userService.getCurrentUser();
        Tag tag = findTagOwnedByUser(id, currentUser.getId());
        return mapToResponse(tag);
    }

    // Creates a new tag for the currently authenticated user
    // Throws DuplicateResourceException if a tag with the same name already exists
    @Transactional
    public TagResponse createTag(TagRequest request) {
        User currentUser = userService.getCurrentUser();

        // Prevent duplicate tag names for the same user
        if (tagRepository.existsByNameAndUserId(request.getName(), currentUser.getId())) {
            throw new DuplicateResourceException(
                    "A tag with name '" + request.getName() + "' already exists"
            );
        }

        Tag tag = Tag.builder()
                .name(request.getName())
                .user(currentUser)
                .build();

        return mapToResponse(tagRepository.save(tag));
    }

    // Updates an existing tag
    // Throws ResourceNotFoundException if the tag does not exist
    // Throws UnauthorizedAccessException if the tag belongs to another user
    // Throws DuplicateResourceException if the new name conflicts with an existing tag
    @Transactional
    public TagResponse updateTag(Long id, TagRequest request) {
        User currentUser = userService.getCurrentUser();
        Tag tag = findTagOwnedByUser(id, currentUser.getId());

        // Check for duplicate name only if the name is actually changing
        if (!tag.getName().equals(request.getName()) &&
                tagRepository.existsByNameAndUserId(request.getName(), currentUser.getId())) {
            throw new DuplicateResourceException(
                    "A tag with name '" + request.getName() + "' already exists"
            );
        }

        // Update the tag name
        tag.setName(request.getName());

        return mapToResponse(tagRepository.save(tag));
    }

    // Deletes a tag by ID
    // Removes all associations with bookmarks automatically via cascade on the join table
    // Throws ResourceNotFoundException if the tag does not exist
    // Throws UnauthorizedAccessException if the tag belongs to another user
    @Transactional
    public void deleteTag(Long id) {
        User currentUser = userService.getCurrentUser();
        Tag tag = findTagOwnedByUser(id, currentUser.getId());
        tagRepository.delete(tag);
    }

    // Helper — finds a tag by ID and verifies the current user owns it
    // Separates the not-found case from the unauthorized case for clear error messages
    private Tag findTagOwnedByUser(Long tagId, Long userId) {

        // First check if the tag exists at all
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tag not found with id: " + tagId
                ));

        // Then check if the current user owns it
        if (!tag.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException(
                    "You do not have permission to access this tag"
            );
        }

        return tag;
    }

    // Maps a Tag entity to a TagResponse DTO
    private TagResponse mapToResponse(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .createdAt(tag.getCreatedAt())
                .updatedAt(tag.getUpdatedAt())
                .build();
    }
}