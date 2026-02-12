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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PontoDispensacaoReadUseCaseTest {

    @Mock
    private PontoDispensacaoRepository repository;

    @Mock
    private PontoDispensacaoPresenter presenter;

    @InjectMocks
    private PontoDispensacaoReadUseCase pontoDispensacaoReadUseCase;

    @Test
    void deveBuscarTodosPontosDispensacaoPaginado() {
        Pageable pageable = PageRequest.of(0, 10);
        List<PontoDispensacao> entities = List.of(
                createEntity(1L, "CNES001", "Ponto 1", "UBS", "email1@test.com", 1L),
                createEntity(2L, "CNES002", "Ponto 2", "Hospital", "email2@test.com", 1L)
        );
        Page<PontoDispensacao> entitiesPage = new PageImpl<>(entities, pageable, entities.size());

        PontoDispensacaoDTO dto1 = createDto(1L, "CNES001", "Ponto 1", "UBS", "email1@test.com", 1L);
        PontoDispensacaoDTO dto2 = createDto(2L, "CNES002", "Ponto 2", "Hospital", "email2@test.com", 1L);

        when(repository.findAll(pageable)).thenReturn(entitiesPage);
        when(presenter.toDto(entities.get(0))).thenReturn(dto1);
        when(presenter.toDto(entities.get(1))).thenReturn(dto2);

        Page<PontoDispensacaoDTO> result = pontoDispensacaoReadUseCase.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("CNES001", result.getContent().get(0).cnes());
        assertEquals("CNES002", result.getContent().get(1).cnes());

        verify(repository).findAll(pageable);
        verify(presenter, times(2)).toDto(any(PontoDispensacao.class));
    }

    @Test
    void deveBuscarPontoDispensacaoPorIdComSucesso() {
        Long id = 1L;
        PontoDispensacao entity = createEntity(id, "CNES001", "Ponto Teste", "UBS", "responsavel@email.com", 1L);
        PontoDispensacaoDTO expectedDto = createDto(id, "CNES001", "Ponto Teste", "UBS", "responsavel@email.com", 1L);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(presenter.toDto(entity)).thenReturn(expectedDto);

        PontoDispensacaoDTO result = pontoDispensacaoReadUseCase.findById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("CNES001", result.cnes());
        assertEquals("Ponto Teste", result.nome());
        assertEquals("UBS", result.tipo());
        assertEquals("responsavel@email.com", result.emailResponsavel());

        verify(repository).findById(id);
        verify(presenter).toDto(entity);
    }

    @Test
    void deveLancarExcecaoQuandoPontoDispensacaoNaoEncontradoPorId() {
        Long id = 999L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> pontoDispensacaoReadUseCase.findById(id)
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Ponto de Dispensação não encontrado"));
        assertTrue(exception.getMessage().contains(id.toString()));

        verify(repository).findById(id);
        verify(presenter, never()).toDto(any());
    }

    @Test
    void deveRetornarPaginaVaziaQuandoNaoExistemPontos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PontoDispensacao> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(repository.findAll(pageable)).thenReturn(emptyPage);

        Page<PontoDispensacaoDTO> result = pontoDispensacaoReadUseCase.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());

        verify(repository).findAll(pageable);
        verify(presenter, never()).toDto(any());
    }

    @Test
    void deveRespeitarPaginacaoNaBusca() {
        Pageable pageable = PageRequest.of(1, 5);
        List<PontoDispensacao> entities = List.of(
                createEntity(6L, "CNES006", "Ponto 6", "UBS", "email6@test.com", 1L),
                createEntity(7L, "CNES007", "Ponto 7", "Hospital", "email7@test.com", 1L)
        );
        Page<PontoDispensacao> entitiesPage = new PageImpl<>(entities, pageable, 10);

        when(repository.findAll(pageable)).thenReturn(entitiesPage);
        when(presenter.toDto(any())).thenAnswer(invocation -> {
            PontoDispensacao entity = invocation.getArgument(0);
            return createDto(entity.getId(), entity.getCnes(), entity.getNome(), entity.getTipo(),
                    entity.getEmailResponsavel(), entity.getClientId());
        });

        Page<PontoDispensacaoDTO> result = pontoDispensacaoReadUseCase.findAll(pageable);

        assertEquals(1, result.getNumber());
        assertEquals(5, result.getSize());
        assertEquals(10, result.getTotalElements());
        assertEquals(2, result.getNumberOfElements());
    }

    @Test
    void deveConverterTodosOsCamposNaBuscaPorId() {
        Long id = 10L;
        String cnes = "CNES010";
        String nome = "Ponto Completo";
        String tipo = "Hospital Regional";
        String email = "regional@hospital.com";
        Long clientId = 5L;

        PontoDispensacao entity = createEntity(id, cnes, nome, tipo, email, clientId);
        PontoDispensacaoDTO expectedDto = createDto(id, cnes, nome, tipo, email, clientId);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(presenter.toDto(entity)).thenReturn(expectedDto);

        PontoDispensacaoDTO result = pontoDispensacaoReadUseCase.findById(id);

        assertEquals(id, result.id());
        assertEquals(cnes, result.cnes());
        assertEquals(nome, result.nome());
        assertEquals(tipo, result.tipo());
        assertEquals(email, result.emailResponsavel());
        assertEquals(clientId, result.clientId());
    }

    @Test
    void deveConsultarRepositorioApenasumaVezNaBuscaPorId() {
        Long id = 1L;
        PontoDispensacao entity = createEntity(id, "CNES001", "Ponto", "UBS", "email@test.com", 1L);
        PontoDispensacaoDTO dto = createDto(id, "CNES001", "Ponto", "UBS", "email@test.com", 1L);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(presenter.toDto(entity)).thenReturn(dto);

        pontoDispensacaoReadUseCase.findById(id);

        verify(repository, times(1)).findById(id);
    }

    @Test
    void deveLancarExcecaoComMensagemApropriadaQuandoIdNaoExiste() {
        Long id = 12345L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> pontoDispensacaoReadUseCase.findById(id)
        );

        String expectedMessage = "Ponto de Dispensação não encontrado com ID: " + id;
        assertEquals(expectedMessage, exception.getMessage());
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

