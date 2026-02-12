package com.postech.fiap.fase5.api.usecases.estimativa.converter;

import com.postech.fiap.fase5.api.dto.estimativa.HistoricoConsumoMesAnosAnterioresDTO;
import com.postech.fiap.fase5.api.repositories.projections.HistoricoConsumoMesAnosAnterioresProjection;
import org.springframework.stereotype.Component;

@Component
public class HistoricoConsumoMensalConverter {

    public HistoricoConsumoMesAnosAnterioresDTO toDTO(HistoricoConsumoMesAnosAnterioresProjection projection) {
        return HistoricoConsumoMesAnosAnterioresDTO.builder()
                .ano(projection.getAno())
                .idPontoDispensacao(projection.getIdPontoDispensacao())
                .idInsumo(projection.getIdInsumo())
                .mes(projection.getMes())
                .totalConsumo(projection.getTotalConsumo())
                .build();
    }
}

