package com.postech.fiap.fase5.api.usecases.previsao.transferencia;

import com.postech.fiap.fase5.api.dto.estimativa.InsumoDiarioDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioDiarioDTO;
import com.postech.fiap.fase5.api.dto.estimativa.SugestaoTransferenciaDTO;
import com.postech.fiap.fase5.api.usecases.previsao.calculadores.CalculadorEstoqueTransferivel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.postech.fiap.fase5.api.usecases.previsao.constantes.ConstantesPrevisao.LIMITE_ESTOQUE_DOACAO_DIAS;
import static com.postech.fiap.fase5.api.usecases.previsao.constantes.ConstantesPrevisao.PERIODO_ANALISE_DIAS;

@Component
@RequiredArgsConstructor
public class IdentificadorPontosDoadores {

    private final CalculadorEstoqueTransferivel calculadorEstoqueTransferivel;

    public SugestaoTransferenciaDTO identificar(InventarioDiarioDTO ponto, InsumoDiarioDTO insumo) {
        if (!possuiExcedente(insumo)) {
            return null;
        }

        int estoqueTransferivel = calculadorEstoqueTransferivel.calcular(insumo.getLotes());
        int excedente = calcularExcedente(insumo, estoqueTransferivel);

        if (excedente <= 0) {
            return null;
        }

        return construirSugestao(ponto, insumo, excedente);
    }

    private boolean possuiExcedente(InsumoDiarioDTO insumo) {
        return insumo.getPrevisaoEsgotamentoDias() != null
                && insumo.getPrevisaoEsgotamentoDias() > LIMITE_ESTOQUE_DOACAO_DIAS;
    }

    private int calcularExcedente(InsumoDiarioDTO insumo, int estoqueTransferivel) {
        double consumo30Dias = obterConsumoMedioDiario(insumo) * PERIODO_ANALISE_DIAS;
        return (int) (estoqueTransferivel - consumo30Dias);
    }

    private double obterConsumoMedioDiario(InsumoDiarioDTO insumo) {
        return insumo.getConsumoMedioDiario() != null ? insumo.getConsumoMedioDiario() : 0.0;
    }

    private SugestaoTransferenciaDTO construirSugestao(InventarioDiarioDTO ponto, InsumoDiarioDTO insumo, int excedente) {
        return SugestaoTransferenciaDTO.builder()
                .idPontoDoador(ponto.getPontoDispensacao().getId())
                .nomePontoDoador(ponto.getPontoDispensacao().getNome())
                .quantidadeDisponivelNoDoador(excedente)
                .previsaoDiasDoador(insumo.getPrevisaoEsgotamentoDias())
                .build();
    }
}

