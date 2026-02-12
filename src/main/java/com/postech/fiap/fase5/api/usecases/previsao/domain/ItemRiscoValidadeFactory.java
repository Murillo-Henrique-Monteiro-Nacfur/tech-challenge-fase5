package com.postech.fiap.fase5.api.usecases.previsao.domain;

import com.postech.fiap.fase5.api.dto.estimativa.ItemRiscoValidadeDTO;
import com.postech.fiap.fase5.api.repositories.projections.LoteInventarioProjection;
import org.springframework.stereotype.Component;


@Component
public class ItemRiscoValidadeFactory {

    public ItemRiscoValidadeDTO criar(
            LoteInventarioProjection lote,
            double mediaDiaria,
            long diasParaVencer,
            int sobraPrevista) {

        return ItemRiscoValidadeDTO.builder()
                .idInsumo(lote.getIdInsumo())
                .nomeInsumo(lote.getNomeInsumo())
                .numeroLote(lote.getNumeroLote())
                .dataValidade(lote.getDataValidade())
                .quantidadeAtual(lote.getQuantidade())
                .consumoMedioDiario(mediaDiaria)
                .diasParaVencer(diasParaVencer)
                .quantidadeDesperdicioPrevisto(sobraPrevista)
                .build();
    }
}

