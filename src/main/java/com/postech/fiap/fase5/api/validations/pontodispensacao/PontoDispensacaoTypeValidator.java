package com.postech.fiap.fase5.api.validations.pontodispensacao;

import com.postech.fiap.fase5.api.dto.PontoDispensacaoDTO;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PontoDispensacaoTypeValidator implements PontoDispensacaoCreateValidation {

    private static final Set<String> VALID_TYPES = Set.of("FARMACIA_CENTRAL", "FARMACIA_MUNICIPAL", "ALMOXARIFADO");

    @Override
    public void validate(PontoDispensacaoDTO dto) {
        if (dto.tipo() == null || !VALID_TYPES.contains(dto.tipo())) {
            throw new IllegalArgumentException("Tipo inválido. Valores permitidos: " + VALID_TYPES);
        }
    }
}
