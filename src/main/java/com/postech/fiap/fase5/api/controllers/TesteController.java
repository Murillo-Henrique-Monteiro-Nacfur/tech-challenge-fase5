package com.postech.fiap.fase5.api.controllers;

import com.postech.fiap.fase5.api.controllers.pontodispensacao.PontoDispensacaoControllerSwagger;
import com.postech.fiap.fase5.api.dto.PontoDispensacaoDTO;
import com.postech.fiap.fase5.api.usecases.EstimativaUseCase;
import com.postech.fiap.fase5.api.usecases.pontodispensacao.PontoDispensacaoUpdateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teste")
@RequiredArgsConstructor
public class TesteController implements PontoDispensacaoControllerSwagger {

     private final EstimativaUseCase estimativaUseCase;
    @GetMapping
    public ResponseEntity<Object> update() {
        return ResponseEntity.ok(estimativaUseCase.execute());
    }
}
