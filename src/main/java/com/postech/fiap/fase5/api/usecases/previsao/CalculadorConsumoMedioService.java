package com.postech.fiap.fase5.api.usecases.previsao;
import com.postech.fiap.fase5.api.dto.estimativa.HistoricoConsumoMesAnosAnterioresDTO;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class CalculadorConsumoMedioService {
    private static final double DIAS_POR_MES = 30.0;
    public double calcularMediaDiaria(List<HistoricoConsumoMesAnosAnterioresDTO> historicoInsumo) {
        long totalConsumo = calcularConsumoTotal(historicoInsumo);
        int numeroDeAnos = historicoInsumo.size();
        double mediaMensalSazonal = calcularMediaMensal(totalConsumo, numeroDeAnos);
        return mediaMensalSazonal / DIAS_POR_MES;
    }
    private long calcularConsumoTotal(List<HistoricoConsumoMesAnosAnterioresDTO> historicoInsumo) {
        return historicoInsumo.stream()
                .mapToLong(HistoricoConsumoMesAnosAnterioresDTO::getTotalConsumo)
                .sum();
    }
    private double calcularMediaMensal(long totalConsumo, int numeroDeAnos) {
        return (double) totalConsumo / Math.max(numeroDeAnos, 1);
    }
}
