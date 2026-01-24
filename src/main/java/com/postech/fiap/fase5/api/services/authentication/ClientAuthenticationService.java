package com.postech.fiap.fase5.api.services.authentication;

import com.postech.fiap.fase5.api.dto.TokenRequest;
import com.postech.fiap.fase5.api.dto.TokenResponse;
import com.postech.fiap.fase5.api.entities.Client;
import com.postech.fiap.fase5.api.repositories.ClientRepository;
import com.postech.fiap.fase5.infrastructure.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientAuthenticationService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public TokenResponse authenticate(TokenRequest request) {
        if (!"client_credentials".equals(request.grantType())) {
            throw new IllegalArgumentException("Invalid grant_type");
        }

        Client client = clientRepository.findByClientId(request.clientId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid client_id or client_secret"));

        if (!passwordEncoder.matches(request.clientSecret(), client.getClientSecret())) {
            throw new IllegalArgumentException("Invalid client_id or client_secret");
        }

        String token = jwtService.generateTokenForClient(client.getClientId(), client.getScopes());
        return new TokenResponse(token, 3600);
    }
}
