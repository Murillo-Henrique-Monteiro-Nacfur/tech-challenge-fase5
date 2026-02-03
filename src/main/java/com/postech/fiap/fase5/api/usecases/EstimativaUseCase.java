package com.postech.fiap.fase5.api.usecases;

import com.postech.fiap.fase5.api.dto.estimativa.*;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import com.postech.fiap.fase5.api.repositories.HistoricoConsumoRepository;
import com.postech.fiap.fase5.api.repositories.LoteInventarioRepository;
import com.postech.fiap.fase5.api.repositories.PontoDispensacaoRepository;
import com.postech.fiap.fase5.api.repositories.projections.HistoricoConsumoMesAnosAnterioresProjection;
import com.postech.fiap.fase5.api.repositories.projections.HistoricoConsumoPorDiaProjection;
import com.postech.fiap.fase5.api.repositories.projections.LoteInventarioProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstimativaUseCase {

    private final PontoDispensacaoRepository pontoDispensacaoRepository;
    private final LoteInventarioRepository loteInventarioRepository;
    private final HistoricoConsumoRepository historicoConsumoRepository;

    // --- CENÁRIO DIÁRIO ---
    public List<InventarioDiarioDTO> executeDiaria() {
        log.info("Iniciando estimativa diária");
        
        // 1. Buscas Otimizadas (Apenas o necessário para o dia)
        List<PontoDispensacao> pontoDispensacaos = pontoDispensacaoRepository.findAll();
        List<LoteInventarioProjection> loteInventarioProjections = loteInventarioRepository.findAllLotePorInventario();
        List<HistoricoConsumoPorDiaProjection> historicoDias = historicoConsumoRepository.buscaHistoricoPorDiaNosUltimosTrintaDias(LocalDateTime.now().minusDays(30));

        // 2. Agrupamentos
        Map<Long, List<LoteInventarioProjection>> lotesPorPontoMap = loteInventarioProjections.stream()
                .collect(Collectors.groupingBy(LoteInventarioProjection::getIdPontoDispensacao));

        List<HistoricoConsumoPorDiaDTO> historicoDiasDTOs = historicoDias.stream().map(e ->
                HistoricoConsumoPorDiaDTO.builder().dia(e.getDia()).idPontoDispensacao(e.getIdPontoDispensacao()).idInsumo(e.getIdInsumo()).totalConsumo(e.getTotalConsumo()).build()).toList();

        // 3. Montagem do DTO Diário
        return pontoDispensacaos.stream().map(ponto -> {
            List<LoteInventarioProjection> lotesDoPonto = lotesPorPontoMap.getOrDefault(ponto.getId(), Collections.emptyList());
            List<InsumoDiarioDTO> insumosDTOs = processarInsumosDiario(lotesDoPonto);
            
            List<HistoricoConsumoPorDiaDTO> historicoPorPonto = historicoDiasDTOs.stream()
                    .filter(hc -> hc.getIdPontoDispensacao().equals(ponto.getId()))
                    .toList();

            return InventarioDiarioDTO.builder()
                    .pontoDispensacao(ponto)
                    .insumos(insumosDTOs)
                    .historicoConsumo(historicoPorPonto)
                    .build();
        }).toList();
    }

    // --- CENÁRIO MENSAL ---
    public List<InventarioMensalDTO> executeMensal() {
        log.info("Iniciando estimativa mensal");

        // 1. Buscas Otimizadas (Apenas o necessário para o mês/sazonal)
        List<PontoDispensacao> pontoDispensacaos = pontoDispensacaoRepository.findAll();
        List<LoteInventarioProjection> loteInventarioProjections = loteInventarioRepository.findAllLotePorInventario();
        List<HistoricoConsumoMesAnosAnterioresProjection> historicoAnos = historicoConsumoRepository.buscaHistoricoParaOProximoMesDosUltimosCincoAnos();

        // 2. Agrupamentos
        Map<Long, List<LoteInventarioProjection>> lotesPorPontoMap = loteInventarioProjections.stream()
                .collect(Collectors.groupingBy(LoteInventarioProjection::getIdPontoDispensacao));

        List<HistoricoConsumoMesAnosAnterioresDTO> historicoAnosDTOs = historicoAnos.stream().map(e ->
                HistoricoConsumoMesAnosAnterioresDTO.builder().ano(e.getAno()).idPontoDispensacao(e.getIdPontoDispensacao()).idInsumo(e.getIdInsumo()).mes(e.getMes()).totalConsumo(e.getTotalConsumo()).build()).toList();

        // 3. Montagem do DTO Mensal
        return pontoDispensacaos.stream().map(ponto -> {
            List<LoteInventarioProjection> lotesDoPonto = lotesPorPontoMap.getOrDefault(ponto.getId(), Collections.emptyList());
            List<InsumoMensalDTO> insumosDTOs = processarInsumosMensal(lotesDoPonto);
            
            List<HistoricoConsumoMesAnosAnterioresDTO> historicoPorPonto = historicoAnosDTOs.stream()
                    .filter(hc -> hc.getIdPontoDispensacao().equals(ponto.getId()))
                    .toList();

            return InventarioMensalDTO.builder()
                    .pontoDispensacao(ponto)
                    .insumos(insumosDTOs)
                    .historicoSazonal(historicoPorPonto)
                    .build();
        }).toList();
    }

    // --- MÉTODOS AUXILIARES ---

    private List<InsumoDiarioDTO> processarInsumosDiario(List<LoteInventarioProjection> lotesDoPonto) {
        Map<Long, List<LoteInventarioProjection>> lotesPorInsumo = lotesDoPonto.stream()
                .collect(Collectors.groupingBy(LoteInventarioProjection::getIdInsumo));

        return lotesPorInsumo.values().stream().map(listaLotes -> {
            LoteInventarioProjection primeiro = listaLotes.get(0);
            List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotesDTOs = listaLotes.stream().map(this::converterParaLoteDTO).toList();
            int quantidadeTotal = listaLotes.stream().mapToInt(LoteInventarioProjection::getQuantidade).sum();

            return InsumoDiarioDTO.builder()
                    .idInsumo(primeiro.getIdInsumo())
                    .nomeInsumo(primeiro.getNomeInsumo())
                    .quantidade(quantidadeTotal)
                    .lotes(lotesDTOs)
                    .build();
        }).toList();
    }

    private List<InsumoMensalDTO> processarInsumosMensal(List<LoteInventarioProjection> lotesDoPonto) {
        Map<Long, List<LoteInventarioProjection>> lotesPorInsumo = lotesDoPonto.stream()
                .collect(Collectors.groupingBy(LoteInventarioProjection::getIdInsumo));

        return lotesPorInsumo.values().stream().map(listaLotes -> {
            LoteInventarioProjection primeiro = listaLotes.get(0);
            List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotesDTOs = listaLotes.stream().map(this::converterParaLoteDTO).toList();
            int quantidadeTotal = listaLotes.stream().mapToInt(LoteInventarioProjection::getQuantidade).sum();

            return InsumoMensalDTO.builder()
                    .idInsumo(primeiro.getIdInsumo())
                    .nomeInsumo(primeiro.getNomeInsumo())
                    .quantidade(quantidadeTotal)
                    .lotes(lotesDTOs)
                    .build();
        }).toList();
    }

    private InventarioPontoDispensacaoInsumosPorLoteDTO converterParaLoteDTO(LoteInventarioProjection e) {
        return InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                .idLote(e.getIdLote())
                .idInsumo(e.getIdInsumo())
                .nomeInsumo(e.getNomeInsumo())
                .numeroLote(e.getNumeroLote())
                .quantidade(e.getQuantidade())
                .dataValidade(e.getDataValidade())
                .build();
    }
}
