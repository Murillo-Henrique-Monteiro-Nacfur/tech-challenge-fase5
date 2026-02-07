package com.postech.fiap.fase5.api.usecases.previsao.filtros;

import com.postech.fiap.fase5.api.dto.estimativa.HistoricoConsumoPorDiaDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static com.postech.fiap.fase5.api.usecases.previsao.constantes.ConstantesPrevisao.PERIODO_ANALISE_DIAS;

@Component
public class FiltroHistoricoRecente {

    public List<HistoricoConsumoPorDiaDTO> filtrar(List<HistoricoConsumoPorDiaDTO> historico) {
        LocalDate dataLimite = LocalDate.now().minusDays(PERIODO_ANALISE_DIAS);
        LocalDate dataAtual = LocalDate.now().plusDays(1);

        return historico.stream()
                .filter(h -> h.getDia() != null)
                .filter(h -> h.getDia().isAfter(dataLimite))
                .filter(h -> h.getDia().isBefore(dataAtual))
                .toList();
    }

    public List<HistoricoConsumoPorDiaDTO> filtrarPorInsumo(List<HistoricoConsumoPorDiaDTO> historico, Long idInsumo) {
        return historico.stream()
                .filter(h -> h.getIdInsumo().equals(idInsumo))
                .toList();
    }
}

