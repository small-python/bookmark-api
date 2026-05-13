package com.ahmed.bookmark.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

// Configures the OpenAPI documentation shown in Swagger UI
// Defines the API metadata and the JWT bearer token security scheme
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Bookmark API",
                version = "1.0",
                description = "A RESTful API for managing bookmarks with categories and tags. " +
                        "Built with Java 21 and Spring Boot. " +
                        "Register an account, login to get a JWT token, " +
                        "then click Authorize to test protected endpoints.",
                contact = @Contact(
                        name = "Ahmed Yinusa",
                        email = "yinusaahmed80@gmail.com"
                )
        )
)
// Defines the bearer token security scheme used across all protected endpoints
// This adds the Authorize button to Swagger UI where you paste your JWT token
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Enter your JWT token obtained from the /api/v1/auth/login endpoint"
)
public class OpenApiConfig {
    // No bean definitions needed — annotations handle all the configuration
}