package com.postech.fiap.fase5.api.usecases;

import com.postech.fiap.fase5.api.dto.CargaEstoqueDTO;
import com.postech.fiap.fase5.api.dto.ItemCargaDTO;
import com.postech.fiap.fase5.api.dto.insumos.InsumoDetalheDTO;
import com.postech.fiap.fase5.api.entities.Insumo;
import com.postech.fiap.fase5.api.entities.Lote;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import com.postech.fiap.fase5.api.repositories.PontoDispensacaoRepository;
import com.postech.fiap.fase5.api.usecases.insumo.InsumoCreateUpdateUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarCargaEstoqueUseCaseTest {

    @Mock
    private PontoDispensacaoRepository pontoDispensacaoRepository;

    @Mock
    private InsumoCreateUpdateUseCase insumoCreateUpdateUseCase;

    @Mock
    private LoteCreateUpdateUseCase loteCreateUpdateUseCase;

    @Mock
    private EstoqueMovimentacaoUseCase estoqueMovimentacaoUseCase;

    @InjectMocks
    private ProcessarCargaEstoqueUseCase processarCargaEstoqueUseCase;

    @Test
    void deveProcessarCargaEstoqueComSucesso() {
        Long clientId = 1L;
        String cnes = "CNES001";
        PontoDispensacao ponto = criarPontoDispensacao(1L, cnes, "Ponto 1", clientId);

        List<InsumoDetalheDTO> insumosDetalhes = criarInsumosDetalhes();
        List<ItemCargaDTO> itens = criarItensCarga();

        CargaEstoqueDTO cargaDTO = new CargaEstoqueDTO(
                cnes,
                LocalDateTime.now(),
                itens,
                insumosDetalhes
        );

        Lote lote1 = criarLote(1L, "LOTE001", 100);
        Lote lote2 = criarLote(2L, "LOTE002", 200);

        when(pontoDispensacaoRepository.findByClientIdAndCnes(clientId, cnes))
                .thenReturn(Optional.of(ponto));
        when(loteCreateUpdateUseCase.execute(itens.get(0)))
                .thenReturn(lote1);
        when(loteCreateUpdateUseCase.execute(itens.get(1)))
                .thenReturn(lote2);

        processarCargaEstoqueUseCase.execute(cargaDTO, clientId);

        verify(pontoDispensacaoRepository).findByClientIdAndCnes(clientId, cnes);
        verify(insumoCreateUpdateUseCase).execute(insumosDetalhes);
        verify(loteCreateUpdateUseCase, times(2)).execute(any(ItemCargaDTO.class));
        verify(estoqueMovimentacaoUseCase).execute(ponto, lote1, itens.get(0).quantidadeEnviada());
        verify(estoqueMovimentacaoUseCase).execute(ponto, lote2, itens.get(1).quantidadeEnviada());
    }

    @Test
    void deveLancarExcecaoQuandoPontoDispensacaoNaoEncontrado() {
        Long clientId = 1L;
        String cnes = "CNES999";

        CargaEstoqueDTO cargaDTO = new CargaEstoqueDTO(
                cnes,
                LocalDateTime.now(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        when(pontoDispensacaoRepository.findByClientIdAndCnes(clientId, cnes))
                .thenReturn(Optional.empty());

        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> processarCargaEstoqueUseCase.execute(cargaDTO, clientId)
        );

        assertEquals("Cliente não possui Ponto de Dispensação vinculado.", exception.getMessage());
        verify(pontoDispensacaoRepository).findByClientIdAndCnes(clientId, cnes);
        verify(insumoCreateUpdateUseCase, never()).execute(any());
        verify(loteCreateUpdateUseCase, never()).execute(any(ItemCargaDTO.class));
        verify(estoqueMovimentacaoUseCase, never()).execute(any(), any(), any());
    }

    @Test
    void deveProcessarCargaComListaDeItensVazia() {
        Long clientId = 1L;
        String cnes = "CNES001";
        PontoDispensacao ponto = criarPontoDispensacao(1L, cnes, "Ponto 1", clientId);

        List<InsumoDetalheDTO> insumosDetalhes = criarInsumosDetalhes();

        CargaEstoqueDTO cargaDTO = new CargaEstoqueDTO(
                cnes,
                LocalDateTime.now(),
                Collections.emptyList(),
                insumosDetalhes
        );

        when(pontoDispensacaoRepository.findByClientIdAndCnes(clientId, cnes))
                .thenReturn(Optional.of(ponto));

        processarCargaEstoqueUseCase.execute(cargaDTO, clientId);

        verify(pontoDispensacaoRepository).findByClientIdAndCnes(clientId, cnes);
        verify(insumoCreateUpdateUseCase).execute(insumosDetalhes);
        verify(loteCreateUpdateUseCase, never()).execute(any(ItemCargaDTO.class));
        verify(estoqueMovimentacaoUseCase, never()).execute(any(), any(), any());
    }

    @Test
    void deveProcessarCargaComListaDeItensNula() {
        Long clientId = 1L;
        String cnes = "CNES001";
        PontoDispensacao ponto = criarPontoDispensacao(1L, cnes, "Ponto 1", clientId);

        List<InsumoDetalheDTO> insumosDetalhes = criarInsumosDetalhes();

        CargaEstoqueDTO cargaDTO = new CargaEstoqueDTO(
                cnes,
                LocalDateTime.now(),
                null,
                insumosDetalhes
        );

        when(pontoDispensacaoRepository.findByClientIdAndCnes(clientId, cnes))
                .thenReturn(Optional.of(ponto));

        processarCargaEstoqueUseCase.execute(cargaDTO, clientId);

        verify(pontoDispensacaoRepository).findByClientIdAndCnes(clientId, cnes);
        verify(insumoCreateUpdateUseCase).execute(insumosDetalhes);
        verify(loteCreateUpdateUseCase, never()).execute(any(ItemCargaDTO.class));
        verify(estoqueMovimentacaoUseCase, never()).execute(any(), any(), any());
    }

    @Test
    void deveProcessarMultiplosItensDeCarregamento() {
        Long clientId = 1L;
        String cnes = "CNES001";
        PontoDispensacao ponto = criarPontoDispensacao(1L, cnes, "Ponto 1", clientId);

        List<InsumoDetalheDTO> insumosDetalhes = criarInsumosDetalhes();
        List<ItemCargaDTO> itens = criarMultiplosItensCarga(5);

        CargaEstoqueDTO cargaDTO = new CargaEstoqueDTO(
                cnes,
                LocalDateTime.now(),
                itens,
                insumosDetalhes
        );

        when(pontoDispensacaoRepository.findByClientIdAndCnes(clientId, cnes))
                .thenReturn(Optional.of(ponto));

        for (int i = 0; i < itens.size(); i++) {
            when(loteCreateUpdateUseCase.execute(itens.get(i)))
                    .thenReturn(criarLote((long) (i + 1), "LOTE" + String.format("%03d", i + 1), 100));
        }

        processarCargaEstoqueUseCase.execute(cargaDTO, clientId);

        verify(pontoDispensacaoRepository).findByClientIdAndCnes(clientId, cnes);
        verify(insumoCreateUpdateUseCase).execute(insumosDetalhes);
        verify(loteCreateUpdateUseCase, times(5)).execute(any(ItemCargaDTO.class));
        verify(estoqueMovimentacaoUseCase, times(5)).execute(eq(ponto), any(Lote.class), any(Integer.class));
    }

    @Test
    void deveProcessarCargaComDiferentesQuantidadesEnviadas() {
        Long clientId = 1L;
        String cnes = "CNES001";
        PontoDispensacao ponto = criarPontoDispensacao(1L, cnes, "Ponto 1", clientId);

        ItemCargaDTO item1 = criarItemCarga("CATMAT001", "LOTE001", 50);
        ItemCargaDTO item2 = criarItemCarga("CATMAT002", "LOTE002", 150);
        List<ItemCargaDTO> itens = List.of(item1, item2);

        CargaEstoqueDTO cargaDTO = new CargaEstoqueDTO(
                cnes,
                LocalDateTime.now(),
                itens,
                Collections.emptyList()
        );

        Lote lote1 = criarLote(1L, "LOTE001", 100);
        Lote lote2 = criarLote(2L, "LOTE002", 300);

        when(pontoDispensacaoRepository.findByClientIdAndCnes(clientId, cnes))
                .thenReturn(Optional.of(ponto));
        when(loteCreateUpdateUseCase.execute(item1))
                .thenReturn(lote1);
        when(loteCreateUpdateUseCase.execute(item2))
                .thenReturn(lote2);

        processarCargaEstoqueUseCase.execute(cargaDTO, clientId);

        verify(estoqueMovimentacaoUseCase).execute(ponto, lote1, 50);
        verify(estoqueMovimentacaoUseCase).execute(ponto, lote2, 150);
    }

    @Test
    void deveProcessarInsumosAntesDeProcessarItens() {
        Long clientId = 1L;
        String cnes = "CNES001";
        PontoDispensacao ponto = criarPontoDispensacao(1L, cnes, "Ponto 1", clientId);

        List<InsumoDetalheDTO> insumosDetalhes = criarInsumosDetalhes();
        List<ItemCargaDTO> itens = criarItensCarga();

        CargaEstoqueDTO cargaDTO = new CargaEstoqueDTO(
                cnes,
                LocalDateTime.now(),
                itens,
                insumosDetalhes
        );

        when(pontoDispensacaoRepository.findByClientIdAndCnes(clientId, cnes))
                .thenReturn(Optional.of(ponto));
        when(loteCreateUpdateUseCase.execute(any(ItemCargaDTO.class)))
                .thenReturn(criarLote(1L, "LOTE001", 100));

        processarCargaEstoqueUseCase.execute(cargaDTO, clientId);

        var inOrder = inOrder(insumoCreateUpdateUseCase, loteCreateUpdateUseCase);
        inOrder.verify(insumoCreateUpdateUseCase).execute(insumosDetalhes);
        inOrder.verify(loteCreateUpdateUseCase, atLeastOnce()).execute(any(ItemCargaDTO.class));
    }

    @Test
    void deveValidarClientIdECnesCorretamenteAoBuscarPontoDispensacao() {
        Long clientId = 2L;
        String cnes = "CNES002";
        PontoDispensacao ponto = criarPontoDispensacao(1L, cnes, "Ponto 2", clientId);

        CargaEstoqueDTO cargaDTO = new CargaEstoqueDTO(
                cnes,
                LocalDateTime.now(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        when(pontoDispensacaoRepository.findByClientIdAndCnes(clientId, cnes))
                .thenReturn(Optional.of(ponto));

        processarCargaEstoqueUseCase.execute(cargaDTO, clientId);

        verify(pontoDispensacaoRepository).findByClientIdAndCnes(clientId, cnes);
    }

    private PontoDispensacao criarPontoDispensacao(Long id, String cnes, String nome, Long clientId) {
        PontoDispensacao ponto = new PontoDispensacao();
        ponto.setId(id);
        ponto.setCnes(cnes);
        ponto.setNome(nome);
        ponto.setTipo("Unidade Básica");
        ponto.setClientId(clientId);
        return ponto;
    }

    private Lote criarLote(Long id, String numeroLote, Integer quantidade) {
        Lote lote = new Lote();
        lote.setId(id);
        lote.setNumeroLote(numeroLote);
        lote.setQuantidade(quantidade);
        lote.setInsumo(criarInsumo(id, "CATMAT" + id, "Medicamento " + id));
        lote.setDataValidade(LocalDate.now().plusMonths(6));
        lote.setDataFabricacao(LocalDate.now().minusMonths(1));
        return lote;
    }

    private Insumo criarInsumo(Long id, String codigoCatmat, String nomeGenerico) {
        Insumo insumo = new Insumo();
        insumo.setId(id);
        insumo.setCodigoCatmat(codigoCatmat);
        insumo.setNomeGenerico(nomeGenerico);
        insumo.setFormaFarmaceutica("Comprimido");
        return insumo;
    }

    private List<InsumoDetalheDTO> criarInsumosDetalhes() {
        List<InsumoDetalheDTO> insumos = new ArrayList<>();
        insumos.add(new InsumoDetalheDTO(
                "CATMAT001",
                "Paracetamol",
                "Comprimido",
                "Genérico",
                "500mg"
        ));
        insumos.add(new InsumoDetalheDTO(
                "CATMAT002",
                "Ibuprofeno",
                "Comprimido",
                "Genérico",
                "600mg"
        ));
        return insumos;
    }

    private List<ItemCargaDTO> criarItensCarga() {
        List<ItemCargaDTO> itens = new ArrayList<>();
        itens.add(criarItemCarga("CATMAT001", "LOTE001", 50));
        itens.add(criarItemCarga("CATMAT002", "LOTE002", 100));
        return itens;
    }

    private ItemCargaDTO criarItemCarga(String codigoCatmat, String numeroLote, Integer quantidadeEnviada) {
        return new ItemCargaDTO(
                codigoCatmat,
                "EXT_" + numeroLote,
                numeroLote,
                LocalDate.now().plusMonths(6),
                LocalDate.now().minusMonths(1),
                quantidadeEnviada,
                quantidadeEnviada * 2
        );
    }

    private List<ItemCargaDTO> criarMultiplosItensCarga(int quantidade) {
        List<ItemCargaDTO> itens = new ArrayList<>();
        for (int i = 1; i <= quantidade; i++) {
            itens.add(criarItemCarga(
                    "CATMAT" + String.format("%03d", i),
                    "LOTE" + String.format("%03d", i),
                    50 * i
            ));
        }
        return itens;
    }
}

