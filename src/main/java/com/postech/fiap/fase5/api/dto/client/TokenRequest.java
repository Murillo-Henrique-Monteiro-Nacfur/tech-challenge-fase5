package com.postech.fiap.fase5.api.dto.client;


public record TokenRequest(
        String clientId,
        String clientSecret,
        String grantType
) {}
