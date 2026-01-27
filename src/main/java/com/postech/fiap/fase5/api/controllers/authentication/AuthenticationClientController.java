package com.postech.fiap.fase5.api.controllers.authentication;

import com.postech.fiap.fase5.api.dto.client.TokenRequest;
import com.postech.fiap.fase5.api.dto.client.TokenResponse;
import com.postech.fiap.fase5.api.usecases.authentication.ClientAuthenticationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class AuthenticationClientController {

    private final ClientAuthenticationUseCase clientAuthenticationUseCase;

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> token(@RequestBody TokenRequest request) {
        return ResponseEntity.ok(clientAuthenticationUseCase.execute(request));
    }
}
