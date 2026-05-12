package com.ahmed.bookmark.repository;

import com.ahmed.bookmark.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Marks this as a Spring-managed repository bean
// JpaRepository provides built-in CRUD methods for the Tag entity
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    // Retrieves all tags belonging to a specific user
    List<Tag> findByUserId(Long userId);

    // Finds a specific tag by its ID and the owner's user ID
    // Prevents users from accessing tags that belong to other users
    Optional<Tag> findByIdAndUserId(Long id, Long userId);

    // Checks if a tag with the given name already exists for a specific user
    // Used during creation to prevent duplicate tag names per user
    boolean existsByNameAndUserId(String name, Long userId);

    // Finds all tags by their IDs and the owner's user ID
    // Used when assigning multiple tags to a bookmark at once
    List<Tag> findByIdInAndUserId(List<Long> ids, Long userId);
}