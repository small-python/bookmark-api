package com.ahmed.bookmark.security;

import com.ahmed.bookmark.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring-managed service that loads user details from the database
// Implements UserDetailsService — required by Spring Security for authentication
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    // Repository for fetching user records from the database
    private final UserRepository userRepository;

    // Loads a user by their email address — called by Spring Security during authentication
    // Throws UsernameNotFoundException if no user exists with the given email
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Fetch the user from the database by email
        // If not found, throw an exception with a descriptive message
        return userRepository.findByEmail(email)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities("ROLE_USER")
                        .build()
                )
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email
                ));
    }
}