package com.postech.fiap.fase5.api.usecases.previsao.calculadores;

import com.postech.fiap.fase5.api.dto.estimativa.HistoricoConsumoPorDiaDTO;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.postech.fiap.fase5.api.usecases.previsao.constantes.ConstantesPrevisao.PERIODO_ANALISE_DIAS;

@Component
public class CalculadorConsumoMedio {

    public double calcular(List<HistoricoConsumoPorDiaDTO> historicoInsumo) {
        if (historicoInsumo.isEmpty()) {
            return 0.0;
        }

        long totalConsumido = historicoInsumo.stream()
                .mapToLong(h -> h.getTotalConsumo() != null ? h.getTotalConsumo() : 0L)
                .sum();

        return (double) totalConsumido / PERIODO_ANALISE_DIAS;
    }
}

