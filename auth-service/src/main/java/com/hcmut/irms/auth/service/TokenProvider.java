package com.hcmut.irms.auth.service;

public interface TokenProvider {
    String generateToken(String username, String role);

    String extractUsername(String token);

    String extractRole(String token);
}
