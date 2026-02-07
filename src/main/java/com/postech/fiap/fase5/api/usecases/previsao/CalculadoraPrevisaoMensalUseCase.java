package com.postech.fiap.fase5.api.usecases.previsao;

import com.postech.fiap.fase5.api.dto.estimativa.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculadoraPrevisaoMensalUseCase {

    private final CalculadorPrevisaoSazonalService calculadorPrevisaoSazonalService;
    private final GeradorSugestoesTransferenciaService geradorSugestoesTransferenciaService;

    public List<InventarioMensalDTO> execute(List<InventarioMensalDTO> inventarios) {
        inventarios.forEach(this::processarInventarioPonto);
        geradorSugestoesTransferenciaService.gerarSugestoes(inventarios);
        return inventarios;
    }

    private void processarInventarioPonto(InventarioMensalDTO inventario) {
        List<HistoricoConsumoMesAnosAnterioresDTO> historicoSazonal = inventario.getHistoricoSazonal();
        inventario.getInsumos().forEach(insumo ->
            calculadorPrevisaoSazonalService.calcularPrevisao(insumo, historicoSazonal)
        );
    }
}
