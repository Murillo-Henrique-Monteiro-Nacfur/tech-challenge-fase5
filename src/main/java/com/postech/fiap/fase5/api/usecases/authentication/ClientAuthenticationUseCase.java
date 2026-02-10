package com.postech.fiap.fase5.api.usecases.authentication;

import com.postech.fiap.fase5.api.dto.client.TokenRequest;
import com.postech.fiap.fase5.api.dto.client.TokenResponse;
import com.postech.fiap.fase5.api.entities.Client;
import com.postech.fiap.fase5.api.validations.client.ClientAuthenticationValidation;
import com.postech.fiap.fase5.infrastructure.security.service.ClientUserDetails;
import com.postech.fiap.fase5.infrastructure.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClientAuthenticationUseCase {

    private static final int TOKEN_EXPIRES_IN_SECONDS = 3600;
    private static final String TOKEN_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final List<ClientAuthenticationValidation> validations;

    public TokenResponse execute(TokenRequest request) {
        validateRequest(request);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.clientId(), request.clientSecret())
        );

        ClientUserDetails userDetails = (ClientUserDetails) authentication.getPrincipal();
        Client client = userDetails.getClient();

        return generateTokenResponse(client);
    }

    private void validateRequest(TokenRequest request) {
        validations.forEach(validation -> validation.validate(request));
    }

    private TokenResponse generateTokenResponse(Client client) {
        String token = jwtService.generateTokenForClient(client.getClientId(), client.getScopes());
        return new TokenResponse(token, TOKEN_TYPE, TOKEN_EXPIRES_IN_SECONDS);
    }
}
