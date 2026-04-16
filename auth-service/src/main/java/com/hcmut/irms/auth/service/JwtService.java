package com.hcmut.irms.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final KeyPair keyPair;

    // Spring automatically injects the KeyPair from your RsaKeyConfig
    public JwtService(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public String generateToken(String username, String role) {
        return Jwts.builder()
                // 1. Add custom claims safely one by one
                .claim("role", role)
                // 2. Set standard claims
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                // 3. Sign it
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic()) // Verify the signature!
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            return !extractAllClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            // If the signature is fake or the token is expired, JJWT throws an exception.
            return false;
        }
    }
}