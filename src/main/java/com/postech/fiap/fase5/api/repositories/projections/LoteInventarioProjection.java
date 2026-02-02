package com.postech.fiap.fase5.api.repositories.projections;

import java.time.LocalDate;

public interface LoteInventarioProjection {

    Long getIdPontoDispensacao();
    Long getIdInsumo();
    String getNomeInsumo();
    Integer getQuantidade();
    Long getIdLote();
    String getNumeroLote();
    LocalDate getDataValidade();
}
