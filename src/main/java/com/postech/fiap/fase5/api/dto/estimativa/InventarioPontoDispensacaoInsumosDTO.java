package com.postech.fiap.fase5.api.dto.estimativa;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class InventarioPontoDispensacaoInsumosDTO {
    private Long idInsumo;
    private String nomeInsumo;
    private Integer quantidade;
    private List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotes;
}
