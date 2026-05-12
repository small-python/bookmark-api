package com.ahmed.bookmark.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Marks this class as a JPA entity mapped to the "bookmarks" table
@Entity
@Table(name = "bookmarks")
// Lombok annotations — generate getters, setters, constructors, and builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bookmark {

    // Primary key — auto-incremented by the database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Title the user gives to the bookmark — required
    @Column(nullable = false, length = 200)
    private String title;

    // The actual URL being bookmarked — stored as TEXT to handle long URLs
    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    // Optional notes the user wants to attach to the bookmark
    @Column(columnDefinition = "TEXT")
    private String description;

    // Many bookmarks belong to one user
    // LAZY loading — user data is not fetched unless explicitly accessed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Many bookmarks can belong to one category — nullable since category is optional
    // LAZY loading — category data is not fetched unless explicitly accessed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // Many-to-many relationship with tags via the bookmark_tags join table
    // Bookmark owns this relationship — manages inserts and deletes on the join table
    @ManyToMany
    @JoinTable(
            name = "bookmark_tags",
            joinColumns = @JoinColumn(name = "bookmark_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();

    // Soft delete flag — true means the bookmark is hidden but still in the database
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    // Automatically set when the record is first created
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Automatically updated every time the record is modified
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}