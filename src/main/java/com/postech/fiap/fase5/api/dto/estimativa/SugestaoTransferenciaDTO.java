package com.postech.fiap.fase5.api.dto.estimativa;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SugestaoTransferenciaDTO {
    private Long idPontoDoador;
    private String nomePontoDoador;
    private Integer quantidadeDisponivelNoDoador;
    private Integer previsaoDiasDoador;
}
