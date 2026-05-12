package com.ahmed.bookmark.repository;

import com.ahmed.bookmark.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Marks this as a Spring-managed repository bean
// JpaRepository provides built-in CRUD methods for the User entity
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Finds a user by their email address — used during login and registration checks
    Optional<User> findByEmail(String email);

    // Checks if a user with the given email already exists — used during registration
    boolean existsByEmail(String email);
}