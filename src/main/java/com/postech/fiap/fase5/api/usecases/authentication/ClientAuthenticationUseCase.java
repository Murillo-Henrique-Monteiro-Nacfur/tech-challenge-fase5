package com.postech.fiap.fase5.api.usecases.authentication;

import com.postech.fiap.fase5.api.dto.client.TokenRequest;
import com.postech.fiap.fase5.api.dto.client.TokenResponse;
import com.postech.fiap.fase5.api.entities.Client;
import com.postech.fiap.fase5.api.repositories.ClientRepository;
import com.postech.fiap.fase5.api.usecases.client.ClientReadUseCase;
import com.postech.fiap.fase5.api.validations.client.ClientAuthenticationValidation;
import com.postech.fiap.fase5.infrastructure.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClientAuthenticationUseCase {

    private static final int EXPIRES_IN = 3600;
    public static final String INVALID_CLIENT_ID_OR_CLIENT_SECRET = "Invalid client_id or client_secret";
    private final ClientReadUseCase clientReadUseCase;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final List<ClientAuthenticationValidation> validations;

    public TokenResponse execute(TokenRequest request) {
        validations.forEach(v -> v.validate(request));

        Client client = clientReadUseCase.findByClientId(request.clientId());

        validaCredenciais(request, client);

        String token = jwtService.generateTokenForClient(client.getClientId(), client.getScopes());
        return new TokenResponse(token, EXPIRES_IN);
    }

    private void validaCredenciais(TokenRequest request, Client client) {
        if (!passwordEncoder.matches(request.clientSecret(), client.getClientSecret())) {
            throw new IllegalArgumentException(INVALID_CLIENT_ID_OR_CLIENT_SECRET);
        }
    }
}
