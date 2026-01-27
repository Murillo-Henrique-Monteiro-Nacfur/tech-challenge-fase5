package com.postech.fiap.fase5.api.validations;

import com.postech.fiap.fase5.api.dto.RegistroConsumoDTO;

public interface ConsumoValidation {
    void validate(RegistroConsumoDTO dto, Long clientId);
}
