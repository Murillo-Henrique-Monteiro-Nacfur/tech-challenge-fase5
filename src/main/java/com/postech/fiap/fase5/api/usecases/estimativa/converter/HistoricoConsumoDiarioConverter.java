package com.postech.fiap.fase5.api.usecases.estimativa.converter;

import com.postech.fiap.fase5.api.dto.estimativa.HistoricoConsumoPorDiaDTO;
import com.postech.fiap.fase5.api.repositories.projections.HistoricoConsumoPorDiaProjection;
import org.springframework.stereotype.Component;

@Component
public class HistoricoConsumoDiarioConverter {

    public HistoricoConsumoPorDiaDTO toDTO(HistoricoConsumoPorDiaProjection projection) {
        return HistoricoConsumoPorDiaDTO.builder()
                .dia(projection.getDia())
                .idPontoDispensacao(projection.getIdPontoDispensacao())
                .idInsumo(projection.getIdInsumo())
                .totalConsumo(projection.getTotalConsumo())
                .build();
    }
}

