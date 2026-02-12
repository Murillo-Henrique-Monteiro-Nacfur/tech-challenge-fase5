package com.postech.fiap.fase5.api.usecases.previsao.processadores;

import com.postech.fiap.fase5.api.dto.estimativa.HistoricoConsumoPorDiaDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InsumoDiarioDTO;
import com.postech.fiap.fase5.api.usecases.previsao.avaliadores.AvaliadorStatusPrevisao;
import com.postech.fiap.fase5.api.usecases.previsao.calculadores.CalculadorConsumoMedio;
import com.postech.fiap.fase5.api.usecases.previsao.calculadores.CalculadorDiasEsgotamento;
import com.postech.fiap.fase5.api.usecases.previsao.filtros.FiltroHistoricoRecente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.postech.fiap.fase5.api.usecases.previsao.constantes.ConstantesPrevisao.STATUS_SEM_DADOS;
import static com.postech.fiap.fase5.api.usecases.previsao.constantes.ConstantesPrevisao.VALOR_SEM_PREVISAO;

@Component
@RequiredArgsConstructor
public class ProcessadorPrevisaoInsumo {

    private final FiltroHistoricoRecente filtroHistoricoRecente;
    private final CalculadorConsumoMedio calculadorConsumoMedio;
    private final CalculadorDiasEsgotamento calculadorDiasEsgotamento;
    private final AvaliadorStatusPrevisao avaliadorStatusPrevisao;

    public void processar(InsumoDiarioDTO insumo, List<HistoricoConsumoPorDiaDTO> historicoTotal) {
        List<HistoricoConsumoPorDiaDTO> historicoInsumo = filtroHistoricoRecente.filtrarPorInsumo(
                historicoTotal,
                insumo.getIdInsumo()
        );

        if (historicoInsumo.isEmpty()) {
            definirComoSemDados(insumo);
            return;
        }

        double consumoMedioDiario = calculadorConsumoMedio.calcular(historicoInsumo);
        int diasRestantes = calculadorDiasEsgotamento.calcular(insumo.getQuantidade(), consumoMedioDiario);
        String status = avaliadorStatusPrevisao.avaliar(consumoMedioDiario, diasRestantes);

        aplicarResultados(insumo, consumoMedioDiario, diasRestantes, status);
    }

    private void definirComoSemDados(InsumoDiarioDTO insumo) {
        insumo.setConsumoMedioDiario(0.0);
        insumo.setPrevisaoEsgotamentoDias(VALOR_SEM_PREVISAO);
        insumo.setStatusPrevisao(STATUS_SEM_DADOS);
    }

    private void aplicarResultados(InsumoDiarioDTO insumo, double consumoMedioDiario, int diasRestantes, String status) {
        insumo.setConsumoMedioDiario(consumoMedioDiario);
        insumo.setPrevisaoEsgotamentoDias(diasRestantes);
        insumo.setStatusPrevisao(status);
    }
}

