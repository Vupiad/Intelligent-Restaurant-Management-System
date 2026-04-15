package com.hcmut.irms.auth.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/.well-known")
public class JwksController {

    private final KeyPair keyPair;

    public JwksController(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    @GetMapping("/jwks.json")
    public Map<String, Object> getJwks() {
        // Extract specifically the Public Key
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        // Use the Nimbus library to format it perfectly to the OAuth2 industry standard
        RSAKey key = new RSAKey.Builder(publicKey)
                .keyID("irms-auth-key-1") // A unique ID for the API Gateway to cache
                .build();

        // Return it as a JSON object
        return new JWKSet(key).toJSONObject();
    }
}