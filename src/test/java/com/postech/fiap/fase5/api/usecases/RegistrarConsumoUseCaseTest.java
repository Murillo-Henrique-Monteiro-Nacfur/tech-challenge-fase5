package com.postech.fiap.fase5.api.usecases;

import com.postech.fiap.fase5.api.dto.ItemConsumoDTO;
import com.postech.fiap.fase5.api.dto.RegistroConsumoDTO;
import com.postech.fiap.fase5.api.entities.HistoricoConsumo;
import com.postech.fiap.fase5.api.entities.Lote;
import com.postech.fiap.fase5.api.entities.LoteInventario;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import com.postech.fiap.fase5.api.repositories.HistoricoConsumoRepository;
import com.postech.fiap.fase5.api.repositories.LoteInventarioRepository;
import com.postech.fiap.fase5.api.repositories.PontoDispensacaoRepository;
import com.postech.fiap.fase5.api.validations.ConsumoValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrarConsumoUseCaseTest {

    @Mock
    private LoteInventarioRepository loteInventarioRepository;

    @Mock
    private HistoricoConsumoRepository historicoConsumoRepository;

    @Mock
    private PontoDispensacaoRepository pontoDispensacaoRepository;

    @Mock
    private ConsumoValidation validation;

    private RegistrarConsumoUseCase registrarConsumoUseCase;

    @BeforeEach
    void setUp() {
        registrarConsumoUseCase = new RegistrarConsumoUseCase(
                loteInventarioRepository,
                historicoConsumoRepository,
                pontoDispensacaoRepository,
                Collections.singletonList(validation)
        );
    }

    @Test
    void deveRegistrarConsumoComSucessoQuandoDadosValidos() {
        Long pontoDispensacaoId = 1L;
        Long clientId = 100L;
        Long loteId = 10L;
        Integer quantidadeConsumida = 50;
        Integer quantidadeDisponivel = 150;
        LocalDateTime dataHoraEvento = LocalDateTime.of(2026, 2, 10, 10, 30);

        PontoDispensacao pontoDispensacao = criarPontoDispensacao(pontoDispensacaoId, "CNES001", clientId);
        Lote lote = criarLote(loteId, "LOTE001");
        LoteInventario loteInventario = criarLoteInventario(1L, pontoDispensacao, lote, quantidadeDisponivel);

        ItemConsumoDTO itemConsumo = new ItemConsumoDTO(loteId, quantidadeConsumida, dataHoraEvento);
        List<ItemConsumoDTO> listaConsumo = Collections.singletonList(itemConsumo);
        RegistroConsumoDTO registroConsumo = new RegistroConsumoDTO(pontoDispensacaoId, listaConsumo);

        when(pontoDispensacaoRepository.findById(pontoDispensacaoId))
                .thenReturn(Optional.of(pontoDispensacao));
        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(pontoDispensacaoId, loteId))
                .thenReturn(Optional.of(loteInventario));
        when(loteInventarioRepository.save(any(LoteInventario.class)))
                .thenReturn(loteInventario);
        when(historicoConsumoRepository.save(any(HistoricoConsumo.class)))
                .thenReturn(new HistoricoConsumo());

        registrarConsumoUseCase.execute(registroConsumo, clientId);

        verify(validation).validate(registroConsumo, clientId);
        verify(pontoDispensacaoRepository).findById(pontoDispensacaoId);
        verify(loteInventarioRepository).findByPontoDispensacaoIdAndLoteId(pontoDispensacaoId, loteId);

        ArgumentCaptor<LoteInventario> loteCaptor = ArgumentCaptor.forClass(LoteInventario.class);
        verify(loteInventarioRepository).save(loteCaptor.capture());
        assertEquals(quantidadeDisponivel - quantidadeConsumida, loteCaptor.getValue().getQuantidade());

        ArgumentCaptor<HistoricoConsumo> historicoCaptor = ArgumentCaptor.forClass(HistoricoConsumo.class);
        verify(historicoConsumoRepository).save(historicoCaptor.capture());
        HistoricoConsumo historicoSalvo = historicoCaptor.getValue();
        assertEquals(pontoDispensacao, historicoSalvo.getPontoDispensacao());
        assertEquals(lote, historicoSalvo.getLote());
        assertEquals(quantidadeConsumida, historicoSalvo.getQuantidade());
        assertEquals(dataHoraEvento, historicoSalvo.getDataHora());
    }

    @Test
    void deveRegistrarMultiplosItensDeConsumoComSucesso() {
        Long pontoDispensacaoId = 1L;
        Long clientId = 100L;
        Long loteId1 = 10L;
        Long loteId2 = 20L;
        Integer quantidadeConsumida1 = 30;
        Integer quantidadeConsumida2 = 40;
        Integer quantidadeDisponivel1 = 100;
        Integer quantidadeDisponivel2 = 200;
        LocalDateTime dataHoraEvento = LocalDateTime.of(2026, 2, 10, 14, 0);

        PontoDispensacao pontoDispensacao = criarPontoDispensacao(pontoDispensacaoId, "CNES001", clientId);
        Lote lote1 = criarLote(loteId1, "LOTE001");
        Lote lote2 = criarLote(loteId2, "LOTE002");
        LoteInventario loteInventario1 = criarLoteInventario(1L, pontoDispensacao, lote1, quantidadeDisponivel1);
        LoteInventario loteInventario2 = criarLoteInventario(2L, pontoDispensacao, lote2, quantidadeDisponivel2);

        ItemConsumoDTO itemConsumo1 = new ItemConsumoDTO(loteId1, quantidadeConsumida1, dataHoraEvento);
        ItemConsumoDTO itemConsumo2 = new ItemConsumoDTO(loteId2, quantidadeConsumida2, dataHoraEvento);
        List<ItemConsumoDTO> listaConsumo = Arrays.asList(itemConsumo1, itemConsumo2);
        RegistroConsumoDTO registroConsumo = new RegistroConsumoDTO(pontoDispensacaoId, listaConsumo);

        when(pontoDispensacaoRepository.findById(pontoDispensacaoId))
                .thenReturn(Optional.of(pontoDispensacao));
        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(pontoDispensacaoId, loteId1))
                .thenReturn(Optional.of(loteInventario1));
        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(pontoDispensacaoId, loteId2))
                .thenReturn(Optional.of(loteInventario2));

        registrarConsumoUseCase.execute(registroConsumo, clientId);

        verify(validation).validate(registroConsumo, clientId);
        verify(pontoDispensacaoRepository).findById(pontoDispensacaoId);
        verify(loteInventarioRepository, times(2)).save(any(LoteInventario.class));
        verify(historicoConsumoRepository, times(2)).save(any(HistoricoConsumo.class));
    }

    @Test
    void deveLancarExcecaoQuandoPontoDispensacaoNaoEncontrado() {
        Long pontoDispensacaoId = 999L;
        Long clientId = 100L;

        ItemConsumoDTO itemConsumo = new ItemConsumoDTO(10L, 50, LocalDateTime.now());
        List<ItemConsumoDTO> listaConsumo = Collections.singletonList(itemConsumo);
        RegistroConsumoDTO registroConsumo = new RegistroConsumoDTO(pontoDispensacaoId, listaConsumo);

        when(pontoDispensacaoRepository.findById(pontoDispensacaoId))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> registrarConsumoUseCase.execute(registroConsumo, clientId)
        );

        assertEquals("Ponto de Dispensação não encontrado.", exception.getMessage());
        verify(validation).validate(registroConsumo, clientId);
        verify(pontoDispensacaoRepository).findById(pontoDispensacaoId);
        verify(loteInventarioRepository, never()).save(any(LoteInventario.class));
        verify(historicoConsumoRepository, never()).save(any(HistoricoConsumo.class));
    }

    @Test
    void deveLancarExcecaoQuandoLoteNaoEncontradoNoInventario() {
        Long pontoDispensacaoId = 1L;
        Long clientId = 100L;
        Long loteId = 999L;

        PontoDispensacao pontoDispensacao = criarPontoDispensacao(pontoDispensacaoId, "CNES001", clientId);

        ItemConsumoDTO itemConsumo = new ItemConsumoDTO(loteId, 50, LocalDateTime.now());
        List<ItemConsumoDTO> listaConsumo = Collections.singletonList(itemConsumo);
        RegistroConsumoDTO registroConsumo = new RegistroConsumoDTO(pontoDispensacaoId, listaConsumo);

        when(pontoDispensacaoRepository.findById(pontoDispensacaoId))
                .thenReturn(Optional.of(pontoDispensacao));
        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(pontoDispensacaoId, loteId))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> registrarConsumoUseCase.execute(registroConsumo, clientId)
        );

        assertEquals("Lote ID " + loteId + " não encontrado no inventário deste ponto.", exception.getMessage());
        verify(validation).validate(registroConsumo, clientId);
        verify(pontoDispensacaoRepository).findById(pontoDispensacaoId);
        verify(loteInventarioRepository).findByPontoDispensacaoIdAndLoteId(pontoDispensacaoId, loteId);
        verify(loteInventarioRepository, never()).save(any(LoteInventario.class));
        verify(historicoConsumoRepository, never()).save(any(HistoricoConsumo.class));
    }

    @Test
    void deveLancarExcecaoQuandoSaldoInsuficiente() {
        Long pontoDispensacaoId = 1L;
        Long clientId = 100L;
        Long loteId = 10L;
        Integer quantidadeConsumida = 200;
        Integer quantidadeDisponivel = 50;

        PontoDispensacao pontoDispensacao = criarPontoDispensacao(pontoDispensacaoId, "CNES001", clientId);
        Lote lote = criarLote(loteId, "LOTE001");
        LoteInventario loteInventario = criarLoteInventario(1L, pontoDispensacao, lote, quantidadeDisponivel);

        ItemConsumoDTO itemConsumo = new ItemConsumoDTO(loteId, quantidadeConsumida, LocalDateTime.now());
        List<ItemConsumoDTO> listaConsumo = Collections.singletonList(itemConsumo);
        RegistroConsumoDTO registroConsumo = new RegistroConsumoDTO(pontoDispensacaoId, listaConsumo);

        when(pontoDispensacaoRepository.findById(pontoDispensacaoId))
                .thenReturn(Optional.of(pontoDispensacao));
        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(pontoDispensacaoId, loteId))
                .thenReturn(Optional.of(loteInventario));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> registrarConsumoUseCase.execute(registroConsumo, clientId)
        );

        assertEquals("Saldo insuficiente para o lote " + loteId + ". Disponível: " + quantidadeDisponivel, exception.getMessage());
        verify(validation).validate(registroConsumo, clientId);
        verify(pontoDispensacaoRepository).findById(pontoDispensacaoId);
        verify(loteInventarioRepository).findByPontoDispensacaoIdAndLoteId(pontoDispensacaoId, loteId);
        verify(loteInventarioRepository, never()).save(any(LoteInventario.class));
        verify(historicoConsumoRepository, never()).save(any(HistoricoConsumo.class));
    }

    @Test
    void deveAtualizarQuantidadeCorretamenteAposConsumo() {
        Long pontoDispensacaoId = 1L;
        Long clientId = 100L;
        Long loteId = 10L;
        Integer quantidadeConsumida = 75;
        Integer quantidadeDisponivel = 100;
        Integer quantidadeEsperada = 25;

        PontoDispensacao pontoDispensacao = criarPontoDispensacao(pontoDispensacaoId, "CNES001", clientId);
        Lote lote = criarLote(loteId, "LOTE001");
        LoteInventario loteInventario = criarLoteInventario(1L, pontoDispensacao, lote, quantidadeDisponivel);

        ItemConsumoDTO itemConsumo = new ItemConsumoDTO(loteId, quantidadeConsumida, LocalDateTime.now());
        List<ItemConsumoDTO> listaConsumo = Collections.singletonList(itemConsumo);
        RegistroConsumoDTO registroConsumo = new RegistroConsumoDTO(pontoDispensacaoId, listaConsumo);

        when(pontoDispensacaoRepository.findById(pontoDispensacaoId))
                .thenReturn(Optional.of(pontoDispensacao));
        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(pontoDispensacaoId, loteId))
                .thenReturn(Optional.of(loteInventario));

        registrarConsumoUseCase.execute(registroConsumo, clientId);

        ArgumentCaptor<LoteInventario> captor = ArgumentCaptor.forClass(LoteInventario.class);
        verify(loteInventarioRepository).save(captor.capture());
        assertEquals(quantidadeEsperada, captor.getValue().getQuantidade());
    }

    @Test
    void deveZerarQuantidadeQuandoConsumoIgualAoDisponivel() {
        Long pontoDispensacaoId = 1L;
        Long clientId = 100L;
        Long loteId = 10L;
        Integer quantidadeConsumida = 100;
        Integer quantidadeDisponivel = 100;

        PontoDispensacao pontoDispensacao = criarPontoDispensacao(pontoDispensacaoId, "CNES001", clientId);
        Lote lote = criarLote(loteId, "LOTE001");
        LoteInventario loteInventario = criarLoteInventario(1L, pontoDispensacao, lote, quantidadeDisponivel);

        ItemConsumoDTO itemConsumo = new ItemConsumoDTO(loteId, quantidadeConsumida, LocalDateTime.now());
        List<ItemConsumoDTO> listaConsumo = Collections.singletonList(itemConsumo);
        RegistroConsumoDTO registroConsumo = new RegistroConsumoDTO(pontoDispensacaoId, listaConsumo);

        when(pontoDispensacaoRepository.findById(pontoDispensacaoId))
                .thenReturn(Optional.of(pontoDispensacao));
        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(pontoDispensacaoId, loteId))
                .thenReturn(Optional.of(loteInventario));

        registrarConsumoUseCase.execute(registroConsumo, clientId);

        ArgumentCaptor<LoteInventario> captor = ArgumentCaptor.forClass(LoteInventario.class);
        verify(loteInventarioRepository).save(captor.capture());
        assertEquals(0, captor.getValue().getQuantidade());
    }

    @Test
    void deveExecutarTodasAsValidacoesAntesDeProcessar() {
        Long pontoDispensacaoId = 1L;
        Long clientId = 100L;

        ItemConsumoDTO itemConsumo = new ItemConsumoDTO(10L, 50, LocalDateTime.now());
        List<ItemConsumoDTO> listaConsumo = Collections.singletonList(itemConsumo);
        RegistroConsumoDTO registroConsumo = new RegistroConsumoDTO(pontoDispensacaoId, listaConsumo);

        doThrow(new IllegalArgumentException("Validação falhou"))
                .when(validation).validate(registroConsumo, clientId);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> registrarConsumoUseCase.execute(registroConsumo, clientId)
        );

        assertEquals("Validação falhou", exception.getMessage());
        verify(validation).validate(registroConsumo, clientId);
        verify(pontoDispensacaoRepository, never()).findById(anyLong());
        verify(loteInventarioRepository, never()).save(any(LoteInventario.class));
        verify(historicoConsumoRepository, never()).save(any(HistoricoConsumo.class));
    }

    private PontoDispensacao criarPontoDispensacao(Long id, String cnes, Long clientId) {
        PontoDispensacao pontoDispensacao = new PontoDispensacao();
        pontoDispensacao.setId(id);
        pontoDispensacao.setCnes(cnes);
        pontoDispensacao.setNome("Ponto Teste");
        pontoDispensacao.setTipo("UBS");
        pontoDispensacao.setClientId(clientId);
        return pontoDispensacao;
    }

    private Lote criarLote(Long id, String numeroLote) {
        Lote lote = new Lote();
        lote.setId(id);
        lote.setNumeroLote(numeroLote);
        return lote;
    }

    private LoteInventario criarLoteInventario(Long id, PontoDispensacao pontoDispensacao, Lote lote, Integer quantidade) {
        LoteInventario loteInventario = new LoteInventario();
        loteInventario.setId(id);
        loteInventario.setPontoDispensacao(pontoDispensacao);
        loteInventario.setLote(lote);
        loteInventario.setQuantidade(quantidade);
        return loteInventario;
    }
}

