package com.postech.fiap.fase5.api.controllers;

import com.postech.fiap.fase5.api.controllers.pontodispensacao.PontoDispensacaoControllerSwagger;
import com.postech.fiap.fase5.api.dto.estimativa.AlertaValidadeDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioDiarioDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioMensalDTO;
import com.postech.fiap.fase5.api.usecases.previsao.AlertaValidadeUseCase;
import com.postech.fiap.fase5.api.usecases.previsao.PrevisaoDiariaUseCase;
import com.postech.fiap.fase5.api.usecases.previsao.PrevisaoMensalUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teste")
@RequiredArgsConstructor
public class TesteController implements PontoDispensacaoControllerSwagger {

    private final PrevisaoDiariaUseCase previsaoDiariaUseCase;
    private final PrevisaoMensalUseCase previsaoMensalUseCase;
    private final AlertaValidadeUseCase alertaValidadeUseCase;

    @GetMapping("/diaria")
    public ResponseEntity<Void> previsaoDiaria() {
        previsaoDiariaUseCase.execute();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mensal")
    public ResponseEntity<Void> previsaoMensal() {
        previsaoMensalUseCase.execute();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validade")
    public ResponseEntity<Void> alertaValidade() {
        alertaValidadeUseCase.execute();
        return ResponseEntity.ok().build();
    }
}
