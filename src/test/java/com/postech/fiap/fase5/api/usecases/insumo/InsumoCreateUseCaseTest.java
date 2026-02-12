package com.postech.fiap.fase5.api.usecases.insumo;

import com.postech.fiap.fase5.api.dto.insumos.InsumoDTO;
import com.postech.fiap.fase5.api.entities.Insumo;
import com.postech.fiap.fase5.api.presenter.InsumoPresenter;
import com.postech.fiap.fase5.api.repositories.InsumoRepository;
import com.postech.fiap.fase5.api.validations.insumo.InsumoCreateValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InsumoCreateUseCaseTest {

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private InsumoPresenter insumoPresenter;

    @Mock
    private InsumoCreateValidation validation1;

    @Mock
    private InsumoCreateValidation validation2;

    private InsumoCreateUseCase insumoCreateUseCase;

    @BeforeEach
    void setUp() {
        List<InsumoCreateValidation> validations = Arrays.asList(validation1, validation2);
        insumoCreateUseCase = new InsumoCreateUseCase(insumoRepository, validations, insumoPresenter);
    }

    @Test
    void deveCriarInsumoComSucessoQuandoDadosValidos() {
        InsumoDTO insumoDTO = criarInsumoDTO(null, "CATMAT123", "Dipirona", "Comprimido", "Marca A", "Analgésico");
        Insumo insumoEntity = criarInsumoEntity(null, "CATMAT123", "Dipirona", "Comprimido", "Marca A", "Analgésico");
        Insumo insumoSalvo = criarInsumoEntity(1L, "CATMAT123", "Dipirona", "Comprimido", "Marca A", "Analgésico");
        InsumoDTO insumoDTORetorno = criarInsumoDTO(1L, "CATMAT123", "Dipirona", "Comprimido", "Marca A", "Analgésico");

        when(insumoPresenter.toEntity(insumoDTO)).thenReturn(insumoEntity);
        when(insumoRepository.save(insumoEntity)).thenReturn(insumoSalvo);
        when(insumoPresenter.toDto(insumoSalvo)).thenReturn(insumoDTORetorno);

        InsumoDTO resultado = insumoCreateUseCase.execute(insumoDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("CATMAT123", resultado.codigoCatmat());
        assertEquals("Dipirona", resultado.nomeGenerico());
        assertEquals("Comprimido", resultado.formaFarmaceutica());
        assertEquals("Marca A", resultado.marca());
        assertEquals("Analgésico", resultado.descricao());

        verify(validation1).validate(insumoDTO);
        verify(validation2).validate(insumoDTO);
        verify(insumoPresenter).toEntity(insumoDTO);
        verify(insumoRepository).save(insumoEntity);
        verify(insumoPresenter).toDto(insumoSalvo);
    }

    @Test
    void deveExecutarTodasValidacoesAntesDecriar() {
        InsumoDTO insumoDTO = criarInsumoDTO(null, "CATMAT456", "Paracetamol", "Comprimido", "Marca B", "Analgésico");
        Insumo insumoEntity = criarInsumoEntity(null, "CATMAT456", "Paracetamol", "Comprimido", "Marca B", "Analgésico");
        Insumo insumoSalvo = criarInsumoEntity(2L, "CATMAT456", "Paracetamol", "Comprimido", "Marca B", "Analgésico");
        InsumoDTO insumoDTORetorno = criarInsumoDTO(2L, "CATMAT456", "Paracetamol", "Comprimido", "Marca B", "Analgésico");

        when(insumoPresenter.toEntity(insumoDTO)).thenReturn(insumoEntity);
        when(insumoRepository.save(insumoEntity)).thenReturn(insumoSalvo);
        when(insumoPresenter.toDto(insumoSalvo)).thenReturn(insumoDTORetorno);

        insumoCreateUseCase.execute(insumoDTO);

        verify(validation1, times(1)).validate(insumoDTO);
        verify(validation2, times(1)).validate(insumoDTO);
    }

    @Test
    void deveLancarExcecaoQuandoValidacaoFalhar() {
        InsumoDTO insumoDTO = criarInsumoDTO(null, "CATMAT789", "Ibuprofeno", "Comprimido", "Marca C", "Anti-inflamatório");

        doThrow(new IllegalArgumentException("Nome genérico é obrigatório"))
                .when(validation1).validate(insumoDTO);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> insumoCreateUseCase.execute(insumoDTO)
        );

        assertEquals("Nome genérico é obrigatório", exception.getMessage());
        verify(validation1).validate(insumoDTO);
        verify(insumoPresenter, never()).toEntity(any(InsumoDTO.class));
        verify(insumoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoSegundaValidacaoFalhar() {
        InsumoDTO insumoDTO = criarInsumoDTO(null, "CATMAT999", "Amoxicilina", "Comprimido", "Marca D", "Antibiótico");

        doNothing().when(validation1).validate(insumoDTO);
        doThrow(new IllegalArgumentException("Código CATMAT já cadastrado"))
                .when(validation2).validate(insumoDTO);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> insumoCreateUseCase.execute(insumoDTO)
        );

        assertEquals("Código CATMAT já cadastrado", exception.getMessage());
        verify(validation1).validate(insumoDTO);
        verify(validation2).validate(insumoDTO);
        verify(insumoPresenter, never()).toEntity(any(InsumoDTO.class));
        verify(insumoRepository, never()).save(any());
    }

    @Test
    void deveCriarInsumoComCamposOpcionaisNulos() {
        InsumoDTO insumoDTO = criarInsumoDTO(null, "CATMAT555", "Medicamento Genérico", null, null, null);
        Insumo insumoEntity = criarInsumoEntity(null, "CATMAT555", "Medicamento Genérico", null, null, null);
        Insumo insumoSalvo = criarInsumoEntity(3L, "CATMAT555", "Medicamento Genérico", null, null, null);
        InsumoDTO insumoDTORetorno = criarInsumoDTO(3L, "CATMAT555", "Medicamento Genérico", null, null, null);

        when(insumoPresenter.toEntity(insumoDTO)).thenReturn(insumoEntity);
        when(insumoRepository.save(insumoEntity)).thenReturn(insumoSalvo);
        when(insumoPresenter.toDto(insumoSalvo)).thenReturn(insumoDTORetorno);

        InsumoDTO resultado = insumoCreateUseCase.execute(insumoDTO);

        assertNotNull(resultado);
        assertEquals(3L, resultado.id());
        assertEquals("CATMAT555", resultado.codigoCatmat());
        assertEquals("Medicamento Genérico", resultado.nomeGenerico());
        assertNull(resultado.formaFarmaceutica());
        assertNull(resultado.marca());
        assertNull(resultado.descricao());

        verify(insumoPresenter).toEntity(insumoDTO);
        verify(insumoRepository).save(insumoEntity);
        verify(insumoPresenter).toDto(insumoSalvo);
    }

    @Test
    void deveUsarPresenterParaConverterDTOEmEntity() {
        InsumoDTO insumoDTO = criarInsumoDTO(null, "CATMAT111", "Teste", "Comprimido", "Marca", "Descrição");
        Insumo insumoEntity = criarInsumoEntity(null, "CATMAT111", "Teste", "Comprimido", "Marca", "Descrição");
        Insumo insumoSalvo = criarInsumoEntity(4L, "CATMAT111", "Teste", "Comprimido", "Marca", "Descrição");
        InsumoDTO insumoDTORetorno = criarInsumoDTO(4L, "CATMAT111", "Teste", "Comprimido", "Marca", "Descrição");

        when(insumoPresenter.toEntity(any(InsumoDTO.class))).thenReturn(insumoEntity);
        when(insumoRepository.save(insumoEntity)).thenReturn(insumoSalvo);
        when(insumoPresenter.toDto(insumoSalvo)).thenReturn(insumoDTORetorno);

        insumoCreateUseCase.execute(insumoDTO);

        ArgumentCaptor<InsumoDTO> dtoCaptor = ArgumentCaptor.forClass(InsumoDTO.class);
        verify(insumoPresenter).toEntity(dtoCaptor.capture());

        InsumoDTO capturedDTO = dtoCaptor.getValue();
        assertEquals("CATMAT111", capturedDTO.codigoCatmat());
        assertEquals("Teste", capturedDTO.nomeGenerico());
    }

    @Test
    void deveUsarPresenterParaConverterEntityEmDTO() {
        InsumoDTO insumoDTO = criarInsumoDTO(null, "CATMAT222", "Teste2", "Solução", "Marca X", "Desc");
        Insumo insumoEntity = criarInsumoEntity(null, "CATMAT222", "Teste2", "Solução", "Marca X", "Desc");
        Insumo insumoSalvo = criarInsumoEntity(5L, "CATMAT222", "Teste2", "Solução", "Marca X", "Desc");
        InsumoDTO insumoDTORetorno = criarInsumoDTO(5L, "CATMAT222", "Teste2", "Solução", "Marca X", "Desc");

        when(insumoPresenter.toEntity(any(InsumoDTO.class))).thenReturn(insumoEntity);
        when(insumoRepository.save(insumoEntity)).thenReturn(insumoSalvo);
        when(insumoPresenter.toDto(insumoSalvo)).thenReturn(insumoDTORetorno);

        insumoCreateUseCase.execute(insumoDTO);

        ArgumentCaptor<Insumo> entityCaptor = ArgumentCaptor.forClass(Insumo.class);
        verify(insumoPresenter).toDto(entityCaptor.capture());

        Insumo capturedEntity = entityCaptor.getValue();
        assertEquals(5L, capturedEntity.getId());
        assertEquals("CATMAT222", capturedEntity.getCodigoCatmat());
        assertEquals("Teste2", capturedEntity.getNomeGenerico());
    }

    @Test
    void deveSalvarInsumoNoRepositorio() {
        InsumoDTO insumoDTO = criarInsumoDTO(null, "CATMAT333", "Teste3", "Cápsula", "Marca Y", "Descrição Y");
        Insumo insumoEntity = criarInsumoEntity(null, "CATMAT333", "Teste3", "Cápsula", "Marca Y", "Descrição Y");
        Insumo insumoSalvo = criarInsumoEntity(6L, "CATMAT333", "Teste3", "Cápsula", "Marca Y", "Descrição Y");
        InsumoDTO insumoDTORetorno = criarInsumoDTO(6L, "CATMAT333", "Teste3", "Cápsula", "Marca Y", "Descrição Y");

        when(insumoPresenter.toEntity(insumoDTO)).thenReturn(insumoEntity);
        when(insumoRepository.save(insumoEntity)).thenReturn(insumoSalvo);
        when(insumoPresenter.toDto(insumoSalvo)).thenReturn(insumoDTORetorno);

        insumoCreateUseCase.execute(insumoDTO);

        ArgumentCaptor<Insumo> entityCaptor = ArgumentCaptor.forClass(Insumo.class);
        verify(insumoRepository).save(entityCaptor.capture());

        Insumo capturedEntity = entityCaptor.getValue();
        assertEquals("CATMAT333", capturedEntity.getCodigoCatmat());
        assertEquals("Teste3", capturedEntity.getNomeGenerico());
        assertEquals("Cápsula", capturedEntity.getFormaFarmaceutica());
    }

    @Test
    void deveRetornarDTOComIdGeradoAposSalvar() {
        InsumoDTO insumoDTO = criarInsumoDTO(null, "CATMAT444", "Teste4", "Xarope", "Marca Z", "Descrição Z");
        Insumo insumoEntity = criarInsumoEntity(null, "CATMAT444", "Teste4", "Xarope", "Marca Z", "Descrição Z");
        Insumo insumoSalvo = criarInsumoEntity(7L, "CATMAT444", "Teste4", "Xarope", "Marca Z", "Descrição Z");
        InsumoDTO insumoDTORetorno = criarInsumoDTO(7L, "CATMAT444", "Teste4", "Xarope", "Marca Z", "Descrição Z");

        when(insumoPresenter.toEntity(insumoDTO)).thenReturn(insumoEntity);
        when(insumoRepository.save(insumoEntity)).thenReturn(insumoSalvo);
        when(insumoPresenter.toDto(insumoSalvo)).thenReturn(insumoDTORetorno);

        InsumoDTO resultado = insumoCreateUseCase.execute(insumoDTO);

        assertNotNull(resultado);
        assertNotNull(resultado.id());
        assertEquals(7L, resultado.id());
        assertEquals("CATMAT444", resultado.codigoCatmat());
    }

    private InsumoDTO criarInsumoDTO(Long id, String codigoCatmat, String nomeGenerico,
                                      String formaFarmaceutica, String marca, String descricao) {
        return new InsumoDTO(id, codigoCatmat, nomeGenerico, formaFarmaceutica, marca, descricao);
    }

    private Insumo criarInsumoEntity(Long id, String codigoCatmat, String nomeGenerico,
                                      String formaFarmaceutica, String marca, String descricao) {
        Insumo insumo = new Insumo();
        insumo.setId(id);
        insumo.setCodigoCatmat(codigoCatmat);
        insumo.setNomeGenerico(nomeGenerico);
        insumo.setFormaFarmaceutica(formaFarmaceutica);
        insumo.setMarca(marca);
        insumo.setDescricao(descricao);
        return insumo;
    }
}

