package com.postech.fiap.fase5.api.dto.estimativa;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class HistoricoConsumoPorDiaDTO {
    LocalDate dia;
    Integer totalConsumo;
    Long idPontoDispensacao;
    Long idInsumo;
}
