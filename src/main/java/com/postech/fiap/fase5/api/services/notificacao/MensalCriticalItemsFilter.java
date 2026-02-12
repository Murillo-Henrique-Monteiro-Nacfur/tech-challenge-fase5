package com.postech.fiap.fase5.api.services.notificacao;

import com.postech.fiap.fase5.api.dto.estimativa.InsumoMensalDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MensalCriticalItemsFilter implements CriticalItemsFilter<InsumoMensalDTO> {

    private static final String CRITICO = "CRITICO";
    private static final String ALERTA = "ALERTA";

    @Override
    public List<InsumoMensalDTO> filterCriticalItems(List<InsumoMensalDTO> items) {
        return items.stream()
                .filter(this::isCriticalOrAlert)
                .toList();
    }

    private boolean isCriticalOrAlert(InsumoMensalDTO item) {
        return CRITICO.equals(item.getStatusPrevisaoSazonal()) || ALERTA.equals(item.getStatusPrevisaoSazonal());
    }
}

