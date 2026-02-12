package com.postech.fiap.fase5.api.usecases.pontodispensacao;

import com.postech.fiap.fase5.api.dto.PontoDispensacaoDTO;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import com.postech.fiap.fase5.api.presenter.PontoDispensacaoPresenter;
import com.postech.fiap.fase5.api.repositories.PontoDispensacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PontoDispensacaoUpdateUseCaseTest {

    @Mock
    private PontoDispensacaoRepository repository;

    @Mock
    private PontoDispensacaoPresenter presenter;

    @InjectMocks
    private PontoDispensacaoUpdateUseCase pontoDispensacaoUpdateUseCase;

    @Test
    void deveAtualizarPontoDispensacaoComSucesso() {
        Long id = 1L;
        PontoDispensacao existingEntity = createEntity(id, "CNES001", "Nome Antigo", "UBS", "old@email.com", 1L);
        PontoDispensacaoDTO updateDto = createDto(null, "CNES002", "Nome Novo", "Hospital", "new@email.com", 1L);

        PontoDispensacao updatedEntity = createEntity(id, "CNES002", "Nome Novo", "Hospital", "new@email.com", 1L);
        PontoDispensacaoDTO expectedDto = createDto(id, "CNES002", "Nome Novo", "Hospital", "new@email.com", 1L);

        when(repository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(repository.save(any(PontoDispensacao.class))).thenReturn(updatedEntity);
        when(presenter.toDto(updatedEntity)).thenReturn(expectedDto);

        PontoDispensacaoDTO result = pontoDispensacaoUpdateUseCase.execute(id, updateDto);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("CNES002", result.cnes());
        assertEquals("Nome Novo", result.nome());
        assertEquals("Hospital", result.tipo());
        assertEquals("new@email.com", result.emailResponsavel());

        verify(repository).findById(id);
        verify(repository).save(any(PontoDispensacao.class));
        verify(presenter).toDto(updatedEntity);
    }

    @Test
    void deveLancarExcecaoQuandoPontoDispensacaoNaoEncontrado() {
        Long id = 999L;
        PontoDispensacaoDTO updateDto = createDto(null, "CNES999", "Novo Nome", "UBS", "email@test.com", 1L);

        when(repository.findById(id)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> pontoDispensacaoUpdateUseCase.execute(id, updateDto)
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Ponto de Dispensação não encontrado"));
        assertTrue(exception.getMessage().contains(id.toString()));

        verify(repository).findById(id);
        verify(repository, never()).save(any());
        verify(presenter, never()).toDto(any());
    }

    @Test
    void deveAtualizarTodosOsCampos() {
        Long id = 5L;
        PontoDispensacao existingEntity = createEntity(id, "OLD_CNES", "Old Name", "Old Type", "old@email.com", 1L);
        PontoDispensacaoDTO updateDto = createDto(null, "NEW_CNES", "New Name", "New Type", "new@email.com", 2L);

        when(repository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(repository.save(any(PontoDispensacao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(presenter.toDto(any())).thenAnswer(invocation -> {
            PontoDispensacao entity = invocation.getArgument(0);
            return createDto(entity.getId(), entity.getCnes(), entity.getNome(), entity.getTipo(),
                    entity.getEmailResponsavel(), entity.getClientId());
        });

        PontoDispensacaoDTO result = pontoDispensacaoUpdateUseCase.execute(id, updateDto);

        assertEquals("NEW_CNES", result.cnes());
        assertEquals("New Name", result.nome());
        assertEquals("New Type", result.tipo());
        assertEquals("new@email.com", result.emailResponsavel());
    }

    @Test
    void devePreservarIdAoAtualizar() {
        Long id = 10L;
        PontoDispensacao existingEntity = createEntity(id, "CNES001", "Nome", "UBS", "email@test.com", 1L);
        PontoDispensacaoDTO updateDto = createDto(null, "CNES002", "Novo Nome", "Hospital", "novo@email.com", 1L);

        when(repository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(repository.save(any(PontoDispensacao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(presenter.toDto(any())).thenAnswer(invocation -> {
            PontoDispensacao entity = invocation.getArgument(0);
            return createDto(entity.getId(), entity.getCnes(), entity.getNome(), entity.getTipo(),
                    entity.getEmailResponsavel(), entity.getClientId());
        });

        PontoDispensacaoDTO result = pontoDispensacaoUpdateUseCase.execute(id, updateDto);

        assertEquals(id, result.id());
    }

    @Test
    void deveAtualizarCnesCorretamente() {
        Long id = 1L;
        PontoDispensacao existingEntity = createEntity(id, "CNES001", "Nome", "UBS", "email@test.com", 1L);
        PontoDispensacaoDTO updateDto = createDto(null, "CNES999", "Nome", "UBS", "email@test.com", 1L);

        when(repository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(repository.save(any(PontoDispensacao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(presenter.toDto(any())).thenAnswer(invocation -> {
            PontoDispensacao entity = invocation.getArgument(0);
            return createDto(entity.getId(), entity.getCnes(), entity.getNome(), entity.getTipo(),
                    entity.getEmailResponsavel(), entity.getClientId());
        });

        PontoDispensacaoDTO result = pontoDispensacaoUpdateUseCase.execute(id, updateDto);

        assertEquals("CNES999", result.cnes());
    }

    @Test
    void deveAtualizarNomeCorretamente() {
        Long id = 2L;
        PontoDispensacao existingEntity = createEntity(id, "CNES001", "Nome Antigo", "UBS", "email@test.com", 1L);
        PontoDispensacaoDTO updateDto = createDto(null, "CNES001", "Nome Atualizado", "UBS", "email@test.com", 1L);

        when(repository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(repository.save(any(PontoDispensacao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(presenter.toDto(any())).thenAnswer(invocation -> {
            PontoDispensacao entity = invocation.getArgument(0);
            return createDto(entity.getId(), entity.getCnes(), entity.getNome(), entity.getTipo(),
                    entity.getEmailResponsavel(), entity.getClientId());
        });

        PontoDispensacaoDTO result = pontoDispensacaoUpdateUseCase.execute(id, updateDto);

        assertEquals("Nome Atualizado", result.nome());
    }

    @Test
    void deveAtualizarTipoCorretamente() {
        Long id = 3L;
        PontoDispensacao existingEntity = createEntity(id, "CNES001", "Nome", "UBS", "email@test.com", 1L);
        PontoDispensacaoDTO updateDto = createDto(null, "CNES001", "Nome", "Hospital Regional", "email@test.com", 1L);

        when(repository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(repository.save(any(PontoDispensacao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(presenter.toDto(any())).thenAnswer(invocation -> {
            PontoDispensacao entity = invocation.getArgument(0);
            return createDto(entity.getId(), entity.getCnes(), entity.getNome(), entity.getTipo(),
                    entity.getEmailResponsavel(), entity.getClientId());
        });

        PontoDispensacaoDTO result = pontoDispensacaoUpdateUseCase.execute(id, updateDto);

        assertEquals("Hospital Regional", result.tipo());
    }

    @Test
    void deveAtualizarEmailResponsavelCorretamente() {
        Long id = 4L;
        PontoDispensacao existingEntity = createEntity(id, "CNES001", "Nome", "UBS", "old@email.com", 1L);
        PontoDispensacaoDTO updateDto = createDto(null, "CNES001", "Nome", "UBS", "new@email.com", 1L);

        when(repository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(repository.save(any(PontoDispensacao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(presenter.toDto(any())).thenAnswer(invocation -> {
            PontoDispensacao entity = invocation.getArgument(0);
            return createDto(entity.getId(), entity.getCnes(), entity.getNome(), entity.getTipo(),
                    entity.getEmailResponsavel(), entity.getClientId());
        });

        PontoDispensacaoDTO result = pontoDispensacaoUpdateUseCase.execute(id, updateDto);

        assertEquals("new@email.com", result.emailResponsavel());
    }

    @Test
    void deveConsultarRepositorioApenasumaVez() {
        Long id = 1L;
        PontoDispensacao existingEntity = createEntity(id, "CNES001", "Nome", "UBS", "email@test.com", 1L);
        PontoDispensacaoDTO updateDto = createDto(null, "CNES002", "Novo Nome", "Hospital", "novo@email.com", 1L);

        when(repository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(repository.save(any(PontoDispensacao.class))).thenReturn(existingEntity);
        when(presenter.toDto(any())).thenReturn(updateDto);

        pontoDispensacaoUpdateUseCase.execute(id, updateDto);

        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(any());
    }

    private PontoDispensacaoDTO createDto(Long id, String cnes, String nome, String tipo, String email, Long clientId) {
        return new PontoDispensacaoDTO(id, cnes, nome, tipo, email, clientId);
    }

    private PontoDispensacao createEntity(Long id, String cnes, String nome, String tipo, String email, Long clientId) {
        PontoDispensacao entity = new PontoDispensacao();
        entity.setId(id);
        entity.setCnes(cnes);
        entity.setNome(nome);
        entity.setTipo(tipo);
        entity.setEmailResponsavel(email);
        entity.setClientId(clientId);
        return entity;
    }
}

