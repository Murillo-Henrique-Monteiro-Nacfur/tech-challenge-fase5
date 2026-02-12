package com.postech.fiap.fase5.api.services.notificacao;

import java.time.LocalDate;

public final class EmailSubjectFactory {

    private static final String DAILY_ALERT_PREFIX = "[ALERTA DIÁRIO] Risco de Desabastecimento - ";
    private static final String MONTHLY_ALERT_PREFIX = "[ALERTA MENSAL] Previsão Sazonal de Estoque - ";
    private static final String VALIDITY_ALERT_PREFIX = "[ALERTA DE VALIDADE] Risco de Perda de Estoque - ";

    private EmailSubjectFactory() {
    }

    public static String createDailyAlertSubject() {
        return DAILY_ALERT_PREFIX + LocalDate.now();
    }

    public static String createMonthlyAlertSubject() {
        return MONTHLY_ALERT_PREFIX + LocalDate.now();
    }

    public static String createValidityAlertSubject() {
        return VALIDITY_ALERT_PREFIX + LocalDate.now();
    }
}

