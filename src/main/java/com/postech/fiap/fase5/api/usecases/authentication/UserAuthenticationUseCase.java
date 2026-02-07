package com.postech.fiap.fase5.api.usecases.authentication;

import com.postech.fiap.fase5.infrastructure.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthenticationUseCase {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public String execute(String login, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = createAuthenticationToken(login, password);
        Authentication authenticatedUser = performAuthentication(authenticationToken);
        return generateJwtToken(authenticatedUser);
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(String login, String password) {
        return new UsernamePasswordAuthenticationToken(login, password);
    }

    private Authentication performAuthentication(UsernamePasswordAuthenticationToken authenticationToken) {
        return authenticationManager.authenticate(authenticationToken);
    }

    private String generateJwtToken(Authentication authentication) {
        return jwtService.generateToken(authentication);
    }
}