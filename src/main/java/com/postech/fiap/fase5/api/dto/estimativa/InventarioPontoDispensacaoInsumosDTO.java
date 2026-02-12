package com.postech.fiap.fase5.api.dto.estimativa;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class InventarioPontoDispensacaoInsumosDTO {
    private Long idInsumo;
    private String nomeInsumo;
    private Integer quantidade;
    private List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotes;
    
    private Double consumoMedioDiario;
    private Integer previsaoEsgotamentoDias;
    private String statusPrevisao; // Ex: "CRÍTICO", "ALERTA", "NORMAL"
    private List<SugestaoTransferenciaDTO> sugestoesTransferenciaImediata;

    private Double consumoMedioDiarioSazonal;
    private Integer previsaoEsgotamentoDiasSazonal;
    private String statusPrevisaoSazonal;
    private List<SugestaoTransferenciaDTO> sugestoesTransferenciaSazonal;
}
