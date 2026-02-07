package com.postech.fiap.fase5.api.usecases.previsao.domain;

import com.postech.fiap.fase5.api.dto.estimativa.AlertaValidadeDTO;
import com.postech.fiap.fase5.api.dto.estimativa.ItemRiscoValidadeDTO;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlertaValidadeFactory {

    public AlertaValidadeDTO criar(PontoDispensacao pontoDispensacao, List<ItemRiscoValidadeDTO> itensEmRisco) {
        return AlertaValidadeDTO.builder()
                .pontoDispensacao(pontoDispensacao)
                .itensEmRisco(itensEmRisco)
                .build();
    }
}

