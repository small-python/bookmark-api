package com.ahmed.bookmark.repository;

import com.ahmed.bookmark.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Marks this as a Spring-managed repository bean
// JpaRepository provides built-in CRUD methods for the Category entity
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Retrieves all categories belonging to a specific user
    List<Category> findByUserId(Long userId);

    // Finds a specific category by its ID and the owner's user ID
    // Prevents users from accessing categories that belong to other users
    Optional<Category> findByIdAndUserId(Long id, Long userId);

    // Checks if a category with the given name already exists for a specific user
    // Used during creation to prevent duplicate category names per user
    boolean existsByNameAndUserId(String name, Long userId);
}