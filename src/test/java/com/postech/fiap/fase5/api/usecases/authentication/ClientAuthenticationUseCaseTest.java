package com.postech.fiap.fase5.api.usecases.authentication;

import com.postech.fiap.fase5.api.dto.client.TokenRequest;
import com.postech.fiap.fase5.api.dto.client.TokenResponse;
import com.postech.fiap.fase5.api.entities.Client;
import com.postech.fiap.fase5.api.validations.client.ClientAuthenticationValidation;
import com.postech.fiap.fase5.infrastructure.security.service.ClientUserDetails;
import com.postech.fiap.fase5.infrastructure.security.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientAuthenticationUseCaseTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private ClientAuthenticationValidation validation;

    @Mock
    private Authentication authentication;

    @Mock
    private ClientUserDetails clientUserDetails;

    private ClientAuthenticationUseCase clientAuthenticationUseCase;

    @BeforeEach
    void setUp() {
        clientAuthenticationUseCase = new ClientAuthenticationUseCase(
                authenticationManager,
                jwtService,
                List.of(validation)
        );
    }

    @Test
    void deveAutenticarClienteComSucesso() {
        TokenRequest request = createTokenRequest("client-id", "client-secret", "client_credentials");
        Client client = createClient("client-id", "read write");
        String expectedToken = "jwt-token-12345";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(clientUserDetails);
        when(clientUserDetails.getClient()).thenReturn(client);
        when(jwtService.generateTokenForClient("client-id", "read write")).thenReturn(expectedToken);

        TokenResponse response = clientAuthenticationUseCase.execute(request);

        assertNotNull(response);
        assertEquals(expectedToken, response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600, response.expiresIn());

        verify(validation).validate(request);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateTokenForClient("client-id", "read write");
    }

    @Test
    void deveExecutarTodasValidacoesAntesDeAutenticar() {
        ClientAuthenticationValidation validation1 = mock(ClientAuthenticationValidation.class);
        ClientAuthenticationValidation validation2 = mock(ClientAuthenticationValidation.class);
        List<ClientAuthenticationValidation> validations = List.of(validation1, validation2);

        ClientAuthenticationUseCase useCase = new ClientAuthenticationUseCase(
                authenticationManager,
                jwtService,
                validations
        );

        TokenRequest request = createTokenRequest("client-id", "client-secret", "client_credentials");
        Client client = createClient("client-id", "read");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(clientUserDetails);
        when(clientUserDetails.getClient()).thenReturn(client);
        when(jwtService.generateTokenForClient(anyString(), anyString())).thenReturn("token");

        useCase.execute(request);

        verify(validation1).validate(request);
        verify(validation2).validate(request);
    }

    @Test
    void deveLancarExcecaoQuandoCredenciaisInvalidas() {
        TokenRequest request = createTokenRequest("client-id", "wrong-secret", "client_credentials");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> clientAuthenticationUseCase.execute(request));

        verify(validation).validate(request);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateTokenForClient(anyString(), anyString());
    }

    @Test
    void deveLancarExcecaoQuandoValidacaoFalhar() {
        TokenRequest request = createTokenRequest("", "secret", "client_credentials");

        doThrow(new IllegalArgumentException("client_id cannot be empty"))
                .when(validation).validate(request);

        assertThrows(IllegalArgumentException.class, () -> clientAuthenticationUseCase.execute(request));

        verify(validation).validate(request);
        verify(authenticationManager, never()).authenticate(any());
        verify(jwtService, never()).generateTokenForClient(anyString(), anyString());
    }

    @Test
    void deveGerarTokenComScopesCorretos() {
        TokenRequest request = createTokenRequest("client-id", "client-secret", "client_credentials");
        String expectedScopes = "read write admin";
        Client client = createClient("client-id", expectedScopes);
        String expectedToken = "jwt-token-with-scopes";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(clientUserDetails);
        when(clientUserDetails.getClient()).thenReturn(client);
        when(jwtService.generateTokenForClient("client-id", expectedScopes)).thenReturn(expectedToken);

        TokenResponse response = clientAuthenticationUseCase.execute(request);

        assertEquals(expectedToken, response.accessToken());
        verify(jwtService).generateTokenForClient("client-id", expectedScopes);
    }

    @Test
    void deveRetornarTokenComTipoBearer() {
        TokenRequest request = createTokenRequest("client-id", "client-secret", "client_credentials");
        Client client = createClient("client-id", "read");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(clientUserDetails);
        when(clientUserDetails.getClient()).thenReturn(client);
        when(jwtService.generateTokenForClient(anyString(), anyString())).thenReturn("token");

        TokenResponse response = clientAuthenticationUseCase.execute(request);

        assertEquals("Bearer", response.tokenType());
    }

    @Test
    void deveRetornarTokenComExpiracaoDe3600Segundos() {
        TokenRequest request = createTokenRequest("client-id", "client-secret", "client_credentials");
        Client client = createClient("client-id", "read");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(clientUserDetails);
        when(clientUserDetails.getClient()).thenReturn(client);
        when(jwtService.generateTokenForClient(anyString(), anyString())).thenReturn("token");

        TokenResponse response = clientAuthenticationUseCase.execute(request);

        assertEquals(3600, response.expiresIn());
    }

    private TokenRequest createTokenRequest(String clientId, String clientSecret, String grantType) {
        return new TokenRequest(clientId, clientSecret, grantType);
    }

    private Client createClient(String clientId, String scopes) {
        return Client.builder()
                .id(1L)
                .clientId(clientId)
                .clientSecret("encrypted-secret")
                .name("Test Client")
                .scopes(scopes)
                .build();
    }
}

