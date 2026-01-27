package com.postech.fiap.fase5.api.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenRequest(
        String clientId,
        String clientSecret,
        String grantType
) {}
