package com.postech.fiap.fase5.api.usecases.previsao.transferencia;

import com.postech.fiap.fase5.api.dto.estimativa.InsumoDiarioDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioDiarioDTO;
import com.postech.fiap.fase5.api.dto.estimativa.SugestaoTransferenciaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.postech.fiap.fase5.api.usecases.previsao.constantes.ConstantesPrevisao.STATUS_ALERTA;
import static com.postech.fiap.fase5.api.usecases.previsao.constantes.ConstantesPrevisao.STATUS_CRITICO;

@Component
@RequiredArgsConstructor
public class AtribuirSugestoesTransferencia {

    public void atribuir(InventarioDiarioDTO ponto, InsumoDiarioDTO insumo, List<SugestaoTransferenciaDTO> doadores) {
        if (!necessitaTransferencia(insumo)) {
            return;
        }

        List<SugestaoTransferenciaDTO> doadoresValidos = filtrarDoadoresValidos(ponto, doadores);
        insumo.setSugestoesTransferencia(doadoresValidos);
    }

    private boolean necessitaTransferencia(InsumoDiarioDTO insumo) {
        String status = insumo.getStatusPrevisao();
        return STATUS_CRITICO.equals(status) || STATUS_ALERTA.equals(status);
    }

    private List<SugestaoTransferenciaDTO> filtrarDoadoresValidos(InventarioDiarioDTO ponto, List<SugestaoTransferenciaDTO> doadores) {
        return doadores.stream()
                .filter(d -> !d.getIdPontoDoador().equals(ponto.getPontoDispensacao().getId()))
                .toList();
    }
}

