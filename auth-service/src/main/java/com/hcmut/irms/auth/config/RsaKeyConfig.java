package com.hcmut.irms.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

@Configuration
public class RsaKeyConfig {
    private final ResourceLoader resourceLoader;
    private final String keystoreLocation;
    private final String keystorePassword;
    private final String keyAlias;
    private final String keyPassword;

    public RsaKeyConfig(
            ResourceLoader resourceLoader,
            @Value("${auth.jwt.keystore.location}") String keystoreLocation,
            @Value("${auth.jwt.keystore.password}") String keystorePassword,
            @Value("${auth.jwt.keystore.alias}") String keyAlias,
            @Value("${auth.jwt.keystore.key-password}") String keyPassword
    ) {
        this.resourceLoader = resourceLoader;
        this.keystoreLocation = keystoreLocation;
        this.keystorePassword = keystorePassword;
        this.keyAlias = keyAlias;
        this.keyPassword = keyPassword;
    }

    @Bean
    public KeyPair keyPair() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            Resource resource = resourceLoader.getResource(keystoreLocation);
            try (InputStream keyStoreStream = resource.getInputStream()) {
                keyStore.load(keyStoreStream, keystorePassword.toCharArray());
            }

            PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias, keyPassword.toCharArray());
            if (privateKey == null || keyStore.getCertificate(keyAlias) == null) {
                throw new IllegalStateException("Key alias not found in keystore: " + keyAlias);
            }

            PublicKey publicKey = keyStore.getCertificate(keyAlias).getPublicKey();
            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA keys from keystore", e);
        }
    }
}
