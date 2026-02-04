package com.postech.fiap.fase5.api.usecases.previsao;

import com.postech.fiap.fase5.api.dto.estimativa.InventarioMensalDTO;
import com.postech.fiap.fase5.api.services.NotificacaoService;
import com.postech.fiap.fase5.api.usecases.DadosEstimativaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrevisaoMensalUseCase {

    private final DadosEstimativaUseCase dadosEstimativaUseCase;
    private final CalculadoraPrevisaoMensalUseCase calculadoraPrevisaoMensalUseCase;
    private final NotificacaoService notificacaoService;

    public List<InventarioMensalDTO> execute() {
        // 1. Busca dados brutos otimizados para o mensal
        var dados = dadosEstimativaUseCase.executeMensal();

        // 2. Aplica inteligência mensal
        var resultado = calculadoraPrevisaoMensalUseCase.execute(dados);
        
        // 3. Notifica responsáveis se houver risco sazonal
        notificacaoService.notificarPrevisaoMensal(resultado);
        
        return resultado;
    }
}
