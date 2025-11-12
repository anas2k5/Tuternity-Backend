package com.smarttutor.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final Key SECRET_KEY;
    private final long ACCESS_TOKEN_EXPIRATION;
    private final long REFRESH_TOKEN_EXPIRATION;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationTime
    ) {
        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long");
        }
        this.SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes());
        this.ACCESS_TOKEN_EXPIRATION = expirationTime; // typically 1 hour
        this.REFRESH_TOKEN_EXPIRATION = 604800000L;    // 7 days
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ✅ Access token
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }
        claims.put("role", role);
        return createToken(claims, username, ACCESS_TOKEN_EXPIRATION);
    }

    // ✅ Refresh token
    public String generateRefreshToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }
        claims.put("role", role);
        return createToken(claims, username, REFRESH_TOKEN_EXPIRATION);
    }

    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return extractedUsername.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
