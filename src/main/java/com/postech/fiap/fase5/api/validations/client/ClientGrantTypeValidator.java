package com.postech.fiap.fase5.api.validations.client;

import com.postech.fiap.fase5.api.dto.client.TokenRequest;
import org.springframework.stereotype.Component;

@Component
public class ClientGrantTypeValidator implements ClientAuthenticationValidation {

    private static final String CLIENT_CREDENTIALS = "client_credentials";

    @Override
    public void validate(TokenRequest request) {
        if (!CLIENT_CREDENTIALS.equals(request.grantType())) {
            throw new IllegalArgumentException("Invalid grant_type. Expected: client_credentials");
        }
    }
}
