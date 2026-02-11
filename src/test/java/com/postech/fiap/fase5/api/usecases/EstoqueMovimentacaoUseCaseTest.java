package com.postech.fiap.fase5.api.usecases;

import com.postech.fiap.fase5.api.entities.Insumo;
import com.postech.fiap.fase5.api.entities.Lote;
import com.postech.fiap.fase5.api.entities.LoteInventario;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import com.postech.fiap.fase5.api.repositories.LoteInventarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstoqueMovimentacaoUseCaseTest {

    @Mock
    private LoteInventarioRepository loteInventarioRepository;

    @InjectMocks
    private EstoqueMovimentacaoUseCase estoqueMovimentacaoUseCase;

    @Test
    void deveAtualizarInventarioExistente() {
        PontoDispensacao ponto = criarPontoDispensacao(1L, "CNES001", "Ponto 1");
        Lote lote = criarLote(1L, "LOTE001", 100);
        LoteInventario inventarioExistente = criarLoteInventario(1L, ponto, lote, 50);

        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(1L, 1L))
                .thenReturn(Optional.of(inventarioExistente));
        when(loteInventarioRepository.save(any(LoteInventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        estoqueMovimentacaoUseCase.execute(ponto, lote, 30);

        ArgumentCaptor<LoteInventario> captor = ArgumentCaptor.forClass(LoteInventario.class);
        verify(loteInventarioRepository).save(captor.capture());

        LoteInventario inventarioSalvo = captor.getValue();
        assertEquals(80, inventarioSalvo.getQuantidade());
        assertEquals(ponto, inventarioSalvo.getPontoDispensacao());
        assertEquals(lote, inventarioSalvo.getLote());
        assertNotNull(inventarioSalvo.getDataHoraChegada());
    }

    @Test
    void deveCriarNovoInventarioQuandoNaoExistir() {
        PontoDispensacao ponto = criarPontoDispensacao(1L, "CNES001", "Ponto 1");
        Lote lote = criarLote(1L, "LOTE001", 100);

        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(1L, 1L))
                .thenReturn(Optional.empty());
        when(loteInventarioRepository.save(any(LoteInventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        estoqueMovimentacaoUseCase.execute(ponto, lote, 25);

        ArgumentCaptor<LoteInventario> captor = ArgumentCaptor.forClass(LoteInventario.class);
        verify(loteInventarioRepository).save(captor.capture());

        LoteInventario inventarioSalvo = captor.getValue();
        assertEquals(25, inventarioSalvo.getQuantidade());
        assertEquals(ponto, inventarioSalvo.getPontoDispensacao());
        assertEquals(lote, inventarioSalvo.getLote());
        assertNotNull(inventarioSalvo.getDataHoraChegada());
    }

    @Test
    void deveIncrementarQuantidadeCorretamente() {
        PontoDispensacao ponto = criarPontoDispensacao(2L, "CNES002", "Ponto 2");
        Lote lote = criarLote(2L, "LOTE002", 200);
        LoteInventario inventarioExistente = criarLoteInventario(2L, ponto, lote, 150);

        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(2L, 2L))
                .thenReturn(Optional.of(inventarioExistente));
        when(loteInventarioRepository.save(any(LoteInventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        estoqueMovimentacaoUseCase.execute(ponto, lote, 75);

        ArgumentCaptor<LoteInventario> captor = ArgumentCaptor.forClass(LoteInventario.class);
        verify(loteInventarioRepository).save(captor.capture());

        LoteInventario inventarioSalvo = captor.getValue();
        assertEquals(225, inventarioSalvo.getQuantidade());
    }

    @Test
    void deveAtualizarDataHoraChegadaAoMovimentar() {
        PontoDispensacao ponto = criarPontoDispensacao(1L, "CNES001", "Ponto 1");
        Lote lote = criarLote(1L, "LOTE001", 100);
        LocalDateTime dataAnterior = LocalDateTime.of(2026, 1, 1, 10, 0);
        LoteInventario inventarioExistente = criarLoteInventario(1L, ponto, lote, 50);
        inventarioExistente.setDataHoraChegada(dataAnterior);

        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(1L, 1L))
                .thenReturn(Optional.of(inventarioExistente));
        when(loteInventarioRepository.save(any(LoteInventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        estoqueMovimentacaoUseCase.execute(ponto, lote, 20);

        ArgumentCaptor<LoteInventario> captor = ArgumentCaptor.forClass(LoteInventario.class);
        verify(loteInventarioRepository).save(captor.capture());

        LoteInventario inventarioSalvo = captor.getValue();
        assertNotNull(inventarioSalvo.getDataHoraChegada());
        assertTrue(inventarioSalvo.getDataHoraChegada().isAfter(dataAnterior));
    }

    @Test
    void deveBuscarInventarioExistenteAntesDeSalvar() {
        PontoDispensacao ponto = criarPontoDispensacao(1L, "CNES001", "Ponto 1");
        Lote lote = criarLote(1L, "LOTE001", 100);
        LoteInventario inventarioExistente = criarLoteInventario(1L, ponto, lote, 50);

        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(1L, 1L))
                .thenReturn(Optional.of(inventarioExistente));
        when(loteInventarioRepository.save(any(LoteInventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        estoqueMovimentacaoUseCase.execute(ponto, lote, 10);

        verify(loteInventarioRepository).findByPontoDispensacaoIdAndLoteId(1L, 1L);
        verify(loteInventarioRepository).save(any(LoteInventario.class));
    }

    @Test
    void deveSalvarInventarioAposMovimentacao() {
        PontoDispensacao ponto = criarPontoDispensacao(1L, "CNES001", "Ponto 1");
        Lote lote = criarLote(1L, "LOTE001", 100);

        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(1L, 1L))
                .thenReturn(Optional.empty());

        estoqueMovimentacaoUseCase.execute(ponto, lote, 15);

        verify(loteInventarioRepository, times(1)).save(any(LoteInventario.class));
    }

    @Test
    void deveProcessarQuantidadeZeroCorretamente() {
        PontoDispensacao ponto = criarPontoDispensacao(1L, "CNES001", "Ponto 1");
        Lote lote = criarLote(1L, "LOTE001", 100);
        LoteInventario inventarioExistente = criarLoteInventario(1L, ponto, lote, 50);

        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(1L, 1L))
                .thenReturn(Optional.of(inventarioExistente));
        when(loteInventarioRepository.save(any(LoteInventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        estoqueMovimentacaoUseCase.execute(ponto, lote, 0);

        ArgumentCaptor<LoteInventario> captor = ArgumentCaptor.forClass(LoteInventario.class);
        verify(loteInventarioRepository).save(captor.capture());

        LoteInventario inventarioSalvo = captor.getValue();
        assertEquals(50, inventarioSalvo.getQuantidade());
    }

    @Test
    void deveProcessarQuantidadeNegativaCorretamente() {
        PontoDispensacao ponto = criarPontoDispensacao(1L, "CNES001", "Ponto 1");
        Lote lote = criarLote(1L, "LOTE001", 100);
        LoteInventario inventarioExistente = criarLoteInventario(1L, ponto, lote, 50);

        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(1L, 1L))
                .thenReturn(Optional.of(inventarioExistente));
        when(loteInventarioRepository.save(any(LoteInventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        estoqueMovimentacaoUseCase.execute(ponto, lote, -20);

        ArgumentCaptor<LoteInventario> captor = ArgumentCaptor.forClass(LoteInventario.class);
        verify(loteInventarioRepository).save(captor.capture());

        LoteInventario inventarioSalvo = captor.getValue();
        assertEquals(30, inventarioSalvo.getQuantidade());
    }

    @Test
    void deveProcessarMultiplasMovimentacoesParaMesmoPonto() {
        PontoDispensacao ponto = criarPontoDispensacao(1L, "CNES001", "Ponto 1");
        Lote lote = criarLote(1L, "LOTE001", 100);
        LoteInventario inventarioExistente = criarLoteInventario(1L, ponto, lote, 50);

        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(1L, 1L))
                .thenReturn(Optional.of(inventarioExistente));
        when(loteInventarioRepository.save(any(LoteInventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        estoqueMovimentacaoUseCase.execute(ponto, lote, 10);
        estoqueMovimentacaoUseCase.execute(ponto, lote, 20);

        verify(loteInventarioRepository, times(2)).findByPontoDispensacaoIdAndLoteId(1L, 1L);
        verify(loteInventarioRepository, times(2)).save(any(LoteInventario.class));
    }

    @Test
    void deveInicializarNovoInventarioComQuantidadeZero() {
        PontoDispensacao ponto = criarPontoDispensacao(3L, "CNES003", "Ponto 3");
        Lote lote = criarLote(3L, "LOTE003", 50);

        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(3L, 3L))
                .thenReturn(Optional.empty());
        when(loteInventarioRepository.save(any(LoteInventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        estoqueMovimentacaoUseCase.execute(ponto, lote, 40);

        ArgumentCaptor<LoteInventario> captor = ArgumentCaptor.forClass(LoteInventario.class);
        verify(loteInventarioRepository).save(captor.capture());

        LoteInventario inventarioSalvo = captor.getValue();
        assertEquals(40, inventarioSalvo.getQuantidade());
        assertEquals(ponto.getId(), inventarioSalvo.getPontoDispensacao().getId());
        assertEquals(lote.getId(), inventarioSalvo.getLote().getId());
    }

    @Test
    void deveManterReferenciasDoPontoELote() {
        PontoDispensacao ponto = criarPontoDispensacao(4L, "CNES004", "Ponto 4");
        Lote lote = criarLote(4L, "LOTE004", 150);

        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(4L, 4L))
                .thenReturn(Optional.empty());
        when(loteInventarioRepository.save(any(LoteInventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        estoqueMovimentacaoUseCase.execute(ponto, lote, 60);

        ArgumentCaptor<LoteInventario> captor = ArgumentCaptor.forClass(LoteInventario.class);
        verify(loteInventarioRepository).save(captor.capture());

        LoteInventario inventarioSalvo = captor.getValue();
        assertSame(ponto, inventarioSalvo.getPontoDispensacao());
        assertSame(lote, inventarioSalvo.getLote());
    }

    private PontoDispensacao criarPontoDispensacao(Long id, String cnes, String nome) {
        PontoDispensacao ponto = new PontoDispensacao();
        ponto.setId(id);
        ponto.setCnes(cnes);
        ponto.setNome(nome);
        ponto.setTipo("TIPO_TESTE");
        ponto.setEmailResponsavel("teste@teste.com");
        ponto.setClientId(1L);
        return ponto;
    }

    private Lote criarLote(Long id, String numeroLote, Integer quantidade) {
        Insumo insumo = new Insumo();
        insumo.setId(1L);
        insumo.setNomeGenerico("Insumo Teste");

        Lote lote = new Lote();
        lote.setId(id);
        lote.setNumeroLote(numeroLote);
        lote.setInsumo(insumo);
        lote.setQuantidade(quantidade);
        lote.setDataValidade(LocalDate.of(2026, 12, 31));
        lote.setDataFabricacao(LocalDate.of(2026, 1, 1));
        lote.setIdUser(1L);
        return lote;
    }

    private LoteInventario criarLoteInventario(Long id, PontoDispensacao ponto, Lote lote, Integer quantidade) {
        LoteInventario inventario = new LoteInventario();
        inventario.setId(id);
        inventario.setPontoDispensacao(ponto);
        inventario.setLote(lote);
        inventario.setQuantidade(quantidade);
        inventario.setDataHoraChegada(LocalDateTime.now());
        return inventario;
    }
}

