package com.postech.fiap.fase5.api.controllers;

import com.postech.fiap.fase5.api.controllers.pontodispensacao.PontoDispensacaoControllerSwagger;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioDiarioDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioMensalDTO;
import com.postech.fiap.fase5.api.services.CalculadoraPrevisaoService;
import com.postech.fiap.fase5.api.usecases.EstimativaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teste")
@RequiredArgsConstructor
public class TesteController implements PontoDispensacaoControllerSwagger {

    private final EstimativaUseCase estimativaUseCase;
    private final CalculadoraPrevisaoService calculadoraPrevisaoService;

    @GetMapping("/diaria")
    public ResponseEntity<List<InventarioDiarioDTO>> previsaoDiaria() {
        // 1. Busca dados brutos otimizados para o dia
        List<InventarioDiarioDTO> dados = estimativaUseCase.executeDiaria();
        
        // 2. Aplica inteligência diária
        List<InventarioDiarioDTO> resultado = calculadoraPrevisaoService.calcularPrevisaoDiaria(dados);
        
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/mensal")
    public ResponseEntity<List<InventarioMensalDTO>> previsaoMensal() {
        // 1. Busca dados brutos otimizados para o mês
        List<InventarioMensalDTO> dados = estimativaUseCase.executeMensal();
        
        // 2. Aplica inteligência mensal
        List<InventarioMensalDTO> resultado = calculadoraPrevisaoService.calcularPrevisaoMensal(dados);
        
        return ResponseEntity.ok(resultado);
    }
}
