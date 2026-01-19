package com.example.task_management_system.auth;

import com.example.task_management_system.user.Role;
import com.example.task_management_system.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMinutes;

    public JwtService(@Value("${security.jwt.secret}") String secret,
                      @Value("${security.jwt.expiration-minutes}") long expMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expMinutes * 60_000;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();

        return Jwts.builder().subject(user.getEmail()).claim("firstName", user.getFirstName())
                   .claim("lastName", user.getLastName())
                   .claim("roles", user.getRoles().stream().map(Role::getName).toList()).issuedAt(Date.from(now))
                   .expiration(Date.from(now.plusMillis(expirationMinutes))).signWith(key).compact();
    }

    public String validateAndGetSubject(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        return claims.getSubject();
    }
}
