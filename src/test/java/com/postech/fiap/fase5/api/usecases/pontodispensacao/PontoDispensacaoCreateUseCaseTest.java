package com.postech.fiap.fase5.api.usecases.pontodispensacao;

import com.postech.fiap.fase5.api.dto.PontoDispensacaoDTO;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import com.postech.fiap.fase5.api.presenter.PontoDispensacaoPresenter;
import com.postech.fiap.fase5.api.repositories.PontoDispensacaoRepository;
import com.postech.fiap.fase5.api.validations.pontodispensacao.PontoDispensacaoCreateValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PontoDispensacaoCreateUseCaseTest {

    @Mock
    private PontoDispensacaoRepository repository;

    @Mock
    private PontoDispensacaoCreateValidation validation;

    @Mock
    private PontoDispensacaoPresenter presenter;

    private PontoDispensacaoCreateUseCase pontoDispensacaoCreateUseCase;

    @BeforeEach
    void setUp() {
        pontoDispensacaoCreateUseCase = new PontoDispensacaoCreateUseCase(
                repository,
                List.of(validation),
                presenter
        );
    }

    @Test
    void deveCriarPontoDispensacaoComSucesso() {
        PontoDispensacaoDTO inputDto = createDto(null, "CNES001", "Ponto Teste", "UBS", "responsavel@email.com", 1L);
        PontoDispensacao entity = createEntity(null, "CNES001", "Ponto Teste", "UBS", "responsavel@email.com", 1L);
        PontoDispensacao savedEntity = createEntity(1L, "CNES001", "Ponto Teste", "UBS", "responsavel@email.com", 1L);
        PontoDispensacaoDTO expectedDto = createDto(1L, "CNES001", "Ponto Teste", "UBS", "responsavel@email.com", 1L);

        when(presenter.toEntity(inputDto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(presenter.toDto(savedEntity)).thenReturn(expectedDto);

        PontoDispensacaoDTO result = pontoDispensacaoCreateUseCase.execute(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("CNES001", result.cnes());
        assertEquals("Ponto Teste", result.nome());
        assertEquals("UBS", result.tipo());
        assertEquals("responsavel@email.com", result.emailResponsavel());

        verify(validation).validate(inputDto);
        verify(presenter).toEntity(inputDto);
        verify(repository).save(entity);
        verify(presenter).toDto(savedEntity);
    }

    @Test
    void deveExecutarTodasValidacoesAntesDecriar() {
        PontoDispensacaoCreateValidation validation1 = mock(PontoDispensacaoCreateValidation.class);
        PontoDispensacaoCreateValidation validation2 = mock(PontoDispensacaoCreateValidation.class);
        List<PontoDispensacaoCreateValidation> validations = List.of(validation1, validation2);

        PontoDispensacaoCreateUseCase useCase = new PontoDispensacaoCreateUseCase(
                repository,
                validations,
                presenter
        );

        PontoDispensacaoDTO dto = createDto(null, "CNES002", "Ponto", "UBS", "email@test.com", 1L);
        PontoDispensacao entity = createEntity(null, "CNES002", "Ponto", "UBS", "email@test.com", 1L);
        PontoDispensacao savedEntity = createEntity(1L, "CNES002", "Ponto", "UBS", "email@test.com", 1L);

        when(presenter.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(presenter.toDto(savedEntity)).thenReturn(dto);

        useCase.execute(dto);

        verify(validation1).validate(dto);
        verify(validation2).validate(dto);
    }

    @Test
    void deveLancarExcecaoQuandoValidacaoFalhar() {
        PontoDispensacaoDTO dto = createDto(null, "", "Ponto", "UBS", "email@test.com", 1L);

        doThrow(new IllegalArgumentException("CNES é obrigatório"))
                .when(validation).validate(dto);

        assertThrows(IllegalArgumentException.class, () -> pontoDispensacaoCreateUseCase.execute(dto));

        verify(validation).validate(dto);
        verify(presenter, never()).toEntity(any());
        verify(repository, never()).save(any());
    }

    @Test
    void deveConverterDtoParaEntityAntesDePersistir() {
        PontoDispensacaoDTO dto = createDto(null, "CNES003", "Ponto 3", "Hospital", "admin@hospital.com", 2L);
        PontoDispensacao entity = createEntity(null, "CNES003", "Ponto 3", "Hospital", "admin@hospital.com", 2L);
        PontoDispensacao savedEntity = createEntity(5L, "CNES003", "Ponto 3", "Hospital", "admin@hospital.com", 2L);
        PontoDispensacaoDTO resultDto = createDto(5L, "CNES003", "Ponto 3", "Hospital", "admin@hospital.com", 2L);

        when(presenter.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(presenter.toDto(savedEntity)).thenReturn(resultDto);

        pontoDispensacaoCreateUseCase.execute(dto);

        verify(presenter).toEntity(dto);
        verify(repository).save(entity);
    }

    @Test
    void deveRetornarDtoComIdAposSalvar() {
        PontoDispensacaoDTO inputDto = createDto(null, "CNES004", "Novo Ponto", "Clinica", "clinica@email.com", 3L);
        PontoDispensacao entity = createEntity(null, "CNES004", "Novo Ponto", "Clinica", "clinica@email.com", 3L);
        PontoDispensacao savedEntity = createEntity(100L, "CNES004", "Novo Ponto", "Clinica", "clinica@email.com", 3L);
        PontoDispensacaoDTO expectedDto = createDto(100L, "CNES004", "Novo Ponto", "Clinica", "clinica@email.com", 3L);

        when(presenter.toEntity(inputDto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(presenter.toDto(savedEntity)).thenReturn(expectedDto);

        PontoDispensacaoDTO result = pontoDispensacaoCreateUseCase.execute(inputDto);

        assertNotNull(result.id());
        assertEquals(100L, result.id());
    }

    @Test
    void devePreservarTodosCamposAposCriacao() {
        String cnes = "CNES005";
        String nome = "Ponto Completo";
        String tipo = "Hospital Regional";
        String email = "regional@hospital.com";
        Long clientId = 5L;

        PontoDispensacaoDTO inputDto = createDto(null, cnes, nome, tipo, email, clientId);
        PontoDispensacao entity = createEntity(null, cnes, nome, tipo, email, clientId);
        PontoDispensacao savedEntity = createEntity(10L, cnes, nome, tipo, email, clientId);
        PontoDispensacaoDTO expectedDto = createDto(10L, cnes, nome, tipo, email, clientId);

        when(presenter.toEntity(inputDto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(presenter.toDto(savedEntity)).thenReturn(expectedDto);

        PontoDispensacaoDTO result = pontoDispensacaoCreateUseCase.execute(inputDto);

        assertEquals(cnes, result.cnes());
        assertEquals(nome, result.nome());
        assertEquals(tipo, result.tipo());
        assertEquals(email, result.emailResponsavel());
        assertEquals(clientId, result.clientId());
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

