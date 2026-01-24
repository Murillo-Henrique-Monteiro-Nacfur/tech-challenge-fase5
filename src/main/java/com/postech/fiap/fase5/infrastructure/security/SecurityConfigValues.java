package com.project.email_expenses_service.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@Slf4j
public class SecurityConfigValues {

    private static final String BEGIN_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----";
    private static final String END_PUBLIC_KEY = "-----END PUBLIC KEY-----";
    private static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";
    private static final String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";
    private static final String RSA = "RSA";

    @Value("${app.security.private-key}")
    private String privateKeyLocationOrContent;

    @Value("${app.security.public-key}")
    private String publicKeyLocationOrContent;

    private final ResourceLoader resourceLoader;

    public SecurityConfigValues(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public RSAPrivateKey getPrivateTokenSecretFile() {
        try {
            String keyString = loadKeyContent(privateKeyLocationOrContent);
            String privateKeyPEM = getKeyPEM(keyString);

            byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
            KeyFactory keyFactory = getInstanceKeyFactory();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load private key", e);
        }
    }

    public RSAPublicKey getPublicTokenSecretFile() {
        try {
            String keyString = loadKeyContent(publicKeyLocationOrContent);
            String publicKeyPEM = getKeyPEM(keyString);

            byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
            KeyFactory keyFactory = getInstanceKeyFactory();
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load public key", e);
        }
    }

    private String getKeyPEM(String keyString) {
        return keyString
                .replace(BEGIN_PUBLIC_KEY, "")
                .replace(END_PUBLIC_KEY, "")
                .replace(BEGIN_PRIVATE_KEY, "")
                .replace(END_PRIVATE_KEY, "")
                .replaceAll("\\s", "");
    }

    private KeyFactory getInstanceKeyFactory() throws NoSuchAlgorithmException {
        return KeyFactory.getInstance(RSA);
    }

    private String loadKeyContent(String locationOrContent) throws IOException {
        if (locationOrContent.trim().startsWith("-----")) {
            return locationOrContent;
        }
        Resource resource = resourceLoader.getResource(locationOrContent);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }
}
