package com.postech.fiap.fase5.api.dto.estimativa;

import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
public class InventarioPontoDispensacaoDTO {
    private PontoDispensacao pontoDispensacao;
    private List<InventarioPontoDispensacaoInsumosDTO> inventarioPontoDispensacaoInsumosDTOS;
    @Setter
    private List<HistoricoConsumoPorDiaDTO> historicoConsumoPorDiaDTOS;
    @Setter
    private List<HistoricoConsumoMesAnosAnterioresDTO> historicoConsumoMesAnosAnterioresDTOS;
}
