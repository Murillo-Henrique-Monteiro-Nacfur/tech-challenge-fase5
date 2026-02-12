package com.postech.fiap.fase5.api.presenter;

import com.postech.fiap.fase5.api.dto.PontoDispensacaoDTO;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PontoDispensacaoPresenterTest {

    @InjectMocks
    private PontoDispensacaoPresenter pontoDispensacaoPresenter;

    @Test
    void deveConverterDTOParaEntity() {
        PontoDispensacaoDTO dto = new PontoDispensacaoDTO(
                1L,
                "CNES123456",
                "UBS Central",
                "Unidade Basica de Saude",
                "responsavel@saude.gov.br",
                100L
        );

        PontoDispensacao resultado = pontoDispensacaoPresenter.toEntity(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("CNES123456", resultado.getCnes());
        assertEquals("UBS Central", resultado.getNome());
        assertEquals("Unidade Basica de Saude", resultado.getTipo());
        assertEquals("responsavel@saude.gov.br", resultado.getEmailResponsavel());
        assertEquals(100L, resultado.getClientId());
    }

    @Test
    void deveConverterEntityParaDTO() {
        PontoDispensacao entity = new PontoDispensacao(
                2L,
                "CNES789012",
                "Hospital Regional",
                "Hospital",
                "contato@hospital.gov.br",
                200L
        );

        PontoDispensacaoDTO resultado = pontoDispensacaoPresenter.toDto(entity);

        assertNotNull(resultado);
        assertEquals(2L, resultado.id());
        assertEquals("CNES789012", resultado.cnes());
        assertEquals("Hospital Regional", resultado.nome());
        assertEquals("Hospital", resultado.tipo());
        assertEquals("contato@hospital.gov.br", resultado.emailResponsavel());
        assertEquals(200L, resultado.clientId());
    }

    @Test
    void deveConverterDTOParaEntityComValoresNulos() {
        PontoDispensacaoDTO dto = new PontoDispensacaoDTO(
                null,
                "CNES999999",
                "Posto de Saude",
                "Posto",
                null,
                null
        );

        PontoDispensacao resultado = pontoDispensacaoPresenter.toEntity(dto);

        assertNotNull(resultado);
        assertNull(resultado.getId());
        assertEquals("CNES999999", resultado.getCnes());
        assertEquals("Posto de Saude", resultado.getNome());
        assertEquals("Posto", resultado.getTipo());
        assertNull(resultado.getEmailResponsavel());
        assertNull(resultado.getClientId());
    }

    @Test
    void deveConverterEntityParaDTOComValoresNulos() {
        PontoDispensacao entity = new PontoDispensacao(
                null,
                "CNES555555",
                "Clinica Popular",
                "Clinica",
                null,
                null
        );

        PontoDispensacaoDTO resultado = pontoDispensacaoPresenter.toDto(entity);

        assertNotNull(resultado);
        assertNull(resultado.id());
        assertEquals("CNES555555", resultado.cnes());
        assertEquals("Clinica Popular", resultado.nome());
        assertEquals("Clinica", resultado.tipo());
        assertNull(resultado.emailResponsavel());
        assertNull(resultado.clientId());
    }

    @Test
    void deveConverterDTOParaEntityComTodosCamposPreenchidos() {
        PontoDispensacaoDTO dto = new PontoDispensacaoDTO(
                5L,
                "CNES111222",
                "Centro de Especialidades",
                "Centro Especializado",
                "atendimento@centro.gov.br",
                500L
        );

        PontoDispensacao resultado = pontoDispensacaoPresenter.toEntity(dto);

        assertNotNull(resultado);
        assertEquals(5L, resultado.getId());
        assertEquals("CNES111222", resultado.getCnes());
        assertEquals("Centro de Especialidades", resultado.getNome());
        assertEquals("Centro Especializado", resultado.getTipo());
        assertEquals("atendimento@centro.gov.br", resultado.getEmailResponsavel());
        assertEquals(500L, resultado.getClientId());
    }

    @Test
    void deveConverterEntityParaDTOComTodosCamposPreenchidos() {
        PontoDispensacao entity = new PontoDispensacao(
                10L,
                "CNES333444",
                "Farmacia Popular",
                "Farmacia",
                "farmacia@saude.gov.br",
                1000L
        );

        PontoDispensacaoDTO resultado = pontoDispensacaoPresenter.toDto(entity);

        assertNotNull(resultado);
        assertEquals(10L, resultado.id());
        assertEquals("CNES333444", resultado.cnes());
        assertEquals("Farmacia Popular", resultado.nome());
        assertEquals("Farmacia", resultado.tipo());
        assertEquals("farmacia@saude.gov.br", resultado.emailResponsavel());
        assertEquals(1000L, resultado.clientId());
    }

    @Test
    void deveConverterDTOComIdZeroParaEntity() {
        PontoDispensacaoDTO dto = new PontoDispensacaoDTO(
                0L,
                "CNES000000",
                "Unidade Temporaria",
                "Unidade Movel",
                "temporaria@saude.gov.br",
                0L
        );

        PontoDispensacao resultado = pontoDispensacaoPresenter.toEntity(dto);

        assertNotNull(resultado);
        assertEquals(0L, resultado.getId());
        assertEquals("CNES000000", resultado.getCnes());
        assertEquals("Unidade Temporaria", resultado.getNome());
        assertEquals("Unidade Movel", resultado.getTipo());
        assertEquals("temporaria@saude.gov.br", resultado.getEmailResponsavel());
        assertEquals(0L, resultado.getClientId());
    }

    @Test
    void deveConverterEntityComIdZeroParaDTO() {
        PontoDispensacao entity = new PontoDispensacao(
                0L,
                "CNES888888",
                "Consultorio Municipal",
                "Consultorio",
                "consultorio@prefeitura.gov.br",
                0L
        );

        PontoDispensacaoDTO resultado = pontoDispensacaoPresenter.toDto(entity);

        assertNotNull(resultado);
        assertEquals(0L, resultado.id());
        assertEquals("CNES888888", resultado.cnes());
        assertEquals("Consultorio Municipal", resultado.nome());
        assertEquals("Consultorio", resultado.tipo());
        assertEquals("consultorio@prefeitura.gov.br", resultado.emailResponsavel());
        assertEquals(0L, resultado.clientId());
    }

    @Test
    void deveConverterDTOComCNESLongoParaEntity() {
        PontoDispensacaoDTO dto = new PontoDispensacaoDTO(
                99L,
                "CNES9876543210",
                "Unidade de Pronto Atendimento",
                "UPA",
                "upa24h@saude.gov.br",
                999L
        );

        PontoDispensacao resultado = pontoDispensacaoPresenter.toEntity(dto);

        assertNotNull(resultado);
        assertEquals(99L, resultado.getId());
        assertEquals("CNES9876543210", resultado.getCnes());
        assertEquals("Unidade de Pronto Atendimento", resultado.getNome());
        assertEquals("UPA", resultado.getTipo());
        assertEquals("upa24h@saude.gov.br", resultado.getEmailResponsavel());
        assertEquals(999L, resultado.getClientId());
    }

    @Test
    void deveConverterEntityComCNESLongoParaDTO() {
        PontoDispensacao entity = new PontoDispensacao(
                88L,
                "CNES1234567890",
                "Centro de Atendimento Integrado",
                "CAI",
                "cai@saude.gov.br",
                888L
        );

        PontoDispensacaoDTO resultado = pontoDispensacaoPresenter.toDto(entity);

        assertNotNull(resultado);
        assertEquals(88L, resultado.id());
        assertEquals("CNES1234567890", resultado.cnes());
        assertEquals("Centro de Atendimento Integrado", resultado.nome());
        assertEquals("CAI", resultado.tipo());
        assertEquals("cai@saude.gov.br", resultado.emailResponsavel());
        assertEquals(888L, resultado.clientId());
    }
}

