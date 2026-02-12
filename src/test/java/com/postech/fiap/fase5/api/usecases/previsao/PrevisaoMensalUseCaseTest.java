package com.postech.fiap.fase5.api.usecases.previsao;

import com.postech.fiap.fase5.api.dto.estimativa.InventarioMensalDTO;
import com.postech.fiap.fase5.api.services.NotificacaoService;
import com.postech.fiap.fase5.api.usecases.DadosEstimativaUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrevisaoMensalUseCaseTest {

    @Mock
    private DadosEstimativaUseCase dadosEstimativaUseCase;

    @Mock
    private CalculadoraPrevisaoMensalUseCase calculadoraPrevisaoMensalUseCase;

    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private PrevisaoMensalUseCase previsaoMensalUseCase;

    @Test
    void deveExecutarFluxoCompletoDePrevisaoMensal() {
        InventarioMensalDTO inventarioPrimeiro = InventarioMensalDTO.builder().build();
        InventarioMensalDTO inventarioSegundo = InventarioMensalDTO.builder().build();
        List<InventarioMensalDTO> dadosEstimativa = List.of(inventarioPrimeiro, inventarioSegundo);
        List<InventarioMensalDTO> previsaoCalculada = List.of(inventarioPrimeiro, inventarioSegundo);

        when(dadosEstimativaUseCase.executeMensal()).thenReturn(dadosEstimativa);
        when(calculadoraPrevisaoMensalUseCase.execute(dadosEstimativa)).thenReturn(previsaoCalculada);

        List<InventarioMensalDTO> resultado = previsaoMensalUseCase.execute();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(previsaoCalculada, resultado);
        verify(dadosEstimativaUseCase).executeMensal();
        verify(calculadoraPrevisaoMensalUseCase).execute(dadosEstimativa);
        verify(notificacaoService).notificarPrevisaoMensal(previsaoCalculada);
    }

    @Test
    void deveExecutarFluxoComListaVazia() {
        List<InventarioMensalDTO> dadosEstimativaVazia = List.of();
        List<InventarioMensalDTO> previsaoVazia = List.of();

        when(dadosEstimativaUseCase.executeMensal()).thenReturn(dadosEstimativaVazia);
        when(calculadoraPrevisaoMensalUseCase.execute(dadosEstimativaVazia)).thenReturn(previsaoVazia);

        List<InventarioMensalDTO> resultado = previsaoMensalUseCase.execute();

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
        verify(dadosEstimativaUseCase).executeMensal();
        verify(calculadoraPrevisaoMensalUseCase).execute(dadosEstimativaVazia);
        verify(notificacaoService).notificarPrevisaoMensal(previsaoVazia);
    }

    @Test
    void deveExecutarNotificacaoIndependentementeDoResultado() {
        List<InventarioMensalDTO> dadosEstimativa = List.of(InventarioMensalDTO.builder().build());
        List<InventarioMensalDTO> previsaoCalculada = List.of(InventarioMensalDTO.builder().build());

        when(dadosEstimativaUseCase.executeMensal()).thenReturn(dadosEstimativa);
        when(calculadoraPrevisaoMensalUseCase.execute(dadosEstimativa)).thenReturn(previsaoCalculada);

        previsaoMensalUseCase.execute();

        verify(notificacaoService, times(1)).notificarPrevisaoMensal(previsaoCalculada);
    }

    @Test
    void deveRespeitarOrdemDeExecucaoDasOperacoes() {
        List<InventarioMensalDTO> dadosEstimativa = List.of(InventarioMensalDTO.builder().build());
        List<InventarioMensalDTO> previsaoCalculada = List.of(InventarioMensalDTO.builder().build());

        when(dadosEstimativaUseCase.executeMensal()).thenReturn(dadosEstimativa);
        when(calculadoraPrevisaoMensalUseCase.execute(dadosEstimativa)).thenReturn(previsaoCalculada);

        previsaoMensalUseCase.execute();

        var inOrder = inOrder(dadosEstimativaUseCase, calculadoraPrevisaoMensalUseCase, notificacaoService);
        inOrder.verify(dadosEstimativaUseCase).executeMensal();
        inOrder.verify(calculadoraPrevisaoMensalUseCase).execute(dadosEstimativa);
        inOrder.verify(notificacaoService).notificarPrevisaoMensal(previsaoCalculada);
    }
}

