package com.postech.fiap.fase5.infrastructure.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedClientProvider {

    /**
     * Extrai o client_id do contexto de segurança atual.
     * Suporta autenticação via JWT (Client Credentials).
     *
     * @return O client_id (String) do cliente autenticado.
     * @throws SecurityException se não houver autenticação válida.
     */
    public String getCurrentClientId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Nenhum usuário/cliente autenticado.");
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            // No fluxo Client Credentials, o 'sub' é o client_id.
            // Também adicionamos um claim 'client_id' explícito no JwtService.
            // Vamos preferir o claim explícito se existir, senão o subject.
            if (jwt.hasClaim("client_id")) {
                return jwt.getClaimAsString("client_id");
            }
            return jwt.getSubject();
        }

        // Fallback para outros tipos de autenticação (ex: testes, Basic Auth)
        return authentication.getName();
    }
}
