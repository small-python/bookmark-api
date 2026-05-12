package com.ahmed.bookmark.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Marks this class as a JPA entity mapped to the "categories" table
@Entity
@Table(name = "categories")
// Lombok annotations — generate getters, setters, constructors, and builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    // Primary key — auto-incremented by the database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Category name — must be unique per user, enforced at DB level
    @Column(nullable = false, length = 100)
    private String name;

    // Optional description of what this category is for
    @Column(length = 255)
    private String description;

    // Many categories belong to one user
    // LAZY loading — user data is not fetched unless explicitly accessed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // One category can have many bookmarks
    // When category is deleted, bookmarks are not deleted — FK is set to null
    @OneToMany(mappedBy = "category")
    @Builder.Default
    private List<Bookmark> bookmarks = new ArrayList<>();

    // Automatically set when the record is first created
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Automatically updated every time the record is modified
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}