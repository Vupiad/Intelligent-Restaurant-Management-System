package com.hcmut.irms.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

@Configuration
public class RsaKeyConfig {

    @Bean
    public KeyPair keyPair() {
        try {
            // 1. Load the keystore file from the resources folder
            KeyStore keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = new ClassPathResource("irms-keystore.jks").getInputStream();

            // 2. Unlock it using the password we set in the terminal
            keyStore.load(resourceAsStream, "password123".toCharArray());

            // 3. Extract the Private and Public keys using the alias
            PrivateKey privateKey = (PrivateKey) keyStore.getKey("irms-key", "password123".toCharArray());
            PublicKey publicKey = keyStore.getCertificate("irms-key").getPublicKey();

            // 4. Return the permanent KeyPair
            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load RSA Keys from Keystore", e);
        }
    }
}
