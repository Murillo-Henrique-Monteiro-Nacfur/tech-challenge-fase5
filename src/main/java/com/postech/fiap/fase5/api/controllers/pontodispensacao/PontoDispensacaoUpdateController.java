package com.postech.fiap.fase5.api.controllers.pontodispensacao;

import com.postech.fiap.fase5.api.dto.PontoDispensacaoDTO;
import com.postech.fiap.fase5.api.usecases.pontodispensacao.PontoDispensacaoUpdateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pontos-dispensacao")
@RequiredArgsConstructor
public class PontoDispensacaoUpdateController implements PontoDispensacaoControllerSwagger {

    private final PontoDispensacaoUpdateUseCase updateUseCase;

    @Operation(summary = "Atualizar ponto de dispensação existente")
    @PutMapping("/{id}")
    public ResponseEntity<PontoDispensacaoDTO> update(@PathVariable Long id, @RequestBody PontoDispensacaoDTO dto) {
        return ResponseEntity.ok(updateUseCase.execute(id, dto));
    }
}
