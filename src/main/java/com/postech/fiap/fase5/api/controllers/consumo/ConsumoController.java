package com.postech.fiap.fase5.api.controllers.consumo;

import com.postech.fiap.fase5.api.dto.RegistroConsumoDTO;
import com.postech.fiap.fase5.api.entities.Client;
import com.postech.fiap.fase5.api.repositories.ClientRepository;
import com.postech.fiap.fase5.api.usecases.RegistrarConsumoUseCase;
import com.postech.fiap.fase5.infrastructure.security.service.AuthenticatedClientProvider;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/consumo")
@RequiredArgsConstructor
public class ConsumoController implements ConsumoControllerSwagger {

    private final RegistrarConsumoUseCase registrarConsumoUseCase;
    private final ClientRepository clientRepository;
    private final AuthenticatedClientProvider authenticatedClientProvider;

    @Operation(summary = "Registrar consumo de medicamentos")
    @PostMapping
    public ResponseEntity<Void> registrarConsumo(@RequestBody RegistroConsumoDTO dto) {
        String clientIdString = authenticatedClientProvider.getCurrentClientId();

        Client client = clientRepository.findByClientId(clientIdString)
                .orElseThrow(() -> new SecurityException("Cliente não encontrado: " + clientIdString));

        registrarConsumoUseCase.execute(dto, client.getId());

        return ResponseEntity.ok().build();
    }
}
