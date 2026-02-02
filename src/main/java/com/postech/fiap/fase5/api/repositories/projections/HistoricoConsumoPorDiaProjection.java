package com.postech.fiap.fase5.api.repositories.projections;

import java.time.LocalDate;

public interface HistoricoConsumoPorDiaProjection {
    LocalDate getDia();
    Integer getTotalConsumo();
    Long getIdPontoDispensacao();
    Long getIdInsumo();
}
