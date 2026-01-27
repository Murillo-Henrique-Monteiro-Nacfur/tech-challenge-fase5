package com.postech.fiap.fase5.api.controllers.pontodispensacao;

import com.postech.fiap.fase5.api.dto.PontoDispensacaoDTO;
import com.postech.fiap.fase5.api.usecases.pontodispensacao.PontoDispensacaoReadUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pontos-dispensacao")
@RequiredArgsConstructor
public class PontoDispensacaoReadController implements PontoDispensacaoControllerSwagger {

    private final PontoDispensacaoReadUseCase readUseCase;

    @Operation(summary = "Listar todos os pontos de dispensação paginados")
    @GetMapping
    public ResponseEntity<Page<PontoDispensacaoDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(readUseCase.findAll(pageable));
    }

    @Operation(summary = "Buscar ponto de dispensação por ID")
    @GetMapping("/{id}")
    public ResponseEntity<PontoDispensacaoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(readUseCase.findById(id));
    }
}
