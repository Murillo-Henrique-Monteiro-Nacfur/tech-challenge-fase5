package com.postech.fiap.fase5.api.services;

import com.postech.fiap.fase5.api.dto.estimativa.*;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import com.postech.fiap.fase5.api.services.email.EmailService;
import com.postech.fiap.fase5.api.services.email.template.DiarioEmailTemplateBuilder;
import com.postech.fiap.fase5.api.services.email.template.MensalEmailTemplateBuilder;
import com.postech.fiap.fase5.api.services.email.template.ValidadeEmailTemplateBuilder;
import com.postech.fiap.fase5.api.services.notificacao.DiarioCriticalItemsFilter;
import com.postech.fiap.fase5.api.services.notificacao.EmailSubjectFactory;
import com.postech.fiap.fase5.api.services.notificacao.MensalCriticalItemsFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @Mock
    private EmailService emailService;

    @Mock
    private DiarioEmailTemplateBuilder diarioEmailTemplateBuilder;

    @Mock
    private MensalEmailTemplateBuilder mensalEmailTemplateBuilder;

    @Mock
    private ValidadeEmailTemplateBuilder validadeEmailTemplateBuilder;

    @Mock
    private DiarioCriticalItemsFilter diarioCriticalItemsFilter;

    @Mock
    private MensalCriticalItemsFilter mensalCriticalItemsFilter;

    @InjectMocks
    private NotificacaoService notificacaoService;

    private PontoDispensacao pontoDispensacao;

    @BeforeEach
    void setUp() {
        pontoDispensacao = new PontoDispensacao();
        pontoDispensacao.setId(1L);
        pontoDispensacao.setNome("Hospital Central");
        pontoDispensacao.setEmailResponsavel("responsavel@hospital.com");
        pontoDispensacao.setCnes("1234567");
        pontoDispensacao.setTipo("Hospital");
    }

    @Test
    void deveNotificarPrevisaoDiariaComItensCriticos() {
        InsumoDiarioDTO insumoCritico = InsumoDiarioDTO.builder()
                .nomeInsumo("Paracetamol")
                .statusPrevisao("CRITICO")
                .build();

        List<InsumoDiarioDTO> todoInsumos = List.of(insumoCritico);
        List<InsumoDiarioDTO> itensCriticos = List.of(insumoCritico);

        InventarioDiarioDTO inventario = InventarioDiarioDTO.builder()
                .pontoDispensacao(pontoDispensacao)
                .insumos(todoInsumos)
                .build();

        when(diarioCriticalItemsFilter.filterCriticalItems(todoInsumos)).thenReturn(itensCriticos);
        when(diarioEmailTemplateBuilder.build(anyString(), anyList())).thenReturn("Email content");

        notificacaoService.notificarPrevisaoDiaria(List.of(inventario));

        verify(diarioCriticalItemsFilter).filterCriticalItems(todoInsumos);
        verify(diarioEmailTemplateBuilder).build("Hospital Central", itensCriticos);
        verify(emailService).sendEmail(
                eq("responsavel@hospital.com"),
                eq(EmailSubjectFactory.createDailyAlertSubject()),
                eq("Email content")
        );
    }

    @Test
    void naoDeveNotificarPrevisaoDiariaQuandoSemItensCriticos() {
        List<InsumoDiarioDTO> todoInsumos = new ArrayList<>();

        InventarioDiarioDTO inventario = InventarioDiarioDTO.builder()
                .pontoDispensacao(pontoDispensacao)
                .insumos(todoInsumos)
                .build();

        when(diarioCriticalItemsFilter.filterCriticalItems(todoInsumos)).thenReturn(Collections.emptyList());

        notificacaoService.notificarPrevisaoDiaria(List.of(inventario));

        verify(diarioCriticalItemsFilter).filterCriticalItems(todoInsumos);
        verify(diarioEmailTemplateBuilder, never()).build(anyString(), anyList());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void naoDeveNotificarPrevisaoDiariaQuandoEmailResponsavelNull() {
        pontoDispensacao.setEmailResponsavel(null);

        InsumoDiarioDTO insumoCritico = InsumoDiarioDTO.builder()
                .nomeInsumo("Paracetamol")
                .statusPrevisao("CRITICO")
                .build();

        InventarioDiarioDTO inventario = InventarioDiarioDTO.builder()
                .pontoDispensacao(pontoDispensacao)
                .insumos(List.of(insumoCritico))
                .build();

        notificacaoService.notificarPrevisaoDiaria(List.of(inventario));

        verify(diarioCriticalItemsFilter, never()).filterCriticalItems(anyList());
        verify(diarioEmailTemplateBuilder, never()).build(anyString(), anyList());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void deveProcessarMultiplosInventariosDiarios() {
        InsumoDiarioDTO insumoCritico1 = InsumoDiarioDTO.builder()
                .nomeInsumo("Paracetamol")
                .statusPrevisao("CRITICO")
                .build();

        InsumoDiarioDTO insumoCritico2 = InsumoDiarioDTO.builder()
                .nomeInsumo("Dipirona")
                .statusPrevisao("CRITICO")
                .build();

        PontoDispensacao ponto2 = new PontoDispensacao();
        ponto2.setId(2L);
        ponto2.setNome("UBS Norte");
        ponto2.setEmailResponsavel("ubs@saude.com");

        InventarioDiarioDTO inventario1 = InventarioDiarioDTO.builder()
                .pontoDispensacao(pontoDispensacao)
                .insumos(List.of(insumoCritico1))
                .build();

        InventarioDiarioDTO inventario2 = InventarioDiarioDTO.builder()
                .pontoDispensacao(ponto2)
                .insumos(List.of(insumoCritico2))
                .build();

        when(diarioCriticalItemsFilter.filterCriticalItems(anyList()))
                .thenReturn(List.of(insumoCritico1))
                .thenReturn(List.of(insumoCritico2));
        when(diarioEmailTemplateBuilder.build(anyString(), anyList())).thenReturn("Email content");

        notificacaoService.notificarPrevisaoDiaria(List.of(inventario1, inventario2));

        verify(diarioCriticalItemsFilter, times(2)).filterCriticalItems(anyList());
        verify(diarioEmailTemplateBuilder, times(2)).build(anyString(), anyList());
        verify(emailService, times(2)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void deveNotificarPrevisaoMensalComItensCriticos() {
        InsumoMensalDTO insumoCritico = InsumoMensalDTO.builder()
                .nomeInsumo("Paracetamol")
                .statusPrevisaoSazonal("CRITICO")
                .build();

        List<InsumoMensalDTO> todoInsumos = List.of(insumoCritico);
        List<InsumoMensalDTO> itensCriticos = List.of(insumoCritico);

        InventarioMensalDTO inventario = InventarioMensalDTO.builder()
                .pontoDispensacao(pontoDispensacao)
                .insumos(todoInsumos)
                .build();

        when(mensalCriticalItemsFilter.filterCriticalItems(todoInsumos)).thenReturn(itensCriticos);
        when(mensalEmailTemplateBuilder.build(anyString(), anyList())).thenReturn("Email content");

        notificacaoService.notificarPrevisaoMensal(List.of(inventario));

        verify(mensalCriticalItemsFilter).filterCriticalItems(todoInsumos);
        verify(mensalEmailTemplateBuilder).build("Hospital Central", itensCriticos);
        verify(emailService).sendEmail(
                eq("responsavel@hospital.com"),
                eq(EmailSubjectFactory.createMonthlyAlertSubject()),
                eq("Email content")
        );
    }

    @Test
    void naoDeveNotificarPrevisaoMensalQuandoSemItensCriticos() {
        List<InsumoMensalDTO> todoInsumos = new ArrayList<>();

        InventarioMensalDTO inventario = InventarioMensalDTO.builder()
                .pontoDispensacao(pontoDispensacao)
                .insumos(todoInsumos)
                .build();

        when(mensalCriticalItemsFilter.filterCriticalItems(todoInsumos)).thenReturn(Collections.emptyList());

        notificacaoService.notificarPrevisaoMensal(List.of(inventario));

        verify(mensalCriticalItemsFilter).filterCriticalItems(todoInsumos);
        verify(mensalEmailTemplateBuilder, never()).build(anyString(), anyList());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void naoDeveNotificarPrevisaoMensalQuandoEmailResponsavelNull() {
        pontoDispensacao.setEmailResponsavel(null);

        InsumoMensalDTO insumoCritico = InsumoMensalDTO.builder()
                .nomeInsumo("Paracetamol")
                .statusPrevisaoSazonal("CRITICO")
                .build();

        InventarioMensalDTO inventario = InventarioMensalDTO.builder()
                .pontoDispensacao(pontoDispensacao)
                .insumos(List.of(insumoCritico))
                .build();

        notificacaoService.notificarPrevisaoMensal(List.of(inventario));

        verify(mensalCriticalItemsFilter, never()).filterCriticalItems(anyList());
        verify(mensalEmailTemplateBuilder, never()).build(anyString(), anyList());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void deveProcessarMultiplosInventariosMensais() {
        InsumoMensalDTO insumoCritico1 = InsumoMensalDTO.builder()
                .nomeInsumo("Paracetamol")
                .statusPrevisaoSazonal("CRITICO")
                .build();

        InsumoMensalDTO insumoCritico2 = InsumoMensalDTO.builder()
                .nomeInsumo("Dipirona")
                .statusPrevisaoSazonal("CRITICO")
                .build();

        PontoDispensacao ponto2 = new PontoDispensacao();
        ponto2.setId(2L);
        ponto2.setNome("UBS Norte");
        ponto2.setEmailResponsavel("ubs@saude.com");

        InventarioMensalDTO inventario1 = InventarioMensalDTO.builder()
                .pontoDispensacao(pontoDispensacao)
                .insumos(List.of(insumoCritico1))
                .build();

        InventarioMensalDTO inventario2 = InventarioMensalDTO.builder()
                .pontoDispensacao(ponto2)
                .insumos(List.of(insumoCritico2))
                .build();

        when(mensalCriticalItemsFilter.filterCriticalItems(anyList()))
                .thenReturn(List.of(insumoCritico1))
                .thenReturn(List.of(insumoCritico2));
        when(mensalEmailTemplateBuilder.build(anyString(), anyList())).thenReturn("Email content");

        notificacaoService.notificarPrevisaoMensal(List.of(inventario1, inventario2));

        verify(mensalCriticalItemsFilter, times(2)).filterCriticalItems(anyList());
        verify(mensalEmailTemplateBuilder, times(2)).build(anyString(), anyList());
        verify(emailService, times(2)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void deveNotificarRiscoValidade() {
        ItemRiscoValidadeDTO itemEmRisco = ItemRiscoValidadeDTO.builder()
                .nomeInsumo("Paracetamol")
                .build();

        AlertaValidadeDTO alerta = AlertaValidadeDTO.builder()
                .pontoDispensacao(pontoDispensacao)
                .itensEmRisco(List.of(itemEmRisco))
                .build();

        when(validadeEmailTemplateBuilder.build(anyString(), anyList())).thenReturn("Email content");

        notificacaoService.notificarRiscoValidade(List.of(alerta));

        verify(validadeEmailTemplateBuilder).build("Hospital Central", List.of(itemEmRisco));
        verify(emailService).sendEmail(
                eq("responsavel@hospital.com"),
                eq(EmailSubjectFactory.createValidityAlertSubject()),
                eq("Email content")
        );
    }

    @Test
    void naoDeveNotificarRiscoValidadeQuandoEmailResponsavelNull() {
        pontoDispensacao.setEmailResponsavel(null);

        ItemRiscoValidadeDTO itemEmRisco = ItemRiscoValidadeDTO.builder()
                .nomeInsumo("Paracetamol")
                .build();

        AlertaValidadeDTO alerta = AlertaValidadeDTO.builder()
                .pontoDispensacao(pontoDispensacao)
                .itensEmRisco(List.of(itemEmRisco))
                .build();

        notificacaoService.notificarRiscoValidade(List.of(alerta));

        verify(validadeEmailTemplateBuilder, never()).build(anyString(), anyList());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void deveProcessarMultiplosAlertasDeValidade() {
        ItemRiscoValidadeDTO itemEmRisco1 = ItemRiscoValidadeDTO.builder()
                .nomeInsumo("Paracetamol")
                .build();

        ItemRiscoValidadeDTO itemEmRisco2 = ItemRiscoValidadeDTO.builder()
                .nomeInsumo("Dipirona")
                .build();

        PontoDispensacao ponto2 = new PontoDispensacao();
        ponto2.setId(2L);
        ponto2.setNome("UBS Norte");
        ponto2.setEmailResponsavel("ubs@saude.com");

        AlertaValidadeDTO alerta1 = AlertaValidadeDTO.builder()
                .pontoDispensacao(pontoDispensacao)
                .itensEmRisco(List.of(itemEmRisco1))
                .build();

        AlertaValidadeDTO alerta2 = AlertaValidadeDTO.builder()
                .pontoDispensacao(ponto2)
                .itensEmRisco(List.of(itemEmRisco2))
                .build();

        when(validadeEmailTemplateBuilder.build(anyString(), anyList())).thenReturn("Email content");

        notificacaoService.notificarRiscoValidade(List.of(alerta1, alerta2));

        verify(validadeEmailTemplateBuilder, times(2)).build(anyString(), anyList());
        verify(emailService, times(2)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void deveProcessarListaVaziaDeInventariosDiarios() {
        assertThatCode(() -> notificacaoService.notificarPrevisaoDiaria(Collections.emptyList()))
                .doesNotThrowAnyException();

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void deveProcessarListaVaziaDeInventariosMensais() {
        assertThatCode(() -> notificacaoService.notificarPrevisaoMensal(Collections.emptyList()))
                .doesNotThrowAnyException();

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void deveProcessarListaVaziaDeAlertasValidade() {
        assertThatCode(() -> notificacaoService.notificarRiscoValidade(Collections.emptyList()))
                .doesNotThrowAnyException();

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}

