package com.postech.fiap.fase5.api.usecases.previsao.calculadores;

import com.postech.fiap.fase5.api.dto.estimativa.InventarioPontoDispensacaoInsumosPorLoteDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static com.postech.fiap.fase5.api.usecases.previsao.constantes.ConstantesPrevisao.LIMITE_VALIDADE_TRANSFERENCIA_DIAS;

@Component
public class CalculadorEstoqueTransferivel {

    public int calcular(List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotes) {
        if (lotes == null || lotes.isEmpty()) {
            return 0;
        }

        LocalDate dataCorte = LocalDate.now().plusDays(LIMITE_VALIDADE_TRANSFERENCIA_DIAS);

        return lotes.stream()
                .filter(lote -> lote.getDataValidade() != null && lote.getDataValidade().isAfter(dataCorte))
                .mapToInt(InventarioPontoDispensacaoInsumosPorLoteDTO::getQuantidade)
                .sum();
    }
}

