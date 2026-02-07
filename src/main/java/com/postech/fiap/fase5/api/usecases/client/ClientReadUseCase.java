package com.postech.fiap.fase5.api.usecases.client;

import com.postech.fiap.fase5.api.entities.Client;
import com.postech.fiap.fase5.api.repositories.ClientRepository;
import com.postech.fiap.fase5.infrastructure.exceptions.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientReadUseCase {

    private final ClientRepository clientRepository;

    public Client execute(String clientId) {
        return clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));
    }
}
