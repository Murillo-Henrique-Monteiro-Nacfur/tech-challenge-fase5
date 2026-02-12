package com.postech.fiap.fase5.api.usecases.pontodispensacao;

import com.postech.fiap.fase5.api.repositories.PontoDispensacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PontoDispensacaoDeleteUseCaseTest {

    @Mock
    private PontoDispensacaoRepository repository;

    @InjectMocks
    private PontoDispensacaoDeleteUseCase pontoDispensacaoDeleteUseCase;

    @Test
    void deveDeletarPontoDispensacaoComSucesso() {
        Long id = 1L;

        when(repository.existsById(id)).thenReturn(true);
        doNothing().when(repository).deleteById(id);

        assertDoesNotThrow(() -> pontoDispensacaoDeleteUseCase.execute(id));

        verify(repository).existsById(id);
        verify(repository).deleteById(id);
    }

    @Test
    void deveLancarExcecaoQuandoPontoDispensacaoNaoExiste() {
        Long id = 999L;

        when(repository.existsById(id)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> pontoDispensacaoDeleteUseCase.execute(id)
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Ponto de Dispensação não encontrado"));
        assertTrue(exception.getMessage().contains(id.toString()));

        verify(repository).existsById(id);
        verify(repository, never()).deleteById(any());
    }

    @Test
    void deveValidarExistenciaAntesDeDelatar() {
        Long id = 5L;

        when(repository.existsById(id)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> pontoDispensacaoDeleteUseCase.execute(id));

        verify(repository).existsById(id);
        verify(repository, never()).deleteById(id);
    }

    @Test
    void deveConsultarExistenciaApenasumaVez() {
        Long id = 10L;

        when(repository.existsById(id)).thenReturn(true);

        pontoDispensacaoDeleteUseCase.execute(id);

        verify(repository, times(1)).existsById(id);
    }

    @Test
    void deveDeletarApenasumaVez() {
        Long id = 20L;

        when(repository.existsById(id)).thenReturn(true);

        pontoDispensacaoDeleteUseCase.execute(id);

        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void deveLancarExcecaoComMensagemApropriadaQuandoNaoExiste() {
        Long id = 12345L;

        when(repository.existsById(id)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> pontoDispensacaoDeleteUseCase.execute(id)
        );

        String expectedMessage = "Ponto de Dispensação não encontrado com ID: " + id;
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void deveExecutarFluxoCompletoParaDeletarPontoExistente() {
        Long id = 100L;

        when(repository.existsById(id)).thenReturn(true);
        doNothing().when(repository).deleteById(id);

        pontoDispensacaoDeleteUseCase.execute(id);

        verify(repository).existsById(id);
        verify(repository).deleteById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void deveInterromperFluxoQuandoPontoNaoExiste() {
        Long id = 200L;

        when(repository.existsById(id)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> pontoDispensacaoDeleteUseCase.execute(id));

        verify(repository).existsById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void deveDeletarMultiplosPontosSequencialmente() {
        Long id1 = 1L;
        Long id2 = 2L;
        Long id3 = 3L;

        when(repository.existsById(id1)).thenReturn(true);
        when(repository.existsById(id2)).thenReturn(true);
        when(repository.existsById(id3)).thenReturn(true);

        pontoDispensacaoDeleteUseCase.execute(id1);
        pontoDispensacaoDeleteUseCase.execute(id2);
        pontoDispensacaoDeleteUseCase.execute(id3);

        verify(repository).deleteById(id1);
        verify(repository).deleteById(id2);
        verify(repository).deleteById(id3);
    }
}

