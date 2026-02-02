package com.postech.fiap.fase5.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class LoteInventarioEstimativaDTO {
    private Long idPontoDispensacao;
    private Long idInsumo;
    private String nomeInsumo;
    private Integer quantidade;
    private Long idLote;
    private String numeroLote;
    private LocalDate dataValidade;
}
