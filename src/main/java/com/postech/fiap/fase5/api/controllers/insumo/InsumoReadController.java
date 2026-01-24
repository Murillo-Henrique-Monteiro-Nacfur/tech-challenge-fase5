package com.postech.fiap.fase5.api.controllers.insumo;

import com.postech.fiap.fase5.api.controllers.InsumoControllerSwagger;
import com.postech.fiap.fase5.api.dto.InsumoDTO;
import com.postech.fiap.fase5.api.usecases.InsumoReadUseCase;
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
@RequestMapping("/insumos")
@RequiredArgsConstructor
public class InsumoReadController implements InsumoControllerSwagger {

    private final InsumoReadUseCase readUseCase;

    @Operation(summary = "Listar todos os insumos paginados")
    @GetMapping
    public ResponseEntity<Page<InsumoDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(readUseCase.findAll(pageable));
    }

    @Operation(summary = "Buscar insumo por ID")
    @GetMapping("/{id}")
    public ResponseEntity<InsumoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(readUseCase.findById(id));
    }
}
