package com.postech.fiap.fase5.api.validations;

import com.postech.fiap.fase5.api.dto.insumos.InsumoDTO;

public interface InsumoCreateValidation {
    void validate(InsumoDTO insumoDTO);
}
