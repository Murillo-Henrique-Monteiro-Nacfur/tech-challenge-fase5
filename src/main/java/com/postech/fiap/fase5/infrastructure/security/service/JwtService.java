package com.postech.fiap.fase5.infrastructure.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtEncoder jwtEncoder;

    public String generateTokenForClient(String clientId, String scopes) {
        Instant instant = Instant.now();
        long expirationTime = 3600000L; // 1 hour

        var claims = JwtClaimsSet.builder()
                .subject(clientId)
                .issuer("tech-challenge-fase5-service")
                .issuedAt(instant)
                .expiresAt(instant.plusSeconds(expirationTime))
                .claim("scope", scopes)
                .claim("client_id", clientId)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
