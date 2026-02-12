package com.postech.fiap.fase5.api.usecases.previsao.domain;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class RiscoValidadeDetector {

    private static final int JANELA_ALERTA_DIAS = 60;
    private static final long DIAS_MINIMOS_VALIDADE = 1;

    public boolean estaEmJanelaDeRisco(LocalDate dataValidade, LocalDate dataReferencia) {
        if (dataValidade == null) {
            return false;
        }

        LocalDate limiteAlerta = dataReferencia.plusDays(JANELA_ALERTA_DIAS);
        return !dataValidade.isBefore(dataReferencia) && dataValidade.isBefore(limiteAlerta);
    }

    public long calcularDiasParaVencer(LocalDate dataValidade, LocalDate dataReferencia) {
        long dias = ChronoUnit.DAYS.between(dataReferencia, dataValidade);
        return Math.max(dias, DIAS_MINIMOS_VALIDADE);
    }

    public int calcularSobraPrevista(int quantidadeAtual, double mediaDiaria, long diasParaVencer) {
        double consumoPrevistoAteVencimento = mediaDiaria * diasParaVencer;
        return (int) (quantidadeAtual - consumoPrevistoAteVencimento);
    }

    public boolean existeRiscoDeDesperdicio(int sobraPrevista) {
        return sobraPrevista > 0;
    }
}

