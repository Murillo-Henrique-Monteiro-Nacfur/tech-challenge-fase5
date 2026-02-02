package com.postech.fiap.fase5.api.dto.estimativa;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class InventarioPontoDispensacaoInsumosPorLoteDTO {
    private Long idLote;
    private Long idInsumo;
    private String numeroLote;
    private Integer quantidade;
    private LocalDate dataValidade;
    private String nomeInsumo;
}
