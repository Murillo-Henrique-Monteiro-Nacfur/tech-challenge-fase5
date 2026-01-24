package com.postech.fiap.fase5.api.controllers.insumo;

import com.postech.fiap.fase5.api.usecases.insumo.InsumoDeleteUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/insumos")
@RequiredArgsConstructor
public class InsumoDeleteController implements InsumoControllerSwagger {

    private final InsumoDeleteUseCase deleteUseCase;

    @Operation(summary = "Excluir insumo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
