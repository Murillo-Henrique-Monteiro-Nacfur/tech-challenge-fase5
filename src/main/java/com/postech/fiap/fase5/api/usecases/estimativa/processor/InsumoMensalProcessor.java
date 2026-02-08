package com.postech.fiap.fase5.api.usecases.estimativa.processor;

import com.postech.fiap.fase5.api.dto.estimativa.InsumoMensalDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioPontoDispensacaoInsumosPorLoteDTO;
import com.postech.fiap.fase5.api.repositories.projections.LoteInventarioProjection;
import com.postech.fiap.fase5.api.usecases.estimativa.converter.LoteInventarioConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InsumoMensalProcessor {

    private final LoteInventarioConverter loteInventarioConverter;

    public List<InsumoMensalDTO> processar(List<LoteInventarioProjection> lotesDoPonto) {
        Map<Long, List<LoteInventarioProjection>> lotesPorInsumo = agruparPorInsumo(lotesDoPonto);

        return lotesPorInsumo.values().stream()
                .map(this::criarInsumoMensalDTO)
                .toList();
    }

    private Map<Long, List<LoteInventarioProjection>> agruparPorInsumo(List<LoteInventarioProjection> lotes) {
        return lotes.stream()
                .collect(Collectors.groupingBy(LoteInventarioProjection::getIdInsumo));
    }

    private InsumoMensalDTO criarInsumoMensalDTO(List<LoteInventarioProjection> listaLotes) {
        LoteInventarioProjection primeiroLote = listaLotes.getFirst();
        List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotesDTOs = converterLotes(listaLotes);
        int quantidadeTotal = calcularQuantidadeTotal(listaLotes);

        return InsumoMensalDTO.builder()
                .idInsumo(primeiroLote.getIdInsumo())
                .nomeInsumo(primeiroLote.getNomeInsumo())
                .quantidade(quantidadeTotal)
                .lotes(lotesDTOs)
                .build();
    }

    private List<InventarioPontoDispensacaoInsumosPorLoteDTO> converterLotes(List<LoteInventarioProjection> listaLotes) {
        return listaLotes.stream()
                .map(loteInventarioConverter::toDTO)
                .toList();
    }

    private int calcularQuantidadeTotal(List<LoteInventarioProjection> listaLotes) {
        return listaLotes.stream()
                .mapToInt(LoteInventarioProjection::getQuantidade)
                .sum();
    }
}

