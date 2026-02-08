package com.postech.fiap.fase5.api.usecases.estimativa.builder;

import com.postech.fiap.fase5.api.dto.estimativa.HistoricoConsumoMesAnosAnterioresDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InsumoMensalDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioMensalDTO;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import com.postech.fiap.fase5.api.repositories.projections.LoteInventarioProjection;
import com.postech.fiap.fase5.api.usecases.estimativa.processor.InsumoMensalProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InventarioMensalBuilder {

    private final InsumoMensalProcessor insumoMensalProcessor;

    public InventarioMensalDTO build(
            PontoDispensacao pontoDispensacao,
            Map<Long, List<LoteInventarioProjection>> lotesPorPontoMap,
            List<HistoricoConsumoMesAnosAnterioresDTO> historicoSazonalDTOs) {

        List<LoteInventarioProjection> lotesDoPonto = obterLotesDoPonto(pontoDispensacao.getId(), lotesPorPontoMap);
        List<InsumoMensalDTO> insumosDTOs = insumoMensalProcessor.processar(lotesDoPonto);
        List<HistoricoConsumoMesAnosAnterioresDTO> historicoPorPonto = filtrarHistoricoPorPonto(pontoDispensacao.getId(), historicoSazonalDTOs);

        return InventarioMensalDTO.builder()
                .pontoDispensacao(pontoDispensacao)
                .insumos(insumosDTOs)
                .historicoSazonal(historicoPorPonto)
                .build();
    }

    private List<LoteInventarioProjection> obterLotesDoPonto(Long idPonto, Map<Long, List<LoteInventarioProjection>> lotesPorPontoMap) {
        return lotesPorPontoMap.getOrDefault(idPonto, Collections.emptyList());
    }

    private List<HistoricoConsumoMesAnosAnterioresDTO> filtrarHistoricoPorPonto(Long idPonto, List<HistoricoConsumoMesAnosAnterioresDTO> historicoSazonalDTOs) {
        return historicoSazonalDTOs.stream()
                .filter(historico -> historico.getIdPontoDispensacao().equals(idPonto))
                .toList();
    }
}

