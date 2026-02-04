package com.postech.fiap.fase5.api.usecases.previsao;

import com.postech.fiap.fase5.api.dto.estimativa.InventarioDiarioDTO;
import com.postech.fiap.fase5.api.services.NotificacaoService;
import com.postech.fiap.fase5.api.usecases.DadosEstimativaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrevisaoDiariaUseCase {


    private final DadosEstimativaUseCase dadosEstimativaUseCase;
    private final CalculadoraPrevisaoDiariaUseCase calculadoraPrevisaoDiariaUseCase;
    private final NotificacaoService notificacaoService;


    public List<InventarioDiarioDTO> execute() {
        // 1. Busca dados brutos otimizados para o dia
        var dados = dadosEstimativaUseCase.executeDiaria();

        // 2. Aplica inteligência diária
        var resultado = calculadoraPrevisaoDiariaUseCase.execute(dados);
        
        // 3. Notifica responsáveis se houver risco
        notificacaoService.notificarPrevisaoDiaria(resultado);
        
        return resultado;
    }
}
