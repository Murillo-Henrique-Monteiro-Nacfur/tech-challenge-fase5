package com.postech.fiap.fase5.api.services.notificacao;

import com.postech.fiap.fase5.api.dto.estimativa.InsumoDiarioDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DiarioCriticalItemsFilter implements CriticalItemsFilter<InsumoDiarioDTO> {

    private static final String CRITICO = "CRITICO";
    private static final String ALERTA = "ALERTA";

    @Override
    public List<InsumoDiarioDTO> filterCriticalItems(List<InsumoDiarioDTO> items) {
        return items.stream()
                .filter(this::isCriticalOrAlert)
                .toList();
    }

    private boolean isCriticalOrAlert(InsumoDiarioDTO item) {
        return CRITICO.equals(item.getStatusPrevisao()) || ALERTA.equals(item.getStatusPrevisao());
    }
}

