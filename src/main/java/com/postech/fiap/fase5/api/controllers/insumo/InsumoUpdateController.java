package com.postech.fiap.fase5.api.controllers.insumo;

import com.postech.fiap.fase5.api.controllers.InsumoControllerSwagger;
import com.postech.fiap.fase5.api.dto.InsumoDTO;
import com.postech.fiap.fase5.api.usecases.InsumoUpdateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/insumos")
@RequiredArgsConstructor
public class InsumoUpdateController implements InsumoControllerSwagger {

    private final InsumoUpdateUseCase updateUseCase;

    @Operation(summary = "Atualizar insumo existente")
    @PutMapping("/{id}")
    public ResponseEntity<InsumoDTO> update(@PathVariable Long id, @RequestBody InsumoDTO insumoDTO) {
        return ResponseEntity.ok(updateUseCase.execute(id, insumoDTO));
    }
}
