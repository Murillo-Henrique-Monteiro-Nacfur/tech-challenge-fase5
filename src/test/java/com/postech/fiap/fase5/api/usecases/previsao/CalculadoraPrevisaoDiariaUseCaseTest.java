package com.postech.fiap.fase5.api.usecases.previsao;

import com.postech.fiap.fase5.api.dto.estimativa.InventarioDiarioDTO;
import com.postech.fiap.fase5.api.usecases.previsao.processadores.ProcessadorPrevisaoPonto;
import com.postech.fiap.fase5.api.usecases.previsao.transferencia.GeradorSugestoesTransferencia;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CalculadoraPrevisaoDiariaUseCaseTest {

    @Mock
    private ProcessadorPrevisaoPonto processadorPrevisaoPonto;

    @Mock
    private GeradorSugestoesTransferencia geradorSugestoesTransferencia;

    @InjectMocks
    private CalculadoraPrevisaoDiariaUseCase calculadoraPrevisaoDiariaUseCase;

    @Test
    void deveExecutarProcessamentoESugestoes() {
        InventarioDiarioDTO primeiroInventario = InventarioDiarioDTO.builder().build();
        InventarioDiarioDTO segundoInventario = InventarioDiarioDTO.builder().build();
        List<InventarioDiarioDTO> dadosBrutos = List.of(primeiroInventario, segundoInventario);

        List<InventarioDiarioDTO> resultado = calculadoraPrevisaoDiariaUseCase.execute(dadosBrutos);

        verify(processadorPrevisaoPonto).processar(primeiroInventario);
        verify(processadorPrevisaoPonto).processar(segundoInventario);
        verify(geradorSugestoesTransferencia).gerar(dadosBrutos);
        assertEquals(dadosBrutos, resultado);
    }
}

