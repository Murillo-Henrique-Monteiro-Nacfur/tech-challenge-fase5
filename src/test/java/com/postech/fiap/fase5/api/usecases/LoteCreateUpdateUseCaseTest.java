package com.postech.fiap.fase5.api.usecases;

import com.postech.fiap.fase5.api.dto.ItemCargaDTO;
import com.postech.fiap.fase5.api.entities.Insumo;
import com.postech.fiap.fase5.api.entities.Lote;
import com.postech.fiap.fase5.api.repositories.InsumoRepository;
import com.postech.fiap.fase5.api.repositories.LoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoteCreateUpdateUseCaseTest {

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private InsumoRepository insumoRepository;

    @InjectMocks
    private LoteCreateUpdateUseCase loteCreateUpdateUseCase;

    @Test
    void deveRetornarLoteExistenteQuandoNumeroLoteEInsumoJaExistem() {
        String codigoCatmat = "CATMAT001";
        String numeroLote = "LOTE001";
        LocalDate dataValidade = LocalDate.of(2026, 12, 31);
        LocalDate dataFabricacao = LocalDate.of(2026, 1, 1);
        Integer quantidadeTotalLote = 100;

        Insumo insumo = criarInsumo(1L, codigoCatmat, "Paracetamol");
        Lote loteExistente = criarLote(1L, numeroLote, insumo, dataValidade, dataFabricacao, quantidadeTotalLote);

        ItemCargaDTO itemCargaDTO = new ItemCargaDTO(
                codigoCatmat,
                "EXT001",
                numeroLote,
                dataValidade,
                dataFabricacao,
                50,
                quantidadeTotalLote
        );

        when(insumoRepository.findByCodigoCatmat(codigoCatmat))
                .thenReturn(Optional.of(insumo));
        when(loteRepository.findByNumeroLoteAndInsumoId(numeroLote, insumo.getId()))
                .thenReturn(Optional.of(loteExistente));

        Lote resultado = loteCreateUpdateUseCase.execute(itemCargaDTO);

        assertNotNull(resultado);
        assertEquals(loteExistente.getId(), resultado.getId());
        assertEquals(numeroLote, resultado.getNumeroLote());
        assertEquals(insumo, resultado.getInsumo());
        assertEquals(dataValidade, resultado.getDataValidade());
        assertEquals(dataFabricacao, resultado.getDataFabricacao());
        assertEquals(quantidadeTotalLote, resultado.getQuantidade());

        verify(insumoRepository).findByCodigoCatmat(codigoCatmat);
        verify(loteRepository).findByNumeroLoteAndInsumoId(numeroLote, insumo.getId());
        verify(loteRepository, never()).save(any(Lote.class));
    }

    @Test
    void deveCriarNovoLoteQuandoNumeroLoteNaoExistirParaInsumo() {
        String codigoCatmat = "CATMAT002";
        String numeroLote = "LOTE002";
        LocalDate dataValidade = LocalDate.of(2027, 6, 30);
        LocalDate dataFabricacao = LocalDate.of(2026, 6, 1);
        Integer quantidadeTotalLote = 200;

        Insumo insumo = criarInsumo(2L, codigoCatmat, "Ibuprofeno");

        ItemCargaDTO itemCargaDTO = new ItemCargaDTO(
                codigoCatmat,
                "EXT002",
                numeroLote,
                dataValidade,
                dataFabricacao,
                100,
                quantidadeTotalLote
        );

        when(insumoRepository.findByCodigoCatmat(codigoCatmat))
                .thenReturn(Optional.of(insumo));
        when(loteRepository.findByNumeroLoteAndInsumoId(numeroLote, insumo.getId()))
                .thenReturn(Optional.empty());
        when(loteRepository.save(any(Lote.class)))
                .thenAnswer(invocation -> {
                    Lote loteSalvo = invocation.getArgument(0);
                    loteSalvo.setId(3L);
                    return loteSalvo;
                });

        Lote resultado = loteCreateUpdateUseCase.execute(itemCargaDTO);

        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        assertEquals(numeroLote, resultado.getNumeroLote());
        assertEquals(insumo, resultado.getInsumo());
        assertEquals(dataValidade, resultado.getDataValidade());
        assertEquals(dataFabricacao, resultado.getDataFabricacao());
        assertEquals(quantidadeTotalLote, resultado.getQuantidade());

        verify(insumoRepository).findByCodigoCatmat(codigoCatmat);
        verify(loteRepository).findByNumeroLoteAndInsumoId(numeroLote, insumo.getId());

        ArgumentCaptor<Lote> loteCaptor = ArgumentCaptor.forClass(Lote.class);
        verify(loteRepository).save(loteCaptor.capture());

        Lote loteSalvo = loteCaptor.getValue();
        assertEquals(numeroLote, loteSalvo.getNumeroLote());
        assertEquals(insumo, loteSalvo.getInsumo());
        assertEquals(dataValidade, loteSalvo.getDataValidade());
        assertEquals(dataFabricacao, loteSalvo.getDataFabricacao());
        assertEquals(quantidadeTotalLote, loteSalvo.getQuantidade());
    }

    @Test
    void deveLancarExcecaoQuandoInsumoNaoForEncontrado() {
        String codigoCatmatInexistente = "CATMAT999";
        ItemCargaDTO itemCargaDTO = new ItemCargaDTO(
                codigoCatmatInexistente,
                "EXT003",
                "LOTE003",
                LocalDate.of(2027, 1, 1),
                LocalDate.of(2026, 1, 1),
                50,
                100
        );

        when(insumoRepository.findByCodigoCatmat(codigoCatmatInexistente))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loteCreateUpdateUseCase.execute(itemCargaDTO)
        );

        assertEquals("Insumo não encontrado: " + codigoCatmatInexistente, exception.getMessage());

        verify(insumoRepository).findByCodigoCatmat(codigoCatmatInexistente);
        verify(loteRepository, never()).findByNumeroLoteAndInsumoId(anyString(), anyLong());
        verify(loteRepository, never()).save(any(Lote.class));
    }

    @Test
    void deveCriarLoteComTodasAsPropriedadesDoItemCargaDTO() {
        String codigoCatmat = "CATMAT003";
        String numeroLote = "LOTE003";
        LocalDate dataValidade = LocalDate.of(2028, 3, 15);
        LocalDate dataFabricacao = LocalDate.of(2026, 3, 1);
        Integer quantidadeTotalLote = 500;

        Insumo insumo = criarInsumo(4L, codigoCatmat, "Dipirona");

        ItemCargaDTO itemCargaDTO = new ItemCargaDTO(
                codigoCatmat,
                "EXT004",
                numeroLote,
                dataValidade,
                dataFabricacao,
                250,
                quantidadeTotalLote
        );

        when(insumoRepository.findByCodigoCatmat(codigoCatmat))
                .thenReturn(Optional.of(insumo));
        when(loteRepository.findByNumeroLoteAndInsumoId(numeroLote, insumo.getId()))
                .thenReturn(Optional.empty());
        when(loteRepository.save(any(Lote.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        loteCreateUpdateUseCase.execute(itemCargaDTO);

        ArgumentCaptor<Lote> loteCaptor = ArgumentCaptor.forClass(Lote.class);
        verify(loteRepository).save(loteCaptor.capture());

        Lote loteSalvo = loteCaptor.getValue();
        assertNotNull(loteSalvo);
        assertEquals(numeroLote, loteSalvo.getNumeroLote());
        assertEquals(insumo, loteSalvo.getInsumo());
        assertEquals(dataValidade, loteSalvo.getDataValidade());
        assertEquals(dataFabricacao, loteSalvo.getDataFabricacao());
        assertEquals(quantidadeTotalLote, loteSalvo.getQuantidade());
    }

    @Test
    void deveConsultarRepositorioCorretamenteParaBuscarLoteExistente() {
        String codigoCatmat = "CATMAT004";
        String numeroLote = "LOTE004";
        Long insumoId = 5L;

        Insumo insumo = criarInsumo(insumoId, codigoCatmat, "Amoxicilina");
        Lote loteExistente = criarLote(5L, numeroLote, insumo, LocalDate.now(), LocalDate.now(), 300);

        ItemCargaDTO itemCargaDTO = new ItemCargaDTO(
                codigoCatmat,
                "EXT005",
                numeroLote,
                LocalDate.of(2027, 12, 31),
                LocalDate.of(2026, 12, 1),
                150,
                300
        );

        when(insumoRepository.findByCodigoCatmat(codigoCatmat))
                .thenReturn(Optional.of(insumo));
        when(loteRepository.findByNumeroLoteAndInsumoId(numeroLote, insumoId))
                .thenReturn(Optional.of(loteExistente));

        loteCreateUpdateUseCase.execute(itemCargaDTO);

        verify(loteRepository).findByNumeroLoteAndInsumoId(numeroLote, insumoId);
    }

    private Insumo criarInsumo(Long id, String codigoCatmat, String nomeGenerico) {
        Insumo insumo = new Insumo();
        insumo.setId(id);
        insumo.setCodigoCatmat(codigoCatmat);
        insumo.setNomeGenerico(nomeGenerico);
        insumo.setFormaFarmaceutica("Comprimido");
        insumo.setMarca("Genérico");
        return insumo;
    }

    private Lote criarLote(Long id, String numeroLote, Insumo insumo, LocalDate dataValidade,
                           LocalDate dataFabricacao, Integer quantidade) {
        Lote lote = new Lote();
        lote.setId(id);
        lote.setNumeroLote(numeroLote);
        lote.setInsumo(insumo);
        lote.setDataValidade(dataValidade);
        lote.setDataFabricacao(dataFabricacao);
        lote.setQuantidade(quantidade);
        return lote;
    }
}

