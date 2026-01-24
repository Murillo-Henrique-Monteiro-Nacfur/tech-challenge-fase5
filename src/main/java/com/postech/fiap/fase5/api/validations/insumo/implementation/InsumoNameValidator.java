package com.postech.fiap.fase5.api.validations.insumo.implementation;

import com.postech.fiap.fase5.api.dto.insumos.InsumoDTO;
import com.postech.fiap.fase5.api.validations.insumo.InsumoCreateValidation;
import org.springframework.stereotype.Component;

@Component
public class InsumoNameValidator implements InsumoCreateValidation {

    @Override
    public void validate(InsumoDTO insumoDTO) {
        if (insumoDTO.nomeGenerico() == null || insumoDTO.nomeGenerico().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome genérico é obrigatório");
        }
    }
}
