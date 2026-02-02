package com.postech.fiap.fase5.api.dto.estimativa;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistoricoConsumoMesAnosAnterioresDTO {
    private Integer mes;
    private Integer ano;
    private Integer totalConsumo;
    private Long idPontoDispensacao;
    private Long idInsumo;

}
