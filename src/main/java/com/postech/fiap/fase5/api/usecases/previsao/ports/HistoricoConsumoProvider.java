package com.postech.fiap.fase5.api.usecases.previsao.ports;

import com.postech.fiap.fase5.api.repositories.HistoricoConsumoRepository;
import com.postech.fiap.fase5.api.repositories.projections.HistoricoConsumoPorDiaProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HistoricoConsumoProvider {

    private static final int PERIODO_HISTORICO_DIAS = 30;

    private final HistoricoConsumoRepository historicoConsumoRepository;

    public List<HistoricoConsumoPorDiaProjection> buscarHistoricoRecente(LocalDateTime dataReferencia) {
        LocalDateTime dataInicio = dataReferencia.minusDays(PERIODO_HISTORICO_DIAS);
        LocalDateTime dataFim = dataReferencia.plusDays(1);

        return historicoConsumoRepository.buscaHistoricoPorDiaNosUltimosTrintaDias(dataInicio, dataFim);
    }
}

