package com.ahmed.bookmark.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Marks this class as a JPA entity mapped to the "tags" table
@Entity
@Table(name = "tags")
// Lombok annotations — generate getters, setters, constructors, and builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    // Primary key — auto-incremented by the database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tag name — must be unique per user, enforced at DB level
    @Column(nullable = false, length = 50)
    private String name;

    // Many tags belong to one user
    // LAZY loading — user data is not fetched unless explicitly accessed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Many-to-many relationship with bookmarks via the bookmark_tags join table
    // mappedBy means Bookmark owns this relationship
    @ManyToMany(mappedBy = "tags")
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