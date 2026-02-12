package com.postech.fiap.fase5.api.usecases.previsao.transferencia;

import com.postech.fiap.fase5.api.dto.estimativa.InsumoDiarioDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioDiarioDTO;
import com.postech.fiap.fase5.api.dto.estimativa.SugestaoTransferenciaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeradorSugestoesTransferencia {

    private final IdentificadorPontosDoadores identificadorPontosDoadores;
    private final AtribuirSugestoesTransferencia atribuirSugestoesTransferencia;

    public void gerar(List<InventarioDiarioDTO> todosPontos) {
        Map<Long, List<SugestaoTransferenciaDTO>> mapaDoadores = identificarDoadores(todosPontos);
        atribuirSugestoes(todosPontos, mapaDoadores);
    }

    private Map<Long, List<SugestaoTransferenciaDTO>> identificarDoadores(List<InventarioDiarioDTO> todosPontos) {
        Map<Long, List<SugestaoTransferenciaDTO>> mapaDoadores = new HashMap<>();

        for (InventarioDiarioDTO ponto : todosPontos) {
            processarPontoParaDoacao(ponto, mapaDoadores);
        }

        return mapaDoadores;
    }

    private void processarPontoParaDoacao(InventarioDiarioDTO ponto, Map<Long, List<SugestaoTransferenciaDTO>> mapaDoadores) {
        for (InsumoDiarioDTO insumo : ponto.getInsumos()) {
            SugestaoTransferenciaDTO doador = identificadorPontosDoadores.identificar(ponto, insumo);

            if (doador != null) {
                mapaDoadores.computeIfAbsent(insumo.getIdInsumo(), k -> new ArrayList<>()).add(doador);
            }
        }
    }

    private void atribuirSugestoes(List<InventarioDiarioDTO> todosPontos, Map<Long, List<SugestaoTransferenciaDTO>> mapaDoadores) {
        for (InventarioDiarioDTO ponto : todosPontos) {
            processarPontoParaRecebimento(ponto, mapaDoadores);
        }
    }

    private void processarPontoParaRecebimento(InventarioDiarioDTO ponto, Map<Long, List<SugestaoTransferenciaDTO>> mapaDoadores) {
        for (InsumoDiarioDTO insumo : ponto.getInsumos()) {
            List<SugestaoTransferenciaDTO> doadores = mapaDoadores.getOrDefault(insumo.getIdInsumo(), new ArrayList<>());
            atribuirSugestoesTransferencia.atribuir(ponto, insumo, doadores);
        }
    }
}

