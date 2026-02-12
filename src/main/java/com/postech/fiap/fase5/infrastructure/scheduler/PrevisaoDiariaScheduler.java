package com.postech.fiap.fase5.infrastructure.scheduler;

import com.postech.fiap.fase5.api.usecases.previsao.PrevisaoDiariaUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PrevisaoDiariaScheduler {

    private final PrevisaoDiariaUseCase previsaoDiariaUseCase;

    @Scheduled(cron = "0 0 2 * * *")
    public void executarPrevisaoDiaria() {
        log.info("Iniciando execucao da previsao diaria");
        var resultado = previsaoDiariaUseCase.execute();
        log.info("Previsao diaria executada com sucesso. Total de itens processados: {}", resultado.size());
    }
}
