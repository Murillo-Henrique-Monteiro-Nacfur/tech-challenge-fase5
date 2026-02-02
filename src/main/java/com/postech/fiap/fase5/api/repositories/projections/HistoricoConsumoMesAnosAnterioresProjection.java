package com.postech.fiap.fase5.api.repositories.projections;

public interface HistoricoConsumoMesAnosAnterioresProjection {
    Integer getMes();
    Integer getAno();
    Integer getTotalConsumo();
    Long getIdPontoDispensacao();
    Long getIdInsumo();
}