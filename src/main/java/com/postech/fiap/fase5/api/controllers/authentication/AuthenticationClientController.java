package com.postech.fiap.fase5.api.controllers.authentication;

import com.postech.fiap.fase5.api.dto.client.TokenRequest;
import com.postech.fiap.fase5.api.dto.client.TokenResponse;
import com.postech.fiap.fase5.api.usecases.authentication.ClientAuthenticationUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class AuthenticationClientController {

    private final ClientAuthenticationUseCase clientAuthenticationUseCase;

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> tokenJson(@RequestBody @Valid TokenRequest request) {
        return ResponseEntity.ok(clientAuthenticationUseCase.execute(request));
    }

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<TokenResponse> tokenForm(
            @RequestParam(value = "client_id", required = false) String clientId,
            @RequestParam(value = "client_secret", required = false) String clientSecret,
            @RequestParam(value = "grant_type") String grantType,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader
    ) {
        if (clientId == null && authHeader != null && authHeader.startsWith("Basic ")) {
            String base64Credentials = authHeader.substring("Basic ".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            final String[] values = credentials.split(":", 2);
            if (values.length == 2) {
                clientId = values[0];
                clientSecret = values[1];
            }
        }

        if (clientId == null || clientSecret == null) {
            throw new IllegalArgumentException("client_id and client_secret are required");
        }

        TokenRequest request = new TokenRequest(clientId, clientSecret, grantType);
        return ResponseEntity.ok(clientAuthenticationUseCase.execute(request));
    }
}
