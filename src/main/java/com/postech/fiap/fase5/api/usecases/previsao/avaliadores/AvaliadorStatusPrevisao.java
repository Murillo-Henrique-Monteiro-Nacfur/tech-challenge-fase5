package com.postech.fiap.fase5.api.usecases.previsao.avaliadores;

import org.springframework.stereotype.Component;

import static com.postech.fiap.fase5.api.usecases.previsao.constantes.ConstantesPrevisao.*;

@Component
public class AvaliadorStatusPrevisao {

    public String avaliar(double consumoMedioDiario, int diasRestantes) {
        if (consumoMedioDiario <= 0) {
            return STATUS_SEM_CONSUMO;
        }

        if (diasRestantes <= LIMITE_STATUS_CRITICO_DIAS) {
            return STATUS_CRITICO;
        }

        if (diasRestantes <= LIMITE_STATUS_ALERTA_DIAS) {
            return STATUS_ALERTA;
        }

        return STATUS_NORMAL;
    }
}

