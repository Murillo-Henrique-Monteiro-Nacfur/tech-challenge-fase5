package com.postech.fiap.fase5.api.usecases.previsao;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioMensalDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InsumoMensalDTO;
import com.postech.fiap.fase5.api.dto.estimativa.SugestaoTransferenciaDTO;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
public class AtribuidorSugestoesService {
    private static final String STATUS_CRITICO = "CRITICO";
    private static final String STATUS_ALERTA = "ALERTA";
    public void atribuirSugestoes(
            InventarioMensalDTO ponto, 
            InsumoMensalDTO insumo, 
            Map<Long, List<SugestaoTransferenciaDTO>> mapaDoadores) {
        if (!necessitaTransferencia(insumo)) {
            return;
        }
        List<SugestaoTransferenciaDTO> doadoresDisponiveis = obterDoadoresDisponiveis(
                ponto, 
                insumo, 
                mapaDoadores
        );
        insumo.setSugestoesTransferenciaSazonal(doadoresDisponiveis);
    }
    private boolean necessitaTransferencia(InsumoMensalDTO insumo) {
        String status = insumo.getStatusPrevisaoSazonal();
        return STATUS_CRITICO.equals(status) || STATUS_ALERTA.equals(status);
    }
    private List<SugestaoTransferenciaDTO> obterDoadoresDisponiveis(
            InventarioMensalDTO ponto, 
            InsumoMensalDTO insumo, 
            Map<Long, List<SugestaoTransferenciaDTO>> mapaDoadores) {
        List<SugestaoTransferenciaDTO> doadores = mapaDoadores.getOrDefault(
                insumo.getIdInsumo(), 
                new ArrayList<>()
        );
        return filtrarDoadoresValidos(ponto, doadores);
    }
    private List<SugestaoTransferenciaDTO> filtrarDoadoresValidos(
            InventarioMensalDTO ponto, 
            List<SugestaoTransferenciaDTO> doadores) {
        return doadores.stream()
                .filter(doador -> !doador.getIdPontoDoador().equals(ponto.getPontoDispensacao().getId()))
                .toList();
    }
}
