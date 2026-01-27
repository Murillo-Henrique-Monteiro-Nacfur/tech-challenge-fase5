package com.postech.fiap.fase5.api.presenter;

import com.postech.fiap.fase5.api.dto.PontoDispensacaoDTO;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import org.springframework.stereotype.Component;

@Component
public class PontoDispensacaoPresenter {

    public PontoDispensacao toEntity(PontoDispensacaoDTO dto) {
        return new PontoDispensacao(
                dto.id(),
                dto.cnes(),
                dto.nome(),
                dto.tipo(),
                dto.emailResponsavel(),
                dto.clientId()
        );
    }

    public PontoDispensacaoDTO toDto(PontoDispensacao entity) {
        return new PontoDispensacaoDTO(
                entity.getId(),
                entity.getCnes(),
                entity.getNome(),
                entity.getTipo(),
                entity.getEmailResponsavel(),
                entity.getClientId()
        );
    }
}
