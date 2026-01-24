package com.postech.fiap.fase5.api.controllers;

import com.postech.fiap.fase5.api.dto.TokenRequest;
import com.postech.fiap.fase5.api.dto.TokenResponse;
import com.postech.fiap.fase5.api.services.authentication.ClientAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final ClientAuthenticationService clientAuthenticationService;

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> token(@RequestBody TokenRequest request) {
        return ResponseEntity.ok(clientAuthenticationService.authenticate(request));
    }
}
