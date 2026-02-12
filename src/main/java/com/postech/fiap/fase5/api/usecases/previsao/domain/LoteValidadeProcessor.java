package com.postech.fiap.fase5.api.usecases.previsao.domain;

import com.postech.fiap.fase5.api.dto.estimativa.ItemRiscoValidadeDTO;
import com.postech.fiap.fase5.api.repositories.projections.LoteInventarioProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoteValidadeProcessor {

    private final RiscoValidadeDetector riscoValidadeDetector;
    private final ConsumoMedioDiarioCalculator consumoCalculator;
    private final ItemRiscoValidadeFactory itemRiscoFactory;

    public List<ItemRiscoValidadeDTO> processarLotes(
            List<LoteInventarioProjection> lotes,
            Map<Long, Double> mediaDiariaPorInsumo,
            LocalDate dataReferencia) {

        List<ItemRiscoValidadeDTO> itensEmRisco = new ArrayList<>();

        for (LoteInventarioProjection lote : lotes) {
            processarLote(lote, mediaDiariaPorInsumo, dataReferencia)
                    .ifPresent(itensEmRisco::add);
        }

        return itensEmRisco;
    }

    private java.util.Optional<ItemRiscoValidadeDTO> processarLote(
            LoteInventarioProjection lote,
            Map<Long, Double> mediaDiariaPorInsumo,
            LocalDate dataReferencia) {

        LocalDate dataValidade = lote.getDataValidade();

        if (!riscoValidadeDetector.estaEmJanelaDeRisco(dataValidade, dataReferencia)) {
            return java.util.Optional.empty();
        }

        long diasParaVencer = riscoValidadeDetector.calcularDiasParaVencer(dataValidade, dataReferencia);
        double mediaDiaria = consumoCalculator.obterMediaDiaria(mediaDiariaPorInsumo, lote.getIdInsumo());
        int sobraPrevista = riscoValidadeDetector.calcularSobraPrevista(
                lote.getQuantidade(), mediaDiaria, diasParaVencer);

        if (!riscoValidadeDetector.existeRiscoDeDesperdicio(sobraPrevista)) {
            return java.util.Optional.empty();
        }

        return java.util.Optional.of(itemRiscoFactory.criar(lote, mediaDiaria, diasParaVencer, sobraPrevista));
    }
}

