package com.postech.fiap.fase5.api.usecases.previsao.processadores;

import com.postech.fiap.fase5.api.dto.estimativa.HistoricoConsumoPorDiaDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InsumoDiarioDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioDiarioDTO;
import com.postech.fiap.fase5.api.usecases.previsao.filtros.FiltroHistoricoRecente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProcessadorPrevisaoPonto {

    private final FiltroHistoricoRecente filtroHistoricoRecente;
    private final ProcessadorPrevisaoInsumo processadorPrevisaoInsumo;

    public void processar(InventarioDiarioDTO ponto) {
        List<HistoricoConsumoPorDiaDTO> historicoRecente = filtroHistoricoRecente.filtrar(
                ponto.getHistoricoConsumo()
        );

        for (InsumoDiarioDTO insumo : ponto.getInsumos()) {
            processadorPrevisaoInsumo.processar(insumo, historicoRecente);
        }
    }
}

