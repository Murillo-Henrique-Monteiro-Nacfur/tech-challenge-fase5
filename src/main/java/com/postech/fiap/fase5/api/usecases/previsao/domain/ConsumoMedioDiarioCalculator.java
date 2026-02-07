package com.postech.fiap.fase5.api.usecases.previsao.domain;

import com.postech.fiap.fase5.api.repositories.projections.HistoricoConsumoPorDiaProjection;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ConsumoMedioDiarioCalculator {

    private static final int PERIODO_CALCULO_DIAS = 30;

    public Map<Long, Map<Long, Double>> calcularMediasPorPontoEInsumo(
            List<HistoricoConsumoPorDiaProjection> historico) {

        Map<Long, Map<Long, Long>> consumoTotal = agruparConsumoTotal(historico);

        return consumoTotal.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> calcularMediasPorInsumo(entry.getValue())
                ));
    }

    private Map<Long, Map<Long, Long>> agruparConsumoTotal(List<HistoricoConsumoPorDiaProjection> historico) {
        return historico.stream()
                .collect(Collectors.groupingBy(
                        HistoricoConsumoPorDiaProjection::getIdPontoDispensacao,
                        Collectors.groupingBy(
                                HistoricoConsumoPorDiaProjection::getIdInsumo,
                                Collectors.summingLong(h -> h.getTotalConsumo() != null ? h.getTotalConsumo() : 0)
                        )
                ));
    }

    private Map<Long, Double> calcularMediasPorInsumo(Map<Long, Long> consumoPorInsumo) {
        return consumoPorInsumo.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() / (double) PERIODO_CALCULO_DIAS
                ));
    }

    public double obterMediaDiaria(Map<Long, Double> mediaPorInsumo, Long idInsumo) {
        return mediaPorInsumo.getOrDefault(idInsumo, 0.0);
    }
}

