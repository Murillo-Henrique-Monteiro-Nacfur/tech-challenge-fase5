package com.postech.fiap.fase5.api.usecases.client;

import com.postech.fiap.fase5.api.entities.Client;
import com.postech.fiap.fase5.api.repositories.ClientRepository;
import com.postech.fiap.fase5.infrastructure.exceptions.ClientNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientReadUseCaseTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientReadUseCase clientReadUseCase;

    @Test
    void deveBuscarClientePorClientIdComSucesso() {
        String clientId = "test-client-id";
        Client expectedClient = createClient(1L, clientId, "Test Client", "read write");

        when(clientRepository.findByClientId(clientId)).thenReturn(Optional.of(expectedClient));

        Client result = clientReadUseCase.execute(clientId);

        assertNotNull(result);
        assertEquals(expectedClient.getId(), result.getId());
        assertEquals(expectedClient.getClientId(), result.getClientId());
        assertEquals(expectedClient.getName(), result.getName());
        assertEquals(expectedClient.getScopes(), result.getScopes());

        verify(clientRepository).findByClientId(clientId);
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        String clientId = "non-existent-client";

        when(clientRepository.findByClientId(clientId)).thenReturn(Optional.empty());

        ClientNotFoundException exception = assertThrows(
                ClientNotFoundException.class,
                () -> clientReadUseCase.execute(clientId)
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains(clientId));

        verify(clientRepository).findByClientId(clientId);
    }

    @Test
    void deveRetornarClienteComTodasAsInformacoes() {
        String clientId = "full-client";
        Client client = Client.builder()
                .id(10L)
                .clientId(clientId)
                .clientSecret("secret-hash")
                .name("Full Client Name")
                .scopes("read write delete admin")
                .build();

        when(clientRepository.findByClientId(clientId)).thenReturn(Optional.of(client));

        Client result = clientReadUseCase.execute(clientId);

        assertEquals(10L, result.getId());
        assertEquals(clientId, result.getClientId());
        assertEquals("secret-hash", result.getClientSecret());
        assertEquals("Full Client Name", result.getName());
        assertEquals("read write delete admin", result.getScopes());
    }

    @Test
    void deveConsultarRepositorioApenasumaVez() {
        String clientId = "single-call-client";
        Client client = createClient(1L, clientId, "Client", "read");

        when(clientRepository.findByClientId(clientId)).thenReturn(Optional.of(client));

        clientReadUseCase.execute(clientId);

        verify(clientRepository, times(1)).findByClientId(clientId);
    }

    @Test
    void deveLancarExcecaoComMensagemApropriada() {
        String clientId = "missing-client-123";

        when(clientRepository.findByClientId(clientId)).thenReturn(Optional.empty());

        ClientNotFoundException exception = assertThrows(
                ClientNotFoundException.class,
                () -> clientReadUseCase.execute(clientId)
        );

        assertTrue(exception.getMessage().contains(clientId),
                "Exception message should contain the client ID");
    }

    private Client createClient(Long id, String clientId, String name, String scopes) {
        return Client.builder()
                .id(id)
                .clientId(clientId)
                .clientSecret("encrypted-secret")
                .name(name)
                .scopes(scopes)
                .build();
    }
}

