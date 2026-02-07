package com.postech.fiap.fase5.api.usecases.previsao;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioPontoDispensacaoInsumosPorLoteDTO;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
@Service
public class CalculadorEstoqueService {
    private static final int DIAS_MARGEM_TRANSFERENCIA = 30;
    public int calcularEstoqueValido(List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotes) {
        if (lotes == null || lotes.isEmpty()) {
            return 0;
        }
        LocalDate hoje = LocalDate.now();
        return lotes.stream()
                .filter(lote -> isLoteValido(lote, hoje))
                .mapToInt(InventarioPontoDispensacaoInsumosPorLoteDTO::getQuantidade)
                .sum();
    }
    public int calcularEstoqueTransferivel(List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotes) {
        if (lotes == null || lotes.isEmpty()) {
            return 0;
        }
        LocalDate dataCorte = LocalDate.now().plusDays(DIAS_MARGEM_TRANSFERENCIA);
        return lotes.stream()
                .filter(lote -> isLoteTransferivel(lote, dataCorte))
                .mapToInt(InventarioPontoDispensacaoInsumosPorLoteDTO::getQuantidade)
                .sum();
    }
    private boolean isLoteValido(InventarioPontoDispensacaoInsumosPorLoteDTO lote, LocalDate dataReferencia) {
        return lote.getDataValidade() != null && lote.getDataValidade().isAfter(dataReferencia);
    }
    private boolean isLoteTransferivel(InventarioPontoDispensacaoInsumosPorLoteDTO lote, LocalDate dataCorte) {
        return lote.getDataValidade() != null && lote.getDataValidade().isAfter(dataCorte);
    }
}
