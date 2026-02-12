package com.postech.fiap.fase5.api.usecases.insumo;

import com.postech.fiap.fase5.api.dto.insumos.InsumoDetalheDTO;
import com.postech.fiap.fase5.api.entities.Insumo;
import com.postech.fiap.fase5.api.presenter.InsumoPresenter;
import com.postech.fiap.fase5.api.repositories.InsumoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InsumoCreateUpdateUseCaseTest {

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private InsumoPresenter insumoPresenter;

    @InjectMocks
    private InsumoCreateUpdateUseCase insumoCreateUpdateUseCase;

    @Test
    void deveProcessarListaValidaECriarNovosInsumos() {
        InsumoDetalheDTO insumo1 = new InsumoDetalheDTO(
                "CATMAT001",
                "Paracetamol",
                "Comprimido",
                "Marca A",
                "Analgésico"
        );
        InsumoDetalheDTO insumo2 = new InsumoDetalheDTO(
                "CATMAT002",
                "Ibuprofeno",
                "Comprimido",
                "Marca B",
                "Anti-inflamatório"
        );

        List<InsumoDetalheDTO> insumosDetalhes = Arrays.asList(insumo1, insumo2);

        Insumo insumoEntity1 = criarInsumoEntity(null, "CATMAT001", "Paracetamol");
        Insumo insumoEntity2 = criarInsumoEntity(null, "CATMAT002", "Ibuprofeno");

        when(insumoRepository.findByCodigoCatmat("CATMAT001")).thenReturn(java.util.Optional.empty());
        when(insumoRepository.findByCodigoCatmat("CATMAT002")).thenReturn(java.util.Optional.empty());
        when(insumoRepository.existsByCodigoCatmat("CATMAT001")).thenReturn(false);
        when(insumoRepository.existsByCodigoCatmat("CATMAT002")).thenReturn(false);
        when(insumoPresenter.toEntity(insumo1)).thenReturn(insumoEntity1);
        when(insumoPresenter.toEntity(insumo2)).thenReturn(insumoEntity2);
        when(insumoRepository.saveAndFlush(any(Insumo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        insumoCreateUpdateUseCase.execute(insumosDetalhes);

        verify(insumoRepository).existsByCodigoCatmat("CATMAT001");
        verify(insumoRepository).existsByCodigoCatmat("CATMAT002");
        verify(insumoPresenter).toEntity(insumo1);
        verify(insumoPresenter).toEntity(insumo2);
        verify(insumoRepository, times(4)).saveAndFlush(any(Insumo.class));
    }

    @Test
    void naoDeveSalvarInsumoQuandoJaExiste() {
        InsumoDetalheDTO insumo1 = new InsumoDetalheDTO(
                "CATMAT001",
                "Paracetamol",
                "Comprimido",
                "Marca A",
                "Analgésico"
        );
        InsumoDetalheDTO insumo2 = new InsumoDetalheDTO(
                "CATMAT002",
                "Ibuprofeno",
                "Comprimido",
                "Marca B",
                "Anti-inflamatório"
        );

        List<InsumoDetalheDTO> insumosDetalhes = Arrays.asList(insumo1, insumo2);

        Insumo insumoEntity1 = criarInsumoEntity(1L, "CATMAT001", "Paracetamol");
        Insumo insumoEntity2 = criarInsumoEntity(null, "CATMAT002", "Ibuprofeno");

        when(insumoRepository.findByCodigoCatmat("CATMAT001")).thenReturn(java.util.Optional.of(insumoEntity1));
        when(insumoRepository.findByCodigoCatmat("CATMAT002")).thenReturn(java.util.Optional.empty());
        when(insumoRepository.existsByCodigoCatmat("CATMAT001")).thenReturn(true);
        when(insumoRepository.existsByCodigoCatmat("CATMAT002")).thenReturn(false);
        when(insumoPresenter.toEntity(insumo2)).thenReturn(insumoEntity2);
        when(insumoRepository.saveAndFlush(any(Insumo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        insumoCreateUpdateUseCase.execute(insumosDetalhes);

        verify(insumoRepository).existsByCodigoCatmat("CATMAT001");
        verify(insumoRepository).existsByCodigoCatmat("CATMAT002");
        verify(insumoPresenter, never()).toEntity(insumo1);
        verify(insumoPresenter).toEntity(insumo2);
        verify(insumoRepository, times(2)).saveAndFlush(any(Insumo.class));
    }

    @Test
    void naoDeveProcessarQuandoListaForNula() {
        insumoCreateUpdateUseCase.execute(null);

        verify(insumoRepository, never()).existsByCodigoCatmat(anyString());
        verify(insumoPresenter, never()).toEntity(any(InsumoDetalheDTO.class));
        verify(insumoRepository, never()).saveAndFlush(any(Insumo.class));
    }

    @Test
    void naoDeveProcessarQuandoListaForVazia() {
        List<InsumoDetalheDTO> insumosDetalhes = Collections.emptyList();

        insumoCreateUpdateUseCase.execute(insumosDetalhes);

        verify(insumoRepository, never()).existsByCodigoCatmat(anyString());
        verify(insumoPresenter, never()).toEntity(any(InsumoDetalheDTO.class));
        verify(insumoRepository, never()).saveAndFlush(any(Insumo.class));
    }

    @Test
    void deveProcessarApenasInsumosNovosQuandoAlgunsJaExistem() {
        InsumoDetalheDTO insumo1 = new InsumoDetalheDTO(
                "CATMAT001",
                "Paracetamol",
                "Comprimido",
                "Marca A",
                "Analgésico"
        );
        InsumoDetalheDTO insumo2 = new InsumoDetalheDTO(
                "CATMAT002",
                "Ibuprofeno",
                "Comprimido",
                "Marca B",
                "Anti-inflamatório"
        );
        InsumoDetalheDTO insumo3 = new InsumoDetalheDTO(
                "CATMAT003",
                "Dipirona",
                "Solução Oral",
                "Marca C",
                "Analgésico"
        );

        List<InsumoDetalheDTO> insumosDetalhes = Arrays.asList(insumo1, insumo2, insumo3);

        Insumo insumoEntity1 = criarInsumoEntity(1L, "CATMAT001", "Paracetamol");
        Insumo insumoEntity2 = criarInsumoEntity(null, "CATMAT002", "Ibuprofeno");
        Insumo insumoEntity3 = criarInsumoEntity(3L, "CATMAT003", "Dipirona");

        when(insumoRepository.findByCodigoCatmat("CATMAT001")).thenReturn(java.util.Optional.of(insumoEntity1));
        when(insumoRepository.findByCodigoCatmat("CATMAT002")).thenReturn(java.util.Optional.empty());
        when(insumoRepository.findByCodigoCatmat("CATMAT003")).thenReturn(java.util.Optional.of(insumoEntity3));
        when(insumoRepository.existsByCodigoCatmat("CATMAT001")).thenReturn(true);
        when(insumoRepository.existsByCodigoCatmat("CATMAT002")).thenReturn(false);
        when(insumoRepository.existsByCodigoCatmat("CATMAT003")).thenReturn(true);
        when(insumoPresenter.toEntity(insumo2)).thenReturn(insumoEntity2);
        when(insumoRepository.saveAndFlush(any(Insumo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        insumoCreateUpdateUseCase.execute(insumosDetalhes);

        verify(insumoRepository).existsByCodigoCatmat("CATMAT001");
        verify(insumoRepository).existsByCodigoCatmat("CATMAT002");
        verify(insumoRepository).existsByCodigoCatmat("CATMAT003");
        verify(insumoPresenter, never()).toEntity(insumo1);
        verify(insumoPresenter).toEntity(insumo2);
        verify(insumoPresenter, never()).toEntity(insumo3);
        verify(insumoRepository, times(2)).saveAndFlush(any(Insumo.class));
    }

    @Test
    void deveSalvarInsumoComDadosCorretos() {
        InsumoDetalheDTO insumoDetalhe = new InsumoDetalheDTO(
                "CATMAT001",
                "Paracetamol",
                "Comprimido",
                "Marca A",
                "Analgésico"
        );

        List<InsumoDetalheDTO> insumosDetalhes = Collections.singletonList(insumoDetalhe);

        Insumo insumoEntity = criarInsumoEntity(null, "CATMAT001", "Paracetamol");

        when(insumoRepository.findByCodigoCatmat("CATMAT001")).thenReturn(java.util.Optional.empty());
        when(insumoRepository.existsByCodigoCatmat("CATMAT001")).thenReturn(false);
        when(insumoPresenter.toEntity(insumoDetalhe)).thenReturn(insumoEntity);
        when(insumoRepository.saveAndFlush(any(Insumo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        insumoCreateUpdateUseCase.execute(insumosDetalhes);

        ArgumentCaptor<Insumo> insumoCaptor = ArgumentCaptor.forClass(Insumo.class);
        verify(insumoRepository, times(2)).saveAndFlush(insumoCaptor.capture());

        Insumo insumoSalvo = insumoCaptor.getAllValues().getFirst();
        assertNotNull(insumoSalvo);
        assertEquals("CATMAT001", insumoSalvo.getCodigoCatmat());
        assertEquals("Paracetamol", insumoSalvo.getNomeGenerico());
    }

    @Test
    void naoDeveSalvarNenhumInsumoQuandoTodosJaExistem() {
        InsumoDetalheDTO insumo1 = new InsumoDetalheDTO(
                "CATMAT001",
                "Paracetamol",
                "Comprimido",
                "Marca A",
                "Analgésico"
        );
        InsumoDetalheDTO insumo2 = new InsumoDetalheDTO(
                "CATMAT002",
                "Ibuprofeno",
                "Comprimido",
                "Marca B",
                "Anti-inflamatório"
        );

        List<InsumoDetalheDTO> insumosDetalhes = Arrays.asList(insumo1, insumo2);

        Insumo insumoEntity1 = criarInsumoEntity(1L, "CATMAT001", "Paracetamol");
        Insumo insumoEntity2 = criarInsumoEntity(2L, "CATMAT002", "Ibuprofeno");

        when(insumoRepository.findByCodigoCatmat("CATMAT001")).thenReturn(java.util.Optional.of(insumoEntity1));
        when(insumoRepository.findByCodigoCatmat("CATMAT002")).thenReturn(java.util.Optional.of(insumoEntity2));
        when(insumoRepository.existsByCodigoCatmat("CATMAT001")).thenReturn(true);
        when(insumoRepository.existsByCodigoCatmat("CATMAT002")).thenReturn(true);

        insumoCreateUpdateUseCase.execute(insumosDetalhes);

        verify(insumoRepository).existsByCodigoCatmat("CATMAT001");
        verify(insumoRepository).existsByCodigoCatmat("CATMAT002");
        verify(insumoPresenter, never()).toEntity(any(InsumoDetalheDTO.class));
        verify(insumoRepository, never()).saveAndFlush(any(Insumo.class));
    }

    private Insumo criarInsumoEntity(Long id, String codigoCatmat, String nomeGenerico) {
        Insumo insumo = new Insumo();
        insumo.setId(id);
        insumo.setCodigoCatmat(codigoCatmat);
        insumo.setNomeGenerico(nomeGenerico);
        return insumo;
    }
}

