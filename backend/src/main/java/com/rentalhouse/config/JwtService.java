package com.rentalhouse.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.expiration}") private long expiration;

    public String generateToken(UserDetails user) {
        long now = System.currentTimeMillis();
        return Jwts.builder().subject(user.getUsername()).issuedAt(new Date(now))
                .expiration(new Date(now + expiration)).signWith(signingKey()).compact();
    }

    public String username(String token) { return claims(token).getSubject(); }

    public boolean valid(String token, UserDetails user) {
        return username(token).equals(user.getUsername()) && claims(token).getExpiration().after(new Date());
    }

    private Claims claims(String token) {
        return Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey signingKey() { return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); }
}