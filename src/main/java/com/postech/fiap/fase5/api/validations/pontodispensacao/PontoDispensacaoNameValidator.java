package com.postech.fiap.fase5.api.validations.pontodispensacao;

import com.postech.fiap.fase5.api.dto.PontoDispensacaoDTO;
import org.springframework.stereotype.Component;

@Component
public class PontoDispensacaoNameValidator implements PontoDispensacaoCreateValidation {

    @Override
    public void validate(PontoDispensacaoDTO dto) {
        if (dto.nome() == null || dto.nome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do ponto de dispensação é obrigatório");
        }
    }
}
