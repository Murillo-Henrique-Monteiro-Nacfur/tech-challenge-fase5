package com.postech.fiap.fase5.api.usecases.previsao;
import com.postech.fiap.fase5.api.dto.estimativa.HistoricoConsumoMesAnosAnterioresDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InsumoMensalDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CalculadorPrevisaoSazonalService {
    private final CalculadorConsumoMedioService calculadorConsumoMedioService;
    private final CalculadorEstoqueService calculadorEstoqueService;
    private final AvaliadorStatusPrevisaoService avaliadorStatusPrevisaoService;
    public void calcularPrevisao(InsumoMensalDTO insumo, List<HistoricoConsumoMesAnosAnterioresDTO> historicoSazonal) {
        if (historicoSazonal == null || historicoSazonal.isEmpty()) {
            definirSemDadosSazonais(insumo);
            return;
        }
        List<HistoricoConsumoMesAnosAnterioresDTO> historicoInsumo = filtrarHistoricoPorInsumo(insumo, historicoSazonal);
        if (historicoInsumo.isEmpty()) {
            definirSemDadosSazonais(insumo);
            return;
        }
        double mediaDiariaSazonal = calculadorConsumoMedioService.calcularMediaDiaria(historicoInsumo);
        insumo.setConsumoMedioDiarioSazonal(mediaDiariaSazonal);
        PrevisaoEsgotamento previsao = calcularPrevisaoEsgotamento(insumo, mediaDiariaSazonal);
        insumo.setPrevisaoEsgotamentoDiasSazonal(previsao.getDiasRestantes());
        insumo.setStatusPrevisaoSazonal(previsao.getStatus());
    }
    private List<HistoricoConsumoMesAnosAnterioresDTO> filtrarHistoricoPorInsumo(
            InsumoMensalDTO insumo, 
            List<HistoricoConsumoMesAnosAnterioresDTO> historicoSazonal) {
        return historicoSazonal.stream()
                .filter(historico -> historico.getIdInsumo().equals(insumo.getIdInsumo()))
                .toList();
    }
    private PrevisaoEsgotamento calcularPrevisaoEsgotamento(InsumoMensalDTO insumo, double mediaDiariaSazonal) {
        if (mediaDiariaSazonal <= 0) {
            return new PrevisaoEsgotamento(999, "SEM_CONSUMO_PREVISTO");
        }
        int estoqueValido = calculadorEstoqueService.calcularEstoqueValido(insumo.getLotes());
        int diasRestantes = (int) (estoqueValido / mediaDiariaSazonal);
        String status = avaliadorStatusPrevisaoService.avaliar(diasRestantes);
        return new PrevisaoEsgotamento(diasRestantes, status);
    }
    private void definirSemDadosSazonais(InsumoMensalDTO insumo) {
        insumo.setConsumoMedioDiarioSazonal(0.0);
        insumo.setPrevisaoEsgotamentoDiasSazonal(999);
        insumo.setStatusPrevisaoSazonal("SEM_DADOS_SAZONAIS");
    }
    private static class PrevisaoEsgotamento {
        private final int diasRestantes;
        private final String status;
        public PrevisaoEsgotamento(int diasRestantes, String status) {
            this.diasRestantes = diasRestantes;
            this.status = status;
        }
        public int getDiasRestantes() {
            return diasRestantes;
        }
        public String getStatus() {
            return status;
        }
    }
}
