package com.postech.fiap.fase5.api.dto.estimativa;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class InsumoMensalDTO {
    // Dados Básicos do Insumo
    private Long idInsumo;
    private String nomeInsumo;
    private Integer quantidade;
    private List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotes;
    
    // Dados Específicos Mensais/Sazonais
    private Double consumoMedioDiarioSazonal;
    private Integer previsaoEsgotamentoDiasSazonal;
    private String statusPrevisaoSazonal;
    private List<SugestaoTransferenciaDTO> sugestoesTransferenciaSazonal;
}
