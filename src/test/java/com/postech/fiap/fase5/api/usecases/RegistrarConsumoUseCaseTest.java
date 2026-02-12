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
import com.postech.fiap.fase5.infrastructure.exceptions.ApplicationNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
    private ConsumoValidation validation1;

    @Mock
    private ConsumoValidation validation2;

    @InjectMocks
    private RegistrarConsumoUseCase registrarConsumoUseCase;

    @Test
    void deveRegistrarConsumoComSucesso() {
        Long clientId = 1L;
        String cnes = "CNES001";
        String numeroLote = "LOTE001";
        Integer quantidadeConsumida = 10;
        LocalDateTime dataHoraEvento = LocalDateTime.now();

        ItemConsumoDTO itemConsumo = new ItemConsumoDTO(numeroLote, quantidadeConsumida, dataHoraEvento);
        RegistroConsumoDTO registroConsumo = new RegistroConsumoDTO(cnes, List.of(itemConsumo));

        PontoDispensacao pontoDispensacao = criarPontoDispensacao(1L, cnes, "Ponto 1", clientId);
        LoteInventario loteInventario = criarLoteInventario(1L, pontoDispensacao, numeroLote, 50);

        List<ConsumoValidation> validations = List.of(validation1, validation2);
        registrarConsumoUseCase = new RegistrarConsumoUseCase(
                loteInventarioRepository,
                historicoConsumoRepository,
                pontoDispensacaoRepository,
                validations
        );

        when(pontoDispensacaoRepository.findByClientIdAndCnes(clientId, cnes))
                .thenReturn(Optional.of(pontoDispensacao));
        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteNumeroLote(1L, numeroLote))
                .thenReturn(Optional.of(loteInventario));
        when(loteInventarioRepository.save(any(LoteInventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(historicoConsumoRepository.save(any(HistoricoConsumo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        registrarConsumoUseCase.execute(registroConsumo, clientId);

        verify(validation1).validate(registroConsumo, clientId);
        verify(validation2).validate(registroConsumo, clientId);
        verify(pontoDispensacaoRepository).findByClientIdAndCnes(clientId, cnes);
        verify(loteInventarioRepository).findByPontoDispensacaoIdAndLoteNumeroLote(1L, numeroLote);
        verify(loteInventarioRepository).save(any(LoteInventario.class));
        verify(historicoConsumoRepository).save(any(HistoricoConsumo.class));
    }

    @Test
    void deveAtualizarQuantidadeInventarioCorretamente() {
        Long clientId = 1L;
        String cnes = "CNES001";
        String numeroLote = "LOTE001";
        Integer quantidadeConsumida = 15;
        LocalDateTime dataHoraEvento = LocalDateTime.now();

        ItemConsumoDTO itemConsumo = new ItemConsumoDTO(numeroLote, quantidadeConsumida, dataHoraEvento);
        RegistroConsumoDTO registroConsumo = new RegistroConsumoDTO(cnes, List.of(itemConsumo));

        PontoDispensacao pontoDispensacao = criarPontoDispensacao(1L, cnes, "Ponto 1", clientId);
        LoteInventario loteInventario = criarLoteInventario(1L, pontoDispensacao, numeroLote, 100);

        List<ConsumoValidation> validations = List.of();
        registrarConsumoUseCase = new RegistrarConsumoUseCase(
                loteInventarioRepository,
                historicoConsumoRepository,
                pontoDispensacaoRepository,
                validations
        );

        when(pontoDispensacaoRepository.findByClientIdAndCnes(clientId, cnes))
                .thenReturn(Optional.of(pontoDispensacao));
        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteNumeroLote(1L, numeroLote))
                .thenReturn(Optional.of(loteInventario));
        when(loteInventarioRepository.save(any(LoteInventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(historicoConsumoRepository.save(any(HistoricoConsumo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        registrarConsumoUseCase.execute(registroConsumo, clientId);

        ArgumentCaptor<LoteInventario> loteCaptor = ArgumentCaptor.forClass(LoteInventario.class);
        verify(loteInventarioRepository).save(loteCaptor.capture());

        LoteInventario loteAtualizado = loteCaptor.getValue();
        assertEquals(85, loteAtualizado.getQuantidade());
    }

    @Test
    void deveRegistrarHistoricoConsumoCorretamente() {
        Long clientId = 1L;
        String cnes = "CNES001";
        String numeroLote = "LOTE001";
        Integer quantidadeConsumida = 20;
        LocalDateTime dataHoraEvento = LocalDateTime.now();

        ItemConsumoDTO itemConsumo = new ItemConsumoDTO(numeroLote, quantidadeConsumida, dataHoraEvento);
        RegistroConsumoDTO registroConsumo = new RegistroConsumoDTO(cnes, List.of(itemConsumo));

        PontoDispensacao pontoDispensacao = criarPontoDispensacao(1L, cnes, "Ponto 1", clientId);
        LoteInventario loteInventario = criarLoteInventario(1L, pontoDispensacao, numeroLote, 100);

        List<ConsumoValidation> validations = List.of();
        registrarConsumoUseCase = new RegistrarConsumoUseCase(
                loteInventarioRepository,
                historicoConsumoRepository,
                pontoDispensacaoRepository,
                validations
        );

        when(pontoDispensacaoRepository.findByClientIdAndCnes(clientId, cnes))
                .thenReturn(Optional.of(pontoDispensacao));
        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteNumeroLote(1L, numeroLote))
                .thenReturn(Optional.of(loteInventario));
        when(loteInventarioRepository.save(any(LoteInventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(historicoConsumoRepository.save(any(HistoricoConsumo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        registrarConsumoUseCase.execute(registroConsumo, clientId);

        ArgumentCaptor<HistoricoConsumo> historicoCaptor = ArgumentCaptor.forClass(HistoricoConsumo.class);
        verify(historicoConsumoRepository).save(historicoCaptor.capture());

        HistoricoConsumo historico = historicoCaptor.getValue();
        assertEquals(pontoDispensacao, historico.getPontoDispensacao());
        assertEquals(loteInventario.getLote(), historico.getLote());
        assertEquals(quantidadeConsumida, historico.getQuantidade());
        assertEquals(dataHoraEvento, historico.getDataHora());
    }

    @Test
    void deveProcessarMultiplosItensConsumo() {
        Long clientId = 1L;
        String cnes = "CNES001";
        LocalDateTime dataHoraEvento = LocalDateTime.now();

        ItemConsumoDTO item1 = new ItemConsumoDTO("LOTE001", 10, dataHoraEvento);
        ItemConsumoDTO item2 = new ItemConsumoDTO("LOTE002", 5, dataHoraEvento);
        RegistroConsumoDTO registroConsumo = new RegistroConsumoDTO(cnes, List.of(item1, item2));

        PontoDispensacao pontoDispensacao = criarPontoDispensacao(1L, cnes, "Ponto 1", clientId);
        LoteInventario loteInventario1 = criarLoteInventario(1L, pontoDispensacao, "LOTE001", 50);
        LoteInventario loteInventario2 = criarLoteInventario(2L, pontoDispensacao, "LOTE002", 30);

        List<ConsumoValidation> validations = List.of();
        registrarConsumoUseCase = new RegistrarConsumoUseCase(
                loteInventarioRepository,
                historicoConsumoRepository,
                pontoDispensacaoRepository,
                validations
        );

        when(pontoDispensacaoRepository.findByClientIdAndCnes(clientId, cnes))
                .thenReturn(Optional.of(pontoDispensacao));
        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteNumeroLote(1L, "LOTE001"))
                .thenReturn(Optional.of(loteInventario1));
        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteNumeroLote(1L, "LOTE002"))
                .thenReturn(Optional.of(loteInventario2));
        when(loteInventarioRepository.save(any(LoteInventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(historicoConsumoRepository.save(any(HistoricoConsumo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        registrarConsumoUseCase.execute(registroConsumo, clientId);

        verify(loteInventarioRepository, times(2)).save(any(LoteInventario.class));
        verify(historicoConsumoRepository, times(2)).save(any(HistoricoConsumo.class));
    }

    @Test
    void deveLancarExcecaoQuandoPontoDispensacaoNaoEncontrado() {
        Long clientId = 1L;
        String cnes = "CNES999";
        String numeroLote = "LOTE001";
        Integer quantidadeConsumida = 10;
        LocalDateTime dataHoraEvento = LocalDateTime.now();

        ItemConsumoDTO itemConsumo = new ItemConsumoDTO(numeroLote, quantidadeConsumida, dataHoraEvento);
        RegistroConsumoDTO registroConsumo = new RegistroConsumoDTO(cnes, List.of(itemConsumo));

        List<ConsumoValidation> validations = List.of();
        registrarConsumoUseCase = new RegistrarConsumoUseCase(
                loteInventarioRepository,
                historicoConsumoRepository,
                pontoDispensacaoRepository,
                validations
        );

        when(pontoDispensacaoRepository.findByClientIdAndCnes(clientId, cnes))
                .thenReturn(Optional.empty());

        ApplicationNotFoundException exception = assertThrows(
                ApplicationNotFoundException.class,
                () -> registrarConsumoUseCase.execute(registroConsumo, clientId)
        );

        assertEquals("Ponto de Dispensação não encontrado.", exception.getMessage());
        verify(pontoDispensacaoRepository).findByClientIdAndCnes(clientId, cnes);
        verify(loteInventarioRepository, never()).save(any());
        verify(historicoConsumoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoLoteInventarioNaoEncontrado() {
        Long clientId = 1L;
        String cnes = "CNES001";
        String numeroLote = "LOTE999";
        Integer quantidadeConsumida = 10;
        LocalDateTime dataHoraEvento = LocalDateTime.now();

        ItemConsumoDTO itemConsumo = new ItemConsumoDTO(numeroLote, quantidadeConsumida, dataHoraEvento);
        RegistroConsumoDTO registroConsumo = new RegistroConsumoDTO(cnes, List.of(itemConsumo));

        PontoDispensacao pontoDispensacao = criarPontoDispensacao(1L, cnes, "Ponto 1", clientId);

        List<ConsumoValidation> validations = List.of();
        registrarConsumoUseCase = new RegistrarConsumoUseCase(
                loteInventarioRepository,
                historicoConsumoRepository,
                pontoDispensacaoRepository,
                validations
        );

        when(pontoDispensacaoRepository.findByClientIdAndCnes(clientId, cnes))
                .thenReturn(Optional.of(pontoDispensacao));
        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteNumeroLote(1L, numeroLote))
                .thenReturn(Optional.empty());

        ApplicationNotFoundException exception = assertThrows(
                ApplicationNotFoundException.class,
                () -> registrarConsumoUseCase.execute(registroConsumo, clientId)
        );

        assertEquals("Lote ID " + numeroLote + " não encontrado no inventário deste ponto.", exception.getMessage());
        verify(loteInventarioRepository, never()).save(any());
        verify(historicoConsumoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoSaldoInsuficiente() {
        Long clientId = 1L;
        String cnes = "CNES001";
        String numeroLote = "LOTE001";
        Integer quantidadeConsumida = 100;
        LocalDateTime dataHoraEvento = LocalDateTime.now();

        ItemConsumoDTO itemConsumo = new ItemConsumoDTO(numeroLote, quantidadeConsumida, dataHoraEvento);
        RegistroConsumoDTO registroConsumo = new RegistroConsumoDTO(cnes, List.of(itemConsumo));

        PontoDispensacao pontoDispensacao = criarPontoDispensacao(1L, cnes, "Ponto 1", clientId);
        LoteInventario loteInventario = criarLoteInventario(1L, pontoDispensacao, numeroLote, 50);

        List<ConsumoValidation> validations = List.of();
        registrarConsumoUseCase = new RegistrarConsumoUseCase(
                loteInventarioRepository,
                historicoConsumoRepository,
                pontoDispensacaoRepository,
                validations
        );

        when(pontoDispensacaoRepository.findByClientIdAndCnes(clientId, cnes))
                .thenReturn(Optional.of(pontoDispensacao));
        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteNumeroLote(1L, numeroLote))
                .thenReturn(Optional.of(loteInventario));

        ApplicationNotFoundException exception = assertThrows(
                ApplicationNotFoundException.class,
                () -> registrarConsumoUseCase.execute(registroConsumo, clientId)
        );

        assertTrue(exception.getMessage().contains("Saldo insuficiente para o lote"));
        assertTrue(exception.getMessage().contains("Disponível: 50"));
        verify(loteInventarioRepository, never()).save(any());
        verify(historicoConsumoRepository, never()).save(any());
    }

    @Test
    void deveValidarSaldoExatoSemLancarExcecao() {
        Long clientId = 1L;
        String cnes = "CNES001";
        String numeroLote = "LOTE001";
        Integer quantidadeConsumida = 50;
        LocalDateTime dataHoraEvento = LocalDateTime.now();

        ItemConsumoDTO itemConsumo = new ItemConsumoDTO(numeroLote, quantidadeConsumida, dataHoraEvento);
        RegistroConsumoDTO registroConsumo = new RegistroConsumoDTO(cnes, List.of(itemConsumo));

        PontoDispensacao pontoDispensacao = criarPontoDispensacao(1L, cnes, "Ponto 1", clientId);
        LoteInventario loteInventario = criarLoteInventario(1L, pontoDispensacao, numeroLote, 50);

        List<ConsumoValidation> validations = List.of();
        registrarConsumoUseCase = new RegistrarConsumoUseCase(
                loteInventarioRepository,
                historicoConsumoRepository,
                pontoDispensacaoRepository,
                validations
        );

        when(pontoDispensacaoRepository.findByClientIdAndCnes(clientId, cnes))
                .thenReturn(Optional.of(pontoDispensacao));
        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteNumeroLote(1L, numeroLote))
                .thenReturn(Optional.of(loteInventario));
        when(loteInventarioRepository.save(any(LoteInventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(historicoConsumoRepository.save(any(HistoricoConsumo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> registrarConsumoUseCase.execute(registroConsumo, clientId));

        ArgumentCaptor<LoteInventario> loteCaptor = ArgumentCaptor.forClass(LoteInventario.class);
        verify(loteInventarioRepository).save(loteCaptor.capture());

        LoteInventario loteAtualizado = loteCaptor.getValue();
        assertEquals(0, loteAtualizado.getQuantidade());
    }

    @Test
    void deveExecutarTodasValidacoesAntesDeProcessar() {
        Long clientId = 1L;
        String cnes = "CNES001";
        String numeroLote = "LOTE001";
        Integer quantidadeConsumida = 10;
        LocalDateTime dataHoraEvento = LocalDateTime.now();

        ItemConsumoDTO itemConsumo = new ItemConsumoDTO(numeroLote, quantidadeConsumida, dataHoraEvento);
        RegistroConsumoDTO registroConsumo = new RegistroConsumoDTO(cnes, List.of(itemConsumo));

        PontoDispensacao pontoDispensacao = criarPontoDispensacao(1L, cnes, "Ponto 1", clientId);
        LoteInventario loteInventario = criarLoteInventario(1L, pontoDispensacao, numeroLote, 50);

        List<ConsumoValidation> validations = List.of(validation1, validation2);
        registrarConsumoUseCase = new RegistrarConsumoUseCase(
                loteInventarioRepository,
                historicoConsumoRepository,
                pontoDispensacaoRepository,
                validations
        );

        when(pontoDispensacaoRepository.findByClientIdAndCnes(clientId, cnes))
                .thenReturn(Optional.of(pontoDispensacao));
        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteNumeroLote(1L, numeroLote))
                .thenReturn(Optional.of(loteInventario));
        when(loteInventarioRepository.save(any(LoteInventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(historicoConsumoRepository.save(any(HistoricoConsumo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        registrarConsumoUseCase.execute(registroConsumo, clientId);

        verify(validation1, times(1)).validate(registroConsumo, clientId);
        verify(validation2, times(1)).validate(registroConsumo, clientId);
    }

    @Test
    void deveProcessarConsumoComQuantidadeZeroNoInventarioAposConsumoTotal() {
        Long clientId = 1L;
        String cnes = "CNES001";
        String numeroLote = "LOTE001";
        Integer quantidadeConsumida = 25;
        LocalDateTime dataHoraEvento = LocalDateTime.now();

        ItemConsumoDTO itemConsumo = new ItemConsumoDTO(numeroLote, quantidadeConsumida, dataHoraEvento);
        RegistroConsumoDTO registroConsumo = new RegistroConsumoDTO(cnes, List.of(itemConsumo));

        PontoDispensacao pontoDispensacao = criarPontoDispensacao(1L, cnes, "Ponto 1", clientId);
        LoteInventario loteInventario = criarLoteInventario(1L, pontoDispensacao, numeroLote, 25);

        List<ConsumoValidation> validations = List.of();
        registrarConsumoUseCase = new RegistrarConsumoUseCase(
                loteInventarioRepository,
                historicoConsumoRepository,
                pontoDispensacaoRepository,
                validations
        );

        when(pontoDispensacaoRepository.findByClientIdAndCnes(clientId, cnes))
                .thenReturn(Optional.of(pontoDispensacao));
        when(loteInventarioRepository.findByPontoDispensacaoIdAndLoteNumeroLote(1L, numeroLote))
                .thenReturn(Optional.of(loteInventario));
        when(loteInventarioRepository.save(any(LoteInventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(historicoConsumoRepository.save(any(HistoricoConsumo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        registrarConsumoUseCase.execute(registroConsumo, clientId);

        ArgumentCaptor<LoteInventario> loteCaptor = ArgumentCaptor.forClass(LoteInventario.class);
        verify(loteInventarioRepository).save(loteCaptor.capture());

        LoteInventario loteAtualizado = loteCaptor.getValue();
        assertEquals(0, loteAtualizado.getQuantidade());

        verify(historicoConsumoRepository).save(any(HistoricoConsumo.class));
    }

    private PontoDispensacao criarPontoDispensacao(Long id, String cnes, String nome, Long clientId) {
        PontoDispensacao ponto = new PontoDispensacao();
        ponto.setId(id);
        ponto.setCnes(cnes);
        ponto.setNome(nome);
        ponto.setTipo("Hospital");
        ponto.setClientId(clientId);
        return ponto;
    }

    private LoteInventario criarLoteInventario(Long id, PontoDispensacao pontoDispensacao, String numeroLote, Integer quantidade) {
        Lote lote = new Lote();
        lote.setId(id);
        lote.setNumeroLote(numeroLote);

        LoteInventario loteInventario = new LoteInventario();
        loteInventario.setId(id);
        loteInventario.setPontoDispensacao(pontoDispensacao);
        loteInventario.setLote(lote);
        loteInventario.setQuantidade(quantidade);
        loteInventario.setDataHoraChegada(LocalDateTime.now());
        return loteInventario;
    }
}

