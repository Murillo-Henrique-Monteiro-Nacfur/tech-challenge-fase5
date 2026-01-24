package com.project.email_expenses_service.infrastructure.security.service;


import com.project.email_expenses_service.api.dto.authentication.UserAuthenticatedDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtEncoder jwtEncoder;

    public String generateToken(Authentication authentication) {
        Instant instant = Instant.now();
        long expirationTime = 3600000L;
        long userId = 0L;
        String scopes = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        if(authentication.getPrincipal() instanceof UserAuthenticatedDTO userAuthenticatedDTO){
            userId = userAuthenticatedDTO.getId();
        }
        var claims = JwtClaimsSet.builder()
                .subject(authentication.getName())
                .issuer("email-expenses-service")
                .issuedAt(instant)
                .expiresAt(instant.plusSeconds(expirationTime))
                .claim("scope", scopes)
                .claim("userId", userId)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
