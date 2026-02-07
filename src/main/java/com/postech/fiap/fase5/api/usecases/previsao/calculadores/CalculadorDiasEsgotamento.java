package com.postech.fiap.fase5.api.usecases.previsao.calculadores;

import org.springframework.stereotype.Component;

import static com.postech.fiap.fase5.api.usecases.previsao.constantes.ConstantesPrevisao.VALOR_SEM_PREVISAO;

@Component
public class CalculadorDiasEsgotamento {

    public int calcular(int quantidadeEstoque, double consumoMedioDiario) {
        if (consumoMedioDiario <= 0) {
            return VALOR_SEM_PREVISAO;
        }

        return (int) (quantidadeEstoque / consumoMedioDiario);
    }
}

