package com.postech.fiap.fase5.api.dto.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TokenRequest(
        @NotBlank(message = "client_id is required")
        String clientId,

        @NotBlank(message = "client_secret is required")
        String clientSecret,

        @NotBlank(message = "grant_type is required")
        @Pattern(regexp = "client_credentials", message = "grant_type must be 'client_credentials'")
        String grantType
) {}
