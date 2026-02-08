package com.postech.fiap.fase5.api.usecases;

import com.postech.fiap.fase5.api.dto.estimativa.HistoricoConsumoMesAnosAnterioresDTO;
import com.postech.fiap.fase5.api.dto.estimativa.HistoricoConsumoPorDiaDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioDiarioDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioMensalDTO;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import com.postech.fiap.fase5.api.repositories.HistoricoConsumoRepository;
import com.postech.fiap.fase5.api.repositories.LoteInventarioRepository;
import com.postech.fiap.fase5.api.repositories.PontoDispensacaoRepository;
import com.postech.fiap.fase5.api.repositories.projections.HistoricoConsumoMesAnosAnterioresProjection;
import com.postech.fiap.fase5.api.repositories.projections.HistoricoConsumoPorDiaProjection;
import com.postech.fiap.fase5.api.repositories.projections.LoteInventarioProjection;
import com.postech.fiap.fase5.api.usecases.estimativa.builder.InventarioDiarioBuilder;
import com.postech.fiap.fase5.api.usecases.estimativa.builder.InventarioMensalBuilder;
import com.postech.fiap.fase5.api.usecases.estimativa.converter.HistoricoConsumoDiarioConverter;
import com.postech.fiap.fase5.api.usecases.estimativa.converter.HistoricoConsumoMensalConverter;
import com.postech.fiap.fase5.api.usecases.estimativa.provider.DataProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DadosEstimativaUseCase {

    private final PontoDispensacaoRepository pontoDispensacaoRepository;
    private final LoteInventarioRepository loteInventarioRepository;
    private final HistoricoConsumoRepository historicoConsumoRepository;
    private final InventarioDiarioBuilder inventarioDiarioBuilder;
    private final InventarioMensalBuilder inventarioMensalBuilder;
    private final HistoricoConsumoDiarioConverter historicoConsumoDiarioConverter;
    private final HistoricoConsumoMensalConverter historicoConsumoMensalConverter;
    private final DataProvider dataProvider;

    public List<InventarioDiarioDTO> executeDiaria() {
        log.info("Iniciando estimativa diária");

        List<PontoDispensacao> pontosDispensacao = buscarPontosDispensacao();
        Map<Long, List<LoteInventarioProjection>> lotesPorPontoMap = buscarEAgruparLotesPorPonto();
        List<HistoricoConsumoPorDiaDTO> historicoConsumoDTOs = buscarHistoricoConsumoDiario();

        return pontosDispensacao.stream()
                .map(ponto -> inventarioDiarioBuilder.build(ponto, lotesPorPontoMap, historicoConsumoDTOs))
                .toList();
    }

    public List<InventarioMensalDTO> executeMensal() {
        log.info("Iniciando estimativa mensal");

        List<PontoDispensacao> pontosDispensacao = buscarPontosDispensacao();
        Map<Long, List<LoteInventarioProjection>> lotesPorPontoMap = buscarEAgruparLotesPorPonto();
        List<HistoricoConsumoMesAnosAnterioresDTO> historicoSazonalDTOs = buscarHistoricoConsumoMensal();

        return pontosDispensacao.stream()
                .map(ponto -> inventarioMensalBuilder.build(ponto, lotesPorPontoMap, historicoSazonalDTOs))
                .toList();
    }

    private List<PontoDispensacao> buscarPontosDispensacao() {
        return pontoDispensacaoRepository.findAll();
    }

    private Map<Long, List<LoteInventarioProjection>> buscarEAgruparLotesPorPonto() {
        return loteInventarioRepository.findAllLotePorInventario().stream()
                .collect(Collectors.groupingBy(LoteInventarioProjection::getIdPontoDispensacao));
    }

    private List<HistoricoConsumoPorDiaDTO> buscarHistoricoConsumoDiario() {
        LocalDateTime dataInicio = dataProvider.obterDataInicioHistoricoDiario();
        LocalDateTime dataFim = dataProvider.obterDataFimHistoricoDiario();

        List<HistoricoConsumoPorDiaProjection> historicoProjections =
                historicoConsumoRepository.buscaHistoricoPorDiaNosUltimosTrintaDias(dataInicio, dataFim);

        return historicoProjections.stream()
                .map(historicoConsumoDiarioConverter::toDTO)
                .toList();
    }

    private List<HistoricoConsumoMesAnosAnterioresDTO> buscarHistoricoConsumoMensal() {
        LocalDateTime proximoMes = dataProvider.obterProximoMes();

        List<HistoricoConsumoMesAnosAnterioresProjection> historicoProjections =
                historicoConsumoRepository.buscaHistoricoParaOProximoMesDosUltimosCincoAnos(
                        proximoMes.getMonthValue(), proximoMes.getYear());

        return historicoProjections.stream()
                .map(historicoConsumoMensalConverter::toDTO)
                .toList();
    }
}
