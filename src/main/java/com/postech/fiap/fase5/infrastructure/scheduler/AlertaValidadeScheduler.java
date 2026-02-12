                          package com.postech.fiap.fase5.infrastructure.scheduler;

import com.postech.fiap.fase5.api.usecases.previsao.AlertaValidadeUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertaValidadeScheduler {

    private final AlertaValidadeUseCase alertaValidadeUseCase;

    @Scheduled(cron = "0 0 6 * * 1")
    public void executarAlertaValidade() {
        log.info("Iniciando execucao do alerta de validade");
        var resultado = alertaValidadeUseCase.execute();
        log.info("Alerta de validade executado com sucesso. Total de alertas gerados: {}", resultado.size());
    }
}

