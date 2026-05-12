package com.ahmed.bookmark.repository;

import com.ahmed.bookmark.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// Marks this as a Spring-managed repository bean
// JpaRepository provides built-in CRUD methods for the Bookmark entity
@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    // Retrieves all non-deleted bookmarks for a specific user with pagination
    // is_deleted = false ensures soft-deleted bookmarks are never returned
    Page<Bookmark> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);

    // Retrieves all non-deleted bookmarks for a specific user filtered by category
    Page<Bookmark> findByUserIdAndCategoryIdAndIsDeletedFalse(
            Long userId, Long categoryId, Pageable pageable);

    // Searches non-deleted bookmarks by title or URL containing the keyword
    // Case-insensitive search using LOWER() so "Java" matches "java" and "JAVA"
    @Query("""
            SELECT b FROM Bookmark b
            WHERE b.user.id = :userId
            AND b.isDeleted = false
            AND (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(b.url) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Bookmark> searchByKeyword(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable);

    // Searches non-deleted bookmarks by tag ID with pagination
    @Query("""
            SELECT b FROM Bookmark b
            JOIN b.tags t
            WHERE b.user.id = :userId
            AND b.isDeleted = false
            AND t.id = :tagId
            """)
    Page<Bookmark> findByUserIdAndTagIdAndIsDeletedFalse(
            @Param("userId") Long userId,
            @Param("tagId") Long tagId,
            Pageable pageable);
}