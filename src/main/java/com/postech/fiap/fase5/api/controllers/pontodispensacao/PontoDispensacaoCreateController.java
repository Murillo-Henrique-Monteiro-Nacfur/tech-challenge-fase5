package com.postech.fiap.fase5.api.controllers.pontodispensacao;

import com.postech.fiap.fase5.api.dto.PontoDispensacaoDTO;
import com.postech.fiap.fase5.api.usecases.pontodispensacao.PontoDispensacaoCreateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pontos-dispensacao")
@RequiredArgsConstructor
public class PontoDispensacaoCreateController implements PontoDispensacaoControllerSwagger {

    private final PontoDispensacaoCreateUseCase createUseCase;

    @Operation(summary = "Cadastrar novo ponto de dispensação")
    @PostMapping
    public ResponseEntity<PontoDispensacaoDTO> create(@RequestBody PontoDispensacaoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createUseCase.execute(dto));
    }
}
