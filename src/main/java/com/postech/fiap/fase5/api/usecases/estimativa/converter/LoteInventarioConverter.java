package com.postech.fiap.fase5.api.usecases.estimativa.converter;

import com.postech.fiap.fase5.api.dto.estimativa.InventarioPontoDispensacaoInsumosPorLoteDTO;
import com.postech.fiap.fase5.api.repositories.projections.LoteInventarioProjection;
import org.springframework.stereotype.Component;

@Component
public class LoteInventarioConverter {

    public InventarioPontoDispensacaoInsumosPorLoteDTO toDTO(LoteInventarioProjection projection) {
        return InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                .idLote(projection.getIdLote())
                .idInsumo(projection.getIdInsumo())
                .nomeInsumo(projection.getNomeInsumo())
                .numeroLote(projection.getNumeroLote())
                .quantidade(projection.getQuantidade())
                .dataValidade(projection.getDataValidade())
                .build();
    }
}

