package com.postech.fiap.fase5.api.dto.client;

public record TokenResponse(
        String accessToken,
        long expiresIn
) {
}
