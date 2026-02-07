package com.postech.fiap.fase5.api.usecases.previsao;

import com.postech.fiap.fase5.api.dto.estimativa.AlertaValidadeDTO;
import com.postech.fiap.fase5.api.dto.estimativa.ItemRiscoValidadeDTO;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import com.postech.fiap.fase5.api.repositories.LoteInventarioRepository;
import com.postech.fiap.fase5.api.repositories.PontoDispensacaoRepository;
import com.postech.fiap.fase5.api.repositories.projections.HistoricoConsumoPorDiaProjection;
import com.postech.fiap.fase5.api.repositories.projections.LoteInventarioProjection;
import com.postech.fiap.fase5.api.services.NotificacaoService;
import com.postech.fiap.fase5.api.usecases.previsao.domain.AlertaValidadeFactory;
import com.postech.fiap.fase5.api.usecases.previsao.domain.ConsumoMedioDiarioCalculator;
import com.postech.fiap.fase5.api.usecases.previsao.domain.LoteValidadeProcessor;
import com.postech.fiap.fase5.api.usecases.previsao.ports.HistoricoConsumoProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertaValidadeUseCase {

    private final PontoDispensacaoRepository pontoDispensacaoRepository;
    private final LoteInventarioRepository loteInventarioRepository;
    private final HistoricoConsumoProvider historicoConsumoProvider;
    private final ConsumoMedioDiarioCalculator consumoCalculator;
    private final LoteValidadeProcessor loteProcessor;
    private final AlertaValidadeFactory alertaFactory;
    private final NotificacaoService notificacaoService;

    public List<AlertaValidadeDTO> execute() {
        log.info("Iniciando verificação de risco de validade");

        LocalDateTime dataHoraReferencia = LocalDateTime.now();
        LocalDate dataReferencia = dataHoraReferencia.toLocalDate();

        List<PontoDispensacao> pontos = pontoDispensacaoRepository.findAll();
        List<LoteInventarioProjection> todosLotes = loteInventarioRepository.findAllLotePorInventario();
        List<HistoricoConsumoPorDiaProjection> historicoRecente =
                historicoConsumoProvider.buscarHistoricoRecente(dataHoraReferencia);

        Map<Long, List<LoteInventarioProjection>> lotesPorPonto = agruparLotesPorPonto(todosLotes);
        Map<Long, Map<Long, Double>> mediaDiariaPorPontoEInsumo =
                consumoCalculator.calcularMediasPorPontoEInsumo(historicoRecente);

        List<AlertaValidadeDTO> alertas = processarPontosDispensacao(
                pontos, lotesPorPonto, mediaDiariaPorPontoEInsumo, dataReferencia);

        notificacaoService.notificarRiscoValidade(alertas);

        return alertas;
    }

    private Map<Long, List<LoteInventarioProjection>> agruparLotesPorPonto(List<LoteInventarioProjection> lotes) {
        return lotes.stream()
                .collect(Collectors.groupingBy(LoteInventarioProjection::getIdPontoDispensacao));
    }

    private List<AlertaValidadeDTO> processarPontosDispensacao(
            List<PontoDispensacao> pontos,
            Map<Long, List<LoteInventarioProjection>> lotesPorPonto,
            Map<Long, Map<Long, Double>> mediaDiariaPorPontoEInsumo,
            LocalDate dataReferencia) {

        List<AlertaValidadeDTO> alertas = new ArrayList<>();

        for (PontoDispensacao ponto : pontos) {
            processarPontoDispensacao(ponto, lotesPorPonto, mediaDiariaPorPontoEInsumo, dataReferencia)
                    .ifPresent(alertas::add);
        }

        return alertas;
    }

    private java.util.Optional<AlertaValidadeDTO> processarPontoDispensacao(
            PontoDispensacao ponto,
            Map<Long, List<LoteInventarioProjection>> lotesPorPonto,
            Map<Long, Map<Long, Double>> mediaDiariaPorPontoEInsumo,
            LocalDate dataReferencia) {

        List<LoteInventarioProjection> lotesDoPonto =
                lotesPorPonto.getOrDefault(ponto.getId(), Collections.emptyList());
        Map<Long, Double> mediaDiariaDoPonto =
                mediaDiariaPorPontoEInsumo.getOrDefault(ponto.getId(), Collections.emptyMap());

        List<ItemRiscoValidadeDTO> itensEmRisco =
                loteProcessor.processarLotes(lotesDoPonto, mediaDiariaDoPonto, dataReferencia);

        if (itensEmRisco.isEmpty()) {
            return java.util.Optional.empty();
        }

        return java.util.Optional.of(alertaFactory.criar(ponto, itensEmRisco));
    }
}
