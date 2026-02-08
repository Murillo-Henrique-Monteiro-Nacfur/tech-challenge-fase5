package com.postech.fiap.fase5.api.usecases.estimativa.builder;

import com.postech.fiap.fase5.api.dto.estimativa.HistoricoConsumoPorDiaDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InsumoDiarioDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioDiarioDTO;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import com.postech.fiap.fase5.api.repositories.projections.LoteInventarioProjection;
import com.postech.fiap.fase5.api.usecases.estimativa.processor.InsumoDiarioProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InventarioDiarioBuilder {

    private final InsumoDiarioProcessor insumoDiarioProcessor;

    public InventarioDiarioDTO build(
            PontoDispensacao pontoDispensacao,
            Map<Long, List<LoteInventarioProjection>> lotesPorPontoMap,
            List<HistoricoConsumoPorDiaDTO> historicoConsumoDTOs) {

        List<LoteInventarioProjection> lotesDoPonto = obterLotesDoPonto(pontoDispensacao.getId(), lotesPorPontoMap);
        List<InsumoDiarioDTO> insumosDTOs = insumoDiarioProcessor.processar(lotesDoPonto);
        List<HistoricoConsumoPorDiaDTO> historicoPorPonto = filtrarHistoricoPorPonto(pontoDispensacao.getId(), historicoConsumoDTOs);

        return InventarioDiarioDTO.builder()
                .pontoDispensacao(pontoDispensacao)
                .insumos(insumosDTOs)
                .historicoConsumo(historicoPorPonto)
                .build();
    }

    private List<LoteInventarioProjection> obterLotesDoPonto(Long idPonto, Map<Long, List<LoteInventarioProjection>> lotesPorPontoMap) {
        return lotesPorPontoMap.getOrDefault(idPonto, Collections.emptyList());
    }

    private List<HistoricoConsumoPorDiaDTO> filtrarHistoricoPorPonto(Long idPonto, List<HistoricoConsumoPorDiaDTO> historicoConsumoDTOs) {
        return historicoConsumoDTOs.stream()
                .filter(historico -> historico.getIdPontoDispensacao().equals(idPonto))
                .toList();
    }
}

