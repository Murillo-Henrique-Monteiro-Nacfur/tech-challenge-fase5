package com.postech.fiap.fase5.api.dto.estimativa;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class InsumoDiarioDTO {
    // Dados Básicos do Insumo
    private Long idInsumo;
    private String nomeInsumo;
    private Integer quantidade;
    private List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotes;
    
    // Dados Específicos Diários
    private Double consumoMedioDiario;
    private Integer previsaoEsgotamentoDias;
    private String statusPrevisao;
    private List<SugestaoTransferenciaDTO> sugestoesTransferencia;
}
