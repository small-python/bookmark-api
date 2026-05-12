package com.ahmed.bookmark.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// Spring-managed component that handles all JWT operations
// Responsible for generating, parsing, and validating JWT tokens
@Component
public class JwtUtil {

    // Secret key injected from application.yaml — used to sign and verify tokens
    @Value("${jwt.secret}")
    private String secret;

    // Token expiration time in milliseconds — injected from application.yaml
    @Value("${jwt.expiration}")
    private long expiration;

    // Builds a SecretKey from the plain text secret string
    // HMAC-SHA algorithm requires a key object, not a plain string
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Generates a JWT token for the given email address
    // The email is stored as the subject — used to identify the user on each request
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    // Extracts all claims from a JWT token after verifying its signature
    // Throws an exception if the token is invalid or expired
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Extracts the email (subject) from a JWT token
    // Used to identify which user is making the request
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // Checks whether a JWT token has expired
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Validates the token by checking the email matches and the token is not expired
    public boolean isTokenValid(String token, String email) {
        return extractEmail(token).equals(email) && !isTokenExpired(token);
    }
}