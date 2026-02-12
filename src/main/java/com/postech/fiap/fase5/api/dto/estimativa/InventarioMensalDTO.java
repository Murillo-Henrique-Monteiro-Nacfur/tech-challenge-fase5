package com.postech.fiap.fase5.api.dto.estimativa;

import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class InventarioMensalDTO {
    private PontoDispensacao pontoDispensacao;
    private List<InsumoMensalDTO> insumos;
    
    private List<HistoricoConsumoMesAnosAnterioresDTO> historicoSazonal;
}
