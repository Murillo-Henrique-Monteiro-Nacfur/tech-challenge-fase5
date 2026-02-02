package com.postech.fiap.fase5.api.usecases;

import com.postech.fiap.fase5.api.dto.estimativa.*;
import com.postech.fiap.fase5.api.entities.HistoricoConsumo;
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


    public Object execute() {
        log.info("Iniciando estimativa");
//buscando dados do banco inicio
        List<PontoDispensacao> pontoDispensacaos = pontoDispensacaoRepository.findAll();

        List<LoteInventarioProjection> loteInventarioProjections = loteInventarioRepository.findAllLotePorInventario();

        List<HistoricoConsumoPorDiaProjection> historicoConsumoPorDiaProjections = historicoConsumoRepository.buscaHistoricoPorDiaNosUltimosTrintaDias(LocalDateTime.now().minusDays(30));


        List<HistoricoConsumoMesAnosAnterioresProjection> historicoConsumoMesAnosAnterioresProjections = historicoConsumoRepository.buscaHistoricoParaOProximoMesDosUltimosCincoAnos();
//buscando dados do banco fim

//colocando em mocks inicio
        // 1. Otimização: Agrupa projeções por PontoDispensacaoId antecipadamente (Evita O(N*M) no loop)
        Map<Long, List<LoteInventarioProjection>> lotesPorPontoMap = loteInventarioProjections.stream()
                .collect(Collectors.groupingBy(LoteInventarioProjection::getIdPontoDispensacao));

        List<HistoricoConsumoPorDiaDTO> historicoConsumoPorDiaDTOS = historicoConsumoPorDiaProjections.stream().map(e ->
                HistoricoConsumoPorDiaDTO.builder().dia(e.getDia()).idPontoDispensacao(e.getIdPontoDispensacao()).idInsumo(e.getIdInsumo()).totalConsumo(e.getTotalConsumo()).build()).toList();

        List<HistoricoConsumoMesAnosAnterioresDTO> historicoConsumoMesAnosAnterioresDTOS = historicoConsumoMesAnosAnterioresProjections.stream().map(e ->
                HistoricoConsumoMesAnosAnterioresDTO.builder().ano(e.getAno()).idPontoDispensacao(e.getIdPontoDispensacao()).idInsumo(e.getIdInsumo()).mes(e.getMes()).totalConsumo(e.getTotalConsumo()).build()).toList();
//colocando em mocks fim

        List<InventarioPontoDispensacaoDTO> inventarioPontoDispensacaoDTOS = pontoDispensacaos.stream().map(pontoDispensacao -> {
                    // Busca os lotes do mapa (O(1)) ao invés de filtrar a lista inteira (O(N))
                    List<LoteInventarioProjection> lotesDoPonto = lotesPorPontoMap.getOrDefault(pontoDispensacao.getId(), Collections.emptyList());

                    List<InventarioPontoDispensacaoInsumosDTO> inventarioPontoDispensacaoInsumosDTOS = processarInsumos(lotesDoPonto);

                    List<HistoricoConsumoPorDiaDTO> historicoPorPonto = historicoConsumoPorDiaDTOS.stream().filter(hc -> hc.getIdPontoDispensacao().equals(pontoDispensacao.getId()))
                            .toList();


                    List<HistoricoConsumoMesAnosAnterioresDTO> historicoConsumoMesAnosAnterioresDTOSFiltrado = historicoConsumoMesAnosAnterioresDTOS.stream().filter(hc -> hc.getIdPontoDispensacao().equals(pontoDispensacao.getId()))
                            .toList();

                    return InventarioPontoDispensacaoDTO.builder()
                            .pontoDispensacao(pontoDispensacao)
                            .inventarioPontoDispensacaoInsumosDTOS(inventarioPontoDispensacaoInsumosDTOS)
                            .historicoConsumoPorDiaDTOS(historicoPorPonto)
                            .historicoConsumoMesAnosAnterioresDTOS(historicoConsumoMesAnosAnterioresDTOSFiltrado)
                            .build();
                })
                .toList();

        return inventarioPontoDispensacaoDTOS;
    }

    private List<InventarioPontoDispensacaoInsumosDTO> processarInsumos(List<LoteInventarioProjection> lotesDoPonto) {
        // Agrupa por ID do Insumo
        Map<Long, List<LoteInventarioProjection>> lotesPorInsumo = lotesDoPonto.stream()
                .collect(Collectors.groupingBy(LoteInventarioProjection::getIdInsumo));

        return lotesPorInsumo.values().stream().map(listaLotes -> {
            LoteInventarioProjection primeiro = listaLotes.get(0);
            
            List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotesDTOs = listaLotes.stream()
                    .map(this::converterParaLoteDTO)
                    .toList();

            int quantidadeTotal = listaLotes.stream().mapToInt(LoteInventarioProjection::getQuantidade).sum();

            return InventarioPontoDispensacaoInsumosDTO.builder()
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
