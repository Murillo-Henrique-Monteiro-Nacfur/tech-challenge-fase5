package com.postech.fiap.fase5.api.usecases.previsao;

import com.postech.fiap.fase5.api.dto.estimativa.InventarioDiarioDTO;
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
class PrevisaoDiariaUseCaseTest {

    @Mock
    private DadosEstimativaUseCase dadosEstimativaUseCase;

    @Mock
    private CalculadoraPrevisaoDiariaUseCase calculadoraPrevisaoDiariaUseCase;

    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private PrevisaoDiariaUseCase previsaoDiariaUseCase;

    @Test
    void deveExecutarFluxoCompletoDePrevisaoDiaria() {
        InventarioDiarioDTO inventarioPrimeiro = InventarioDiarioDTO.builder().build();
        InventarioDiarioDTO inventarioSegundo = InventarioDiarioDTO.builder().build();
        List<InventarioDiarioDTO> dadosEstimativa = List.of(inventarioPrimeiro, inventarioSegundo);
        List<InventarioDiarioDTO> previsaoCalculada = List.of(inventarioPrimeiro, inventarioSegundo);

        when(dadosEstimativaUseCase.executeDiaria()).thenReturn(dadosEstimativa);
        when(calculadoraPrevisaoDiariaUseCase.execute(dadosEstimativa)).thenReturn(previsaoCalculada);

        List<InventarioDiarioDTO> resultado = previsaoDiariaUseCase.execute();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(previsaoCalculada, resultado);
        verify(dadosEstimativaUseCase).executeDiaria();
        verify(calculadoraPrevisaoDiariaUseCase).execute(dadosEstimativa);
        verify(notificacaoService).notificarPrevisaoDiaria(previsaoCalculada);
    }

    @Test
    void deveExecutarFluxoComListaVazia() {
        List<InventarioDiarioDTO> dadosEstimativaVazia = List.of();
        List<InventarioDiarioDTO> previsaoVazia = List.of();

        when(dadosEstimativaUseCase.executeDiaria()).thenReturn(dadosEstimativaVazia);
        when(calculadoraPrevisaoDiariaUseCase.execute(dadosEstimativaVazia)).thenReturn(previsaoVazia);

        List<InventarioDiarioDTO> resultado = previsaoDiariaUseCase.execute();

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
        verify(dadosEstimativaUseCase).executeDiaria();
        verify(calculadoraPrevisaoDiariaUseCase).execute(dadosEstimativaVazia);
        verify(notificacaoService).notificarPrevisaoDiaria(previsaoVazia);
    }

    @Test
    void deveExecutarNotificacaoIndependentementeDoResultado() {
        List<InventarioDiarioDTO> dadosEstimativa = List.of(InventarioDiarioDTO.builder().build());
        List<InventarioDiarioDTO> previsaoCalculada = List.of(InventarioDiarioDTO.builder().build());

        when(dadosEstimativaUseCase.executeDiaria()).thenReturn(dadosEstimativa);
        when(calculadoraPrevisaoDiariaUseCase.execute(dadosEstimativa)).thenReturn(previsaoCalculada);

        previsaoDiariaUseCase.execute();

        verify(notificacaoService, times(1)).notificarPrevisaoDiaria(previsaoCalculada);
    }

    @Test
    void deveRespeitarOrdemDeExecucaoDasOperacoes() {
        List<InventarioDiarioDTO> dadosEstimativa = List.of(InventarioDiarioDTO.builder().build());
        List<InventarioDiarioDTO> previsaoCalculada = List.of(InventarioDiarioDTO.builder().build());

        when(dadosEstimativaUseCase.executeDiaria()).thenReturn(dadosEstimativa);
        when(calculadoraPrevisaoDiariaUseCase.execute(dadosEstimativa)).thenReturn(previsaoCalculada);

        previsaoDiariaUseCase.execute();

        var inOrder = inOrder(dadosEstimativaUseCase, calculadoraPrevisaoDiariaUseCase, notificacaoService);
        inOrder.verify(dadosEstimativaUseCase).executeDiaria();
        inOrder.verify(calculadoraPrevisaoDiariaUseCase).execute(dadosEstimativa);
        inOrder.verify(notificacaoService).notificarPrevisaoDiaria(previsaoCalculada);
    }
}

