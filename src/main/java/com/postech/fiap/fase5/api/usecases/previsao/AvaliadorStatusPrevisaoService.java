package com.postech.fiap.fase5.api.usecases.previsao;
import org.springframework.stereotype.Service;
@Service
public class AvaliadorStatusPrevisaoService {
    private static final int LIMIAR_CRITICO = 5;
    private static final int LIMIAR_ALERTA = 15;
    public String avaliar(int diasRestantes) {
        if (diasRestantes <= LIMIAR_CRITICO) {
            return "CRITICO";
        }
        if (diasRestantes <= LIMIAR_ALERTA) {
            return "ALERTA";
        }
        return "NORMAL";
    }
}
