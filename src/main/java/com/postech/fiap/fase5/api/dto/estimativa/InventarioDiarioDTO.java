package com.postech.fiap.fase5.api.dto.estimativa;

import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class InventarioDiarioDTO {
    private PontoDispensacao pontoDispensacao;
    private List<InsumoDiarioDTO> insumos;
    
    // Dados de Apoio para o Cálculo (não necessariamente precisam ser expostos no JSON final, mas ajudam no trânsito)
    // Poderíamos usar @JsonIgnore se quiséssemos esconder
    private List<HistoricoConsumoPorDiaDTO> historicoConsumo;
}
