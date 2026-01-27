package com.postech.fiap.fase5.api.validations.client;

import com.postech.fiap.fase5.api.dto.client.TokenRequest;

public interface ClientAuthenticationValidation {
    void validate(TokenRequest request);
}
