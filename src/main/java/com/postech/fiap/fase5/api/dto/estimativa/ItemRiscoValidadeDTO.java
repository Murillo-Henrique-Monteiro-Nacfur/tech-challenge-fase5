package com.postech.fiap.fase5.api.dto.estimativa;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class ItemRiscoValidadeDTO {
    private Long idInsumo;
    private String nomeInsumo;
    private String numeroLote;
    private LocalDate dataValidade;
    private Integer quantidadeAtual;
    private Double consumoMedioDiario;
    private Integer quantidadeDesperdicioPrevisto;
    private long diasParaVencer;
}
