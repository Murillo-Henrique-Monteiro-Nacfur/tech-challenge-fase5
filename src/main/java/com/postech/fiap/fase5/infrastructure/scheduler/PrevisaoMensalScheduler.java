package com.postech.fiap.fase5.infrastructure.scheduler;

import com.postech.fiap.fase5.api.usecases.previsao.PrevisaoMensalUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PrevisaoMensalScheduler {

    private final PrevisaoMensalUseCase previsaoMensalUseCase;

    @Scheduled(cron = "0 0 0 1 * *")
    public void executarPrevisaoMensal() {
        log.info("Iniciando execucao da previsao mensal");
        var resultado = previsaoMensalUseCase.execute();
        log.info("Previsao mensal executada com sucesso. Total de itens processados: {}", resultado.size());
    }
}

