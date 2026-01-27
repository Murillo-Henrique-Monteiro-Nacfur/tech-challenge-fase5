package com.postech.fiap.fase5.api.usecases.client;

import com.postech.fiap.fase5.api.entities.Client;
import com.postech.fiap.fase5.api.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientReadUseCase {

    private final ClientRepository clientRepository;

    public Client findByClientId(String clientId) {
        return clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid client_id or client_secret"));
    }
}
