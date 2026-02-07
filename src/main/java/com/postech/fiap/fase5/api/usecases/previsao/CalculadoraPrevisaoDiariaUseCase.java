package com.postech.fiap.fase5.api.usecases.previsao;

import com.postech.fiap.fase5.api.dto.estimativa.InventarioDiarioDTO;
import com.postech.fiap.fase5.api.usecases.previsao.processadores.ProcessadorPrevisaoPonto;
import com.postech.fiap.fase5.api.usecases.previsao.transferencia.GeradorSugestoesTransferencia;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculadoraPrevisaoDiariaUseCase {

    private final ProcessadorPrevisaoPonto processadorPrevisaoPonto;
    private final GeradorSugestoesTransferencia geradorSugestoesTransferencia;

    public List<InventarioDiarioDTO> execute(List<InventarioDiarioDTO> dadosBrutos) {
        dadosBrutos.forEach(processadorPrevisaoPonto::processar);
        geradorSugestoesTransferencia.gerar(dadosBrutos);
        return dadosBrutos;
    }
}
