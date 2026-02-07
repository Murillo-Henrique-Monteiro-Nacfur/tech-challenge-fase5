package com.postech.fiap.fase5.api.usecases.authentication;

import com.postech.fiap.fase5.api.dto.client.TokenRequest;
import com.postech.fiap.fase5.api.dto.client.TokenResponse;
import com.postech.fiap.fase5.api.entities.Client;
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

    private static final int TOKEN_EXPIRES_IN_SECONDS = 3600;
    private static final String INVALID_CLIENT_CREDENTIALS_MESSAGE = "Invalid client_id or client_secret";

    private final ClientReadUseCase clientReadUseCase;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final List<ClientAuthenticationValidation> validations;

    public TokenResponse execute(TokenRequest request) {
        validateRequest(request);
        Client client = findClientByClientId(request);
        verifyClientCredentials(request, client);
        return generateTokenResponse(client);
    }

    private void validateRequest(TokenRequest request) {
        validations.forEach(validation -> validation.validate(request));
    }

    private Client findClientByClientId(TokenRequest request) {
        return clientReadUseCase.execute(request.clientId());
    }

    private void verifyClientCredentials(TokenRequest request, Client client) {
        boolean credentialsMatch = passwordEncoder.matches(request.clientSecret(), client.getClientSecret());

        if (!credentialsMatch) {
            throw new IllegalArgumentException(INVALID_CLIENT_CREDENTIALS_MESSAGE);
        }
    }

    private TokenResponse generateTokenResponse(Client client) {
        String token = jwtService.generateTokenForClient(client.getClientId(), client.getScopes());
        return new TokenResponse(token, TOKEN_EXPIRES_IN_SECONDS);
    }
}
