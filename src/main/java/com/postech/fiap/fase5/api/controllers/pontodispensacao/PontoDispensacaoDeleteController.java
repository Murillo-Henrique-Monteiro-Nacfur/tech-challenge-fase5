package com.postech.fiap.fase5.api.controllers.pontodispensacao;

import com.postech.fiap.fase5.api.usecases.pontodispensacao.PontoDispensacaoDeleteUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pontos-dispensacao")
@RequiredArgsConstructor
public class PontoDispensacaoDeleteController implements PontoDispensacaoControllerSwagger {

    private final PontoDispensacaoDeleteUseCase deleteUseCase;

    @Operation(summary = "Excluir ponto de dispensação")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
