package com.librario.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    // Generate JWT token
    public String generateJwtToken(String username) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());  // Using HMAC SHA key
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // Set expiration time
                .signWith(key, SignatureAlgorithm.HS512)  // Updated signing key
                .compact();
    }

    // Extract username from JWT
    public String getUserNameFromJwtToken(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());  // Using HMAC SHA key
        return Jwts.parserBuilder()
                .setSigningKey(key)  // Updated parsing method
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Validate JWT token
    public boolean validateJwtToken(String authToken) {
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());  // Using HMAC SHA key
            Jwts.parserBuilder()
                    .setSigningKey(key)  // Updated parsing method
                    .build()
                    .parseClaimsJws(authToken); // Token validation
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}