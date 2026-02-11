package com.postech.fiap.fase5.api.services;

import com.postech.fiap.fase5.api.dto.estimativa.*;
import com.postech.fiap.fase5.api.services.email.EmailService;
import com.postech.fiap.fase5.api.services.email.template.DiarioEmailTemplateBuilder;
import com.postech.fiap.fase5.api.services.email.template.MensalEmailTemplateBuilder;
import com.postech.fiap.fase5.api.services.email.template.ValidadeEmailTemplateBuilder;
import com.postech.fiap.fase5.api.services.notificacao.DiarioCriticalItemsFilter;
import com.postech.fiap.fase5.api.services.notificacao.EmailSubjectFactory;
import com.postech.fiap.fase5.api.services.notificacao.MensalCriticalItemsFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final EmailService emailService;
    private final DiarioEmailTemplateBuilder diarioEmailTemplateBuilder;
    private final MensalEmailTemplateBuilder mensalEmailTemplateBuilder;
    private final ValidadeEmailTemplateBuilder validadeEmailTemplateBuilder;
    private final DiarioCriticalItemsFilter diarioCriticalItemsFilter;
    private final MensalCriticalItemsFilter mensalCriticalItemsFilter;

    public void notificarPrevisaoDiaria(List<InventarioDiarioDTO> inventarios) {
        inventarios.forEach(this::processarNotificacaoDiaria);
    }

    public void notificarPrevisaoMensal(List<InventarioMensalDTO> inventarios) {
        inventarios.forEach(this::processarNotificacaoMensal);
    }

    public void notificarRiscoValidade(List<AlertaValidadeDTO> alertas) {
        alertas.forEach(this::processarNotificacaoValidade);
    }

    private void processarNotificacaoDiaria(InventarioDiarioDTO inventario) {
        String emailResponsavel = inventario.getPontoDispensacao().getEmailResponsavel();

        if (!isEmailValido(emailResponsavel, inventario.getPontoDispensacao().getNome())) {
            return;
        }

        List<InsumoDiarioDTO> itensCriticos = diarioCriticalItemsFilter.filterCriticalItems(inventario.getInsumos());

        if (itensCriticos.isEmpty()) {
            return;
        }

        String nomePonto = inventario.getPontoDispensacao().getNome();
        String corpoEmail = diarioEmailTemplateBuilder.build(nomePonto, itensCriticos);

        emailService.sendEmail(emailResponsavel, EmailSubjectFactory.createDailyAlertSubject(), corpoEmail);
    }

    private void processarNotificacaoMensal(InventarioMensalDTO inventario) {
        String emailResponsavel = inventario.getPontoDispensacao().getEmailResponsavel();

        if (emailResponsavel == null) {
            return;
        }

        List<InsumoMensalDTO> itensCriticos = mensalCriticalItemsFilter.filterCriticalItems(inventario.getInsumos());

        if (itensCriticos.isEmpty()) {
            return;
        }

        String nomePonto = inventario.getPontoDispensacao().getNome();
        String corpoEmail = mensalEmailTemplateBuilder.build(nomePonto, itensCriticos);

        emailService.sendEmail(emailResponsavel, EmailSubjectFactory.createMonthlyAlertSubject(), corpoEmail);
    }

    private void processarNotificacaoValidade(AlertaValidadeDTO alerta) {
        String emailResponsavel = alerta.getPontoDispensacao().getEmailResponsavel();

        if (emailResponsavel == null) {
            return;
        }

        String nomePonto = alerta.getPontoDispensacao().getNome();
        String corpoEmail = validadeEmailTemplateBuilder.build(nomePonto, alerta.getItensEmRisco());

        emailService.sendEmail(emailResponsavel, EmailSubjectFactory.createValidityAlertSubject(), corpoEmail);
    }

    private boolean isEmailValido(String email, String nomePonto) {
        if (email == null) {
            log.warn("Ponto de Dispensação {} sem e-mail de responsável cadastrado.", nomePonto);
            return false;
        }
        return true;
    }
}
