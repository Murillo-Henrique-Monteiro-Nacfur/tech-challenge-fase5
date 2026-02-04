package com.postech.fiap.fase5.api.services;

import com.postech.fiap.fase5.api.dto.estimativa.*;
import com.postech.fiap.fase5.api.services.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final EmailService emailService;

    public void notificarPrevisaoDiaria(List<InventarioDiarioDTO> inventarios) {
        for (InventarioDiarioDTO inventario : inventarios) {
            if (inventario.getPontoDispensacao().getEmailResponsavel() == null) {
                log.warn("Ponto de Dispensação {} sem e-mail de responsável cadastrado.", inventario.getPontoDispensacao().getNome());
                continue;
            }

            List<InsumoDiarioDTO> itensCriticos = inventario.getInsumos().stream()
                    .filter(i -> "CRITICO".equals(i.getStatusPrevisao()) || "ALERTA".equals(i.getStatusPrevisao()))
                    .toList();

            if (!itensCriticos.isEmpty()) {
                String corpoEmail = montarCorpoEmailDiario(inventario.getPontoDispensacao().getNome(), itensCriticos);
                emailService.sendEmail(
                        inventario.getPontoDispensacao().getEmailResponsavel(),
                        "[ALERTA DIÁRIO] Risco de Desabastecimento - " + LocalDate.now(),
                        corpoEmail
                );
            }
        }
    }

    public void notificarPrevisaoMensal(List<InventarioMensalDTO> inventarios) {
        for (InventarioMensalDTO inventario : inventarios) {
            if (inventario.getPontoDispensacao().getEmailResponsavel() == null) continue;

            List<InsumoMensalDTO> itensCriticos = inventario.getInsumos().stream()
                    .filter(i -> "CRITICO".equals(i.getStatusPrevisaoSazonal()) || "ALERTA".equals(i.getStatusPrevisaoSazonal()))
                    .toList();

            if (!itensCriticos.isEmpty()) {
                String corpoEmail = montarCorpoEmailMensal(inventario.getPontoDispensacao().getNome(), itensCriticos);
                emailService.sendEmail(
                        inventario.getPontoDispensacao().getEmailResponsavel(),
                        "[ALERTA MENSAL] Previsão Sazonal de Estoque - " + LocalDate.now(),
                        corpoEmail
                );
            }
        }
    }

    private String montarCorpoEmailDiario(String nomePonto, List<InsumoDiarioDTO> itens) {
        StringBuilder sb = new StringBuilder();
        sb.append("Olá, responsável pelo ").append(nomePonto).append(".\n\n");
        sb.append("Identificamos riscos de desabastecimento baseados no consumo dos últimos 30 dias:\n\n");

        for (InsumoDiarioDTO item : itens) {
            sb.append("--------------------------------------------------\n");
            sb.append("INSUMO: ").append(item.getNomeInsumo()).append("\n");
            sb.append("STATUS: ").append(item.getStatusPrevisao()).append("\n");
            sb.append("ESTOQUE ATUAL: ").append(item.getQuantidade()).append("\n");
            sb.append("PREVISÃO DE TÉRMINO: ").append(item.getPrevisaoEsgotamentoDias()).append(" dias\n");
            
            if (item.getSugestoesTransferencia() != null && !item.getSugestoesTransferencia().isEmpty()) {
                sb.append("\nSUGESTÃO DE TRANSFERÊNCIA (Pontos com sobra):\n");
                for (SugestaoTransferenciaDTO sugestao : item.getSugestoesTransferencia()) {
                    sb.append(" - ").append(sugestao.getNomePontoDoador())
                      .append(" (Disp: ").append(sugestao.getQuantidadeDisponivelNoDoador()).append(")\n");
                }
            } else {
                sb.append("\nNENHUM PONTO COM SOBRA IDENTIFICADO PARA TRANSFERÊNCIA.\n");
            }
            sb.append("\n");
        }
        
        sb.append("--------------------------------------------------\n");
        sb.append("Por favor, tome as medidas necessárias.\n");
        return sb.toString();
    }

    private String montarCorpoEmailMensal(String nomePonto, List<InsumoMensalDTO> itens) {
        StringBuilder sb = new StringBuilder();
        sb.append("Olá, responsável pelo ").append(nomePonto).append(".\n\n");
        sb.append("Identificamos riscos de desabastecimento para o PRÓXIMO MÊS baseados no histórico de anos anteriores:\n\n");

        for (InsumoMensalDTO item : itens) {
            sb.append("--------------------------------------------------\n");
            sb.append("INSUMO: ").append(item.getNomeInsumo()).append("\n");
            sb.append("STATUS SAZONAL: ").append(item.getStatusPrevisaoSazonal()).append("\n");
            sb.append("ESTOQUE ATUAL: ").append(item.getQuantidade()).append("\n");
            sb.append("PREVISÃO DE TÉRMINO (Base Histórica): ").append(item.getPrevisaoEsgotamentoDiasSazonal()).append(" dias\n");
            
            if (item.getSugestoesTransferenciaSazonal() != null && !item.getSugestoesTransferenciaSazonal().isEmpty()) {
                sb.append("\nSUGESTÃO DE TRANSFERÊNCIA (Pontos com sobra projetada):\n");
                for (SugestaoTransferenciaDTO sugestao : item.getSugestoesTransferenciaSazonal()) {
                    sb.append(" - ").append(sugestao.getNomePontoDoador())
                      .append(" (Disp: ").append(sugestao.getQuantidadeDisponivelNoDoador()).append(")\n");
                }
            } else {
                sb.append("\nNENHUM PONTO COM SOBRA IDENTIFICADO.\n");
            }
            sb.append("\n");
        }
        
        sb.append("--------------------------------------------------\n");
        return sb.toString();
    }
}
