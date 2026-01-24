package com.postech.fiap.fase5.api.controllers.insumo;

import com.postech.fiap.fase5.api.dto.insumos.InsumoDTO;
import com.postech.fiap.fase5.api.usecases.insumo.InsumoCreateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/insumos")
@RequiredArgsConstructor
public class InsumoCreateController implements InsumoControllerSwagger {

    private final InsumoCreateUseCase createUseCase;

    @Operation(summary = "Cadastrar novo insumo")
    @PostMapping
    public ResponseEntity<InsumoDTO> create(@RequestBody InsumoDTO insumoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createUseCase.execute(insumoDTO));
    }
}
