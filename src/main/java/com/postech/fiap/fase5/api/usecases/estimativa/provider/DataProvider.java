package com.postech.fiap.fase5.api.usecases.estimativa.provider;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataProvider {

    private static final int DIAS_HISTORICO_DIARIO = 30;
    private static final int MESES_ADICIONAR_ESTIMATIVA_MENSAL = 1;

    public LocalDateTime obterDataAtual() {
        return LocalDateTime.now();
    }

    public LocalDateTime obterDataInicioHistoricoDiario() {
        return obterDataAtual().minusDays(DIAS_HISTORICO_DIARIO);
    }

    public LocalDateTime obterDataFimHistoricoDiario() {
        return obterDataAtual().plusDays(1);
    }

    public LocalDateTime obterProximoMes() {
        return obterDataAtual().plusMonths(MESES_ADICIONAR_ESTIMATIVA_MENSAL);
    }
}

