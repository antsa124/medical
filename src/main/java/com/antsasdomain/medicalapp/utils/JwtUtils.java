package com.antsasdomain.medicalapp.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${jwt.secret}") // Load secret from properties
    private String jwtSecret;

    @Value("${jwt.expiration}") // Load expiration from properties
    private long jwtExpirationMs;

    private SecretKey key; // Secure key for signing JWTs

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes()); // Convert secret to a secure Key
    }

    /**
     *  Generate JWT Token for authenticated users
     */
    public String generateToken(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()); // Extract roles

        return Jwts.builder()
                .subject(user.getUsername()) // Set subject
                .claim("roles", roles) // Store user roles
                .issuedAt(new Date()) // Token issued time
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // Expiry time
                .signWith(key) // Sign with secure key
                .compact();
    }

    /**
     * ✅ Extract username from JWT token
     */
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(key) // Verify signature
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * ✅ Extract roles from JWT token
     */
    public List<String> extractRoles(String token) {
        return Jwts.parser()
                .verifyWith(key) // Verify signature
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("roles", List.class);
    }

    /**
     * ✅ Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key) // Verify signature
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false; // Invalid token
        }
    }
}
