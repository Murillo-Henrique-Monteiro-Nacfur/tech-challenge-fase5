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
        var dados = dadosEstimativaUseCase.executeMensal();

        var resultado = calculadoraPrevisaoMensalUseCase.execute(dados);

        notificacaoService.notificarPrevisaoMensal(resultado);

        return resultado;
    }
}
