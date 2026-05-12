package com.ahmed.bookmark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Marks this as the entry point of the Spring Boot application
// Enables auto-configuration, component scanning, and configuration properties
@SpringBootApplication
public class BookmarkApiApplication {

	// Main method — bootstraps and launches the entire Spring application context
	public static void main(String[] args) {
		SpringApplication.run(BookmarkApiApplication.class, args);
	}
}