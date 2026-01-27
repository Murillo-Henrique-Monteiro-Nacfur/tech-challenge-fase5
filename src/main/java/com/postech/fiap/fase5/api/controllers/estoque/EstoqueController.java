package com.postech.fiap.fase5.api.controllers.estoque;

import com.postech.fiap.fase5.api.dto.CargaEstoqueDTO;
import com.postech.fiap.fase5.api.entities.Client;
import com.postech.fiap.fase5.api.repositories.ClientRepository;
import com.postech.fiap.fase5.api.usecases.ProcessarCargaEstoqueUseCase;
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
@RequestMapping("/estoque")
@RequiredArgsConstructor
public class EstoqueController implements EstoqueControllerSwagger {

    private final ProcessarCargaEstoqueUseCase processarCargaEstoqueUseCase;
    private final ClientRepository clientRepository;
    private final AuthenticatedClientProvider authenticatedClientProvider;

    @Operation(summary = "Receber carga de estoque (Bulk Upsert)")
    @PostMapping("/carga")
    public ResponseEntity<Void> receberCarga(@RequestBody CargaEstoqueDTO cargaDTO) {
        String clientIdString = authenticatedClientProvider.getCurrentClientId();

        // Buscar o ID interno do cliente
        Client client = clientRepository.findByClientId(clientIdString)
                .orElseThrow(() -> new SecurityException("Cliente não encontrado: " + clientIdString));

        // Processar a carga
        processarCargaEstoqueUseCase.execute(cargaDTO, client.getId());

        return ResponseEntity.ok().build();
    }
}
