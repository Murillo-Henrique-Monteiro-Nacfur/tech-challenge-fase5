package com.postech.fiap.fase5.infrastructure.security;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@Slf4j
@Getter
public class SecurityConfigValues {
    @Value("classpath:security/app.key")
    private RSAPrivateKey privateTokenSecretFile;
    @Value("classpath:security/app.pub")
    private RSAPublicKey publicTokenSecretFile;
}
