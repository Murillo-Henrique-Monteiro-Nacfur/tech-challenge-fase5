package com.postech.fiap.fase5.api.presenter;

import com.postech.fiap.fase5.api.dto.insumos.InsumoDTO;
import com.postech.fiap.fase5.api.dto.insumos.InsumoDetalheDTO;
import com.postech.fiap.fase5.api.entities.Insumo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InsumoPresenterTest {

    @InjectMocks
    private InsumoPresenter insumoPresenter;

    @Test
    void deveConverterInsumoDeTalheDTOParaEntity() {
        InsumoDetalheDTO dto = new InsumoDetalheDTO(
                "12345",
                "Paracetamol",
                "Comprimido",
                "MarcaA",
                "Analgesico"
        );

        Insumo resultado = insumoPresenter.toEntity(dto);

        assertNotNull(resultado);
        assertEquals("12345", resultado.getCodigoCatmat());
        assertEquals("Paracetamol", resultado.getNomeGenerico());
        assertEquals("Comprimido", resultado.getFormaFarmaceutica());
        assertNull(resultado.getId());
        assertNull(resultado.getMarca());
        assertNull(resultado.getDescricao());
    }

    @Test
    void deveConverterInsumoDTOParaEntity() {
        InsumoDTO dto = new InsumoDTO(
                1L,
                "67890",
                "Ibuprofeno",
                "Capsula",
                "MarcaB",
                "Anti-inflamatorio"
        );

        Insumo resultado = insumoPresenter.toEntity(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("67890", resultado.getCodigoCatmat());
        assertEquals("Ibuprofeno", resultado.getNomeGenerico());
        assertEquals("Capsula", resultado.getFormaFarmaceutica());
        assertEquals("MarcaB", resultado.getMarca());
        assertEquals("Anti-inflamatorio", resultado.getDescricao());
    }

    @Test
    void deveConverterEntityParaInsumoDTO() {
        Insumo entity = new Insumo(
                2L,
                "11111",
                "Dipirona",
                "Solucao oral",
                "MarcaC",
                "Analgesico e antipiretico"
        );

        InsumoDTO resultado = insumoPresenter.toDto(entity);

        assertNotNull(resultado);
        assertEquals(2L, resultado.id());
        assertEquals("11111", resultado.codigoCatmat());
        assertEquals("Dipirona", resultado.nomeGenerico());
        assertEquals("Solucao oral", resultado.formaFarmaceutica());
        assertEquals("MarcaC", resultado.marca());
        assertEquals("Analgesico e antipiretico", resultado.descricao());
    }

    @Test
    void deveConverterInsumoDeTalheDTOComValoresNulos() {
        InsumoDetalheDTO dto = new InsumoDetalheDTO(
                "54321",
                "Amoxicilina",
                null,
                null,
                null
        );

        Insumo resultado = insumoPresenter.toEntity(dto);

        assertNotNull(resultado);
        assertEquals("54321", resultado.getCodigoCatmat());
        assertEquals("Amoxicilina", resultado.getNomeGenerico());
        assertNull(resultado.getFormaFarmaceutica());
    }

    @Test
    void deveConverterInsumoDTOComValoresNulos() {
        InsumoDTO dto = new InsumoDTO(
                null,
                null,
                "Azitromicina",
                null,
                null,
                null
        );

        Insumo resultado = insumoPresenter.toEntity(dto);

        assertNotNull(resultado);
        assertNull(resultado.getId());
        assertNull(resultado.getCodigoCatmat());
        assertEquals("Azitromicina", resultado.getNomeGenerico());
        assertNull(resultado.getFormaFarmaceutica());
        assertNull(resultado.getMarca());
        assertNull(resultado.getDescricao());
    }

    @Test
    void deveConverterEntityComValoresNulosParaDTO() {
        Insumo entity = new Insumo(
                null,
                null,
                "Cefalexina",
                null,
                null,
                null
        );

        InsumoDTO resultado = insumoPresenter.toDto(entity);

        assertNotNull(resultado);
        assertNull(resultado.id());
        assertNull(resultado.codigoCatmat());
        assertEquals("Cefalexina", resultado.nomeGenerico());
        assertNull(resultado.formaFarmaceutica());
        assertNull(resultado.marca());
        assertNull(resultado.descricao());
    }

    @Test
    void deveConverterInsumoDeTalheDTOComTodosCamposPreenchidos() {
        InsumoDetalheDTO dto = new InsumoDetalheDTO(
                "99999",
                "Metformina",
                "Comprimido Revestido",
                "MarcaD",
                "Antidiabetico oral"
        );

        Insumo resultado = insumoPresenter.toEntity(dto);

        assertNotNull(resultado);
        assertEquals("99999", resultado.getCodigoCatmat());
        assertEquals("Metformina", resultado.getNomeGenerico());
        assertEquals("Comprimido Revestido", resultado.getFormaFarmaceutica());
    }

    @Test
    void deveConverterInsumoDTOComTodosCamposPreenchidos() {
        InsumoDTO dto = new InsumoDTO(
                5L,
                "88888",
                "Losartana",
                "Comprimido",
                "MarcaE",
                "Anti-hipertensivo"
        );

        Insumo resultado = insumoPresenter.toEntity(dto);

        assertNotNull(resultado);
        assertEquals(5L, resultado.getId());
        assertEquals("88888", resultado.getCodigoCatmat());
        assertEquals("Losartana", resultado.getNomeGenerico());
        assertEquals("Comprimido", resultado.getFormaFarmaceutica());
        assertEquals("MarcaE", resultado.getMarca());
        assertEquals("Anti-hipertensivo", resultado.getDescricao());
    }

    @Test
    void deveConverterEntityComTodosCamposPreenchidosParaDTO() {
        Insumo entity = new Insumo(
                10L,
                "77777",
                "Sinvastatina",
                "Comprimido Revestido",
                "MarcaF",
                "Hipolipemiante"
        );

        InsumoDTO resultado = insumoPresenter.toDto(entity);

        assertNotNull(resultado);
        assertEquals(10L, resultado.id());
        assertEquals("77777", resultado.codigoCatmat());
        assertEquals("Sinvastatina", resultado.nomeGenerico());
        assertEquals("Comprimido Revestido", resultado.formaFarmaceutica());
        assertEquals("MarcaF", resultado.marca());
        assertEquals("Hipolipemiante", resultado.descricao());
    }
}

