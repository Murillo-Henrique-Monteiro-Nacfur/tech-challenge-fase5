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

        sb.append("""
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4;">
                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="max-width: 700px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="background: linear-gradient(135deg, #d32f2f 0%, #b71c1c 100%); padding: 30px 40px; border-radius: 8px 8px 0 0;">
                            <table width="100%">
                                <tr>
                                    <td>
                                        <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight: 600;">
                                            ⚠️ Alerta de Desabastecimento
                                        </h1>
                                        <p style="color: rgba(255,255,255,0.9); margin: 8px 0 0 0; font-size: 14px;">
                                            Sistema de Gestão de Insumos
                                        </p>
                                    </td>
                                    <td style="text-align: right;">
                                        <span style="color: rgba(255,255,255,0.8); font-size: 13px;">
            """);
        sb.append("                            ").append(LocalDate.now()).append("\n");
        sb.append("""
                                        </span>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px;">
                            <p style="color: #333333; font-size: 16px; line-height: 1.6; margin: 0 0 20px 0;">
                                Prezado(a) responsável pelo <strong style="color: #1976d2;">
            """);
        sb.append(nomePonto);
        sb.append("""
                </strong>,
                            </p>
                            
                            <p style="color: #555555; font-size: 15px; line-height: 1.6; margin: 0 0 30px 0;">
                                Identificamos <strong>riscos de desabastecimento</strong> com base na análise do consumo dos últimos <strong>30 dias</strong>. 
                                Por favor, revise os itens abaixo e tome as medidas necessárias.
                            </p>
                            
                            <!-- Items Table -->
                            <table width="100%" cellspacing="0" cellpadding="0" style="margin-bottom: 30px;">
            """);

        for (InsumoDiarioDTO item : itens) {
            String statusColor = "CRITICO".equals(item.getStatusPrevisao()) ? "#d32f2f" : "#ff9800";
            String statusBgColor = "CRITICO".equals(item.getStatusPrevisao()) ? "#ffebee" : "#fff3e0";
            String statusIcon = "CRITICO".equals(item.getStatusPrevisao()) ? "🔴" : "🟡";

            sb.append("""
                                <tr>
                                    <td style="padding: 20px; background-color: #fafafa; border-left: 4px solid 
            """);
            sb.append(statusColor);
            sb.append("""
                ; border-radius: 4px; margin-bottom: 15px;">
                                        <table width="100%">
                                            <tr>
                                                <td colspan="2" style="padding-bottom: 15px; border-bottom: 1px solid #e0e0e0;">
                                                    <h3 style="color: #333333; margin: 0; font-size: 18px; font-weight: 600;">
            """);
            sb.append("                                            ").append(item.getNomeInsumo()).append("\n");
            sb.append("""
                                                    </h3>
                                                    <span style="display: inline-block; margin-top: 8px; padding: 4px 12px; background-color: 
            """);
            sb.append(statusBgColor);
            sb.append("; color: ");
            sb.append(statusColor);
            sb.append("""
                ; border-radius: 20px; font-size: 12px; font-weight: 600;">
            """);
            sb.append("                                        ").append(statusIcon).append(" ").append(item.getStatusPrevisao()).append("\n");
            sb.append("""
                                                    </span>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td width="50%" style="padding: 15px 0;">
                                                    <p style="color: #757575; font-size: 12px; margin: 0 0 4px 0; text-transform: uppercase;">Estoque Atual</p>
                                                    <p style="color: #333333; font-size: 20px; font-weight: 600; margin: 0;">
            """);
            sb.append("                                        ").append(item.getQuantidade()).append(" <span style=\"font-size: 14px; color: #757575;\">unidades</span>\n");
            sb.append("""
                                                    </p>
                                                </td>
                                                <td width="50%" style="padding: 15px 0;">
                                                    <p style="color: #757575; font-size: 12px; margin: 0 0 4px 0; text-transform: uppercase;">Previsão de Término</p>
                                                    <p style="color: 
            """);
            sb.append(statusColor);
            sb.append("""
                ; font-size: 20px; font-weight: 600; margin: 0;">
            """);
            sb.append("                                        ").append(item.getPrevisaoEsgotamentoDias()).append(" <span style=\"font-size: 14px;\">dias</span>\n");
            sb.append("""
                                                    </p>
                                                </td>
                                            </tr>
            """);

            if (item.getSugestoesTransferencia() != null && !item.getSugestoesTransferencia().isEmpty()) {
                sb.append("""
                                            <tr>
                                                <td colspan="2" style="padding-top: 15px; border-top: 1px solid #e0e0e0;">
                                                    <p style="color: #388e3c; font-size: 13px; font-weight: 600; margin: 0 0 10px 0;">
                                                        💡 Sugestões de Transferência
                                                    </p>
                                                    <table width="100%" cellspacing="0" cellpadding="0">
                """);
                for (SugestaoTransferenciaDTO sugestao : item.getSugestoesTransferencia()) {
                    sb.append("""
                                                        <tr>
                                                            <td style="padding: 8px 12px; background-color: #e8f5e9; border-radius: 4px; margin-bottom: 5px;">
                                                                <span style="color: #2e7d32; font-size: 14px;">
                                                                    📍 
                    """);
                    sb.append(sugestao.getNomePontoDoador());
                    sb.append("""
                                                                </span>
                                                                <span style="float: right; color: #388e3c; font-weight: 600; font-size: 14px;">
                    """);
                    sb.append("                                            Disponível: ").append(sugestao.getQuantidadeDisponivelNoDoador()).append(" un.\n");
                    sb.append("""
                                                                </span>
                                                            </td>
                                                        </tr>
                                                        <tr><td style="height: 5px;"></td></tr>
                    """);
                }
                sb.append("""
                                                    </table>
                                                </td>
                                            </tr>
                """);
            } else {
                sb.append("""
                                            <tr>
                                                <td colspan="2" style="padding-top: 15px; border-top: 1px solid #e0e0e0;">
                                                    <p style="color: #9e9e9e; font-size: 13px; font-style: italic; margin: 0;">
                                                        ℹ️ Nenhum ponto com excedente identificado para transferência.
                                                    </p>
                                                </td>
                                            </tr>
                """);
            }

            sb.append("""
                                        </table>
                                    </td>
                                </tr>
                                <tr><td style="height: 15px;"></td></tr>
            """);
        }

        sb.append("""
                            </table>
                            
                            <!-- Call to Action -->
                            <table width="100%" cellspacing="0" cellpadding="0" style="background-color: #e3f2fd; border-radius: 8px; padding: 20px;">
                                <tr>
                                    <td style="padding: 20px;">
                                        <p style="color: #1565c0; font-size: 14px; margin: 0; line-height: 1.6;">
                                            <strong>📋 Ação Requerida:</strong> Por favor, tome as medidas necessárias para evitar o desabastecimento dos itens listados acima.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    
                    <!-- Footer -->
                    <tr>
                        <td style="background-color: #37474f; padding: 25px 40px; border-radius: 0 0 8px 8px;">
                            <p style="color: rgba(255,255,255,0.7); font-size: 12px; margin: 0; text-align: center;">
                                Este é um e-mail automático do Sistema de Gestão de Insumos.<br>
                                Por favor, não responda diretamente a esta mensagem.
                            </p>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """);

        return sb.toString();
    }

    private String montarCorpoEmailMensal(String nomePonto, List<InsumoMensalDTO> itens) {
        StringBuilder sb = new StringBuilder();

        sb.append("""
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4;">
                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="max-width: 700px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="background: linear-gradient(135deg, #7b1fa2 0%, #4a148c 100%); padding: 30px 40px; border-radius: 8px 8px 0 0;">
                            <table width="100%">
                                <tr>
                                    <td>
                                        <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight: 600;">
                                            📊 Previsão Sazonal de Estoque
                                        </h1>
                                        <p style="color: rgba(255,255,255,0.9); margin: 8px 0 0 0; font-size: 14px;">
                                            Análise Mensal - Sistema de Gestão de Insumos
                                        </p>
                                    </td>
                                    <td style="text-align: right;">
                                        <span style="color: rgba(255,255,255,0.8); font-size: 13px;">
            """);
        sb.append("                            ").append(LocalDate.now()).append("\n");
        sb.append("""
                                        </span>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px;">
                            <p style="color: #333333; font-size: 16px; line-height: 1.6; margin: 0 0 20px 0;">
                                Prezado(a) responsável pelo <strong style="color: #7b1fa2;">
            """);
        sb.append(nomePonto);
        sb.append("""
                </strong>,
                            </p>
                            
                            <p style="color: #555555; font-size: 15px; line-height: 1.6; margin: 0 0 30px 0;">
                                Identificamos <strong>riscos de desabastecimento para o próximo mês</strong> com base na análise do <strong>histórico de anos anteriores</strong>. 
                                Esta previsão considera padrões sazonais de consumo. Recomendamos atenção especial aos itens listados.
                            </p>
                            
                            <!-- Items Table -->
                            <table width="100%" cellspacing="0" cellpadding="0" style="margin-bottom: 30px;">
            """);

        for (InsumoMensalDTO item : itens) {
            String statusColor = "CRITICO".equals(item.getStatusPrevisaoSazonal()) ? "#d32f2f" : "#ff9800";
            String statusBgColor = "CRITICO".equals(item.getStatusPrevisaoSazonal()) ? "#ffebee" : "#fff3e0";
            String statusIcon = "CRITICO".equals(item.getStatusPrevisaoSazonal()) ? "🔴" : "🟡";

            sb.append("""
                                <tr>
                                    <td style="padding: 20px; background-color: #fafafa; border-left: 4px solid 
            """);
            sb.append(statusColor);
            sb.append("""
                ; border-radius: 4px; margin-bottom: 15px;">
                                        <table width="100%">
                                            <tr>
                                                <td colspan="2" style="padding-bottom: 15px; border-bottom: 1px solid #e0e0e0;">
                                                    <h3 style="color: #333333; margin: 0; font-size: 18px; font-weight: 600;">
            """);
            sb.append("                                            ").append(item.getNomeInsumo()).append("\n");
            sb.append("""
                                                    </h3>
                                                    <span style="display: inline-block; margin-top: 8px; padding: 4px 12px; background-color: 
            """);
            sb.append(statusBgColor);
            sb.append("; color: ");
            sb.append(statusColor);
            sb.append("""
                ; border-radius: 20px; font-size: 12px; font-weight: 600;">
            """);
            sb.append("                                        ").append(statusIcon).append(" ").append(item.getStatusPrevisaoSazonal()).append("\n");
            sb.append("""
                                                    </span>
                                                    <span style="display: inline-block; margin-top: 8px; margin-left: 8px; padding: 4px 12px; background-color: #e8eaf6; color: #3949ab; border-radius: 20px; font-size: 12px;">
                                                        📅 Base Histórica
                                                    </span>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td width="50%" style="padding: 15px 0;">
                                                    <p style="color: #757575; font-size: 12px; margin: 0 0 4px 0; text-transform: uppercase;">Estoque Atual</p>
                                                    <p style="color: #333333; font-size: 20px; font-weight: 600; margin: 0;">
            """);
            sb.append("                                        ").append(item.getQuantidade()).append(" <span style=\"font-size: 14px; color: #757575;\">unidades</span>\n");
            sb.append("""
                                                    </p>
                                                </td>
                                                <td width="50%" style="padding: 15px 0;">
                                                    <p style="color: #757575; font-size: 12px; margin: 0 0 4px 0; text-transform: uppercase;">Previsão de Término (Sazonal)</p>
                                                    <p style="color: 
            """);
            sb.append(statusColor);
            sb.append("""
                ; font-size: 20px; font-weight: 600; margin: 0;">
            """);
            sb.append("                                        ").append(item.getPrevisaoEsgotamentoDiasSazonal()).append(" <span style=\"font-size: 14px;\">dias</span>\n");
            sb.append("""
                                                    </p>
                                                </td>
                                            </tr>
            """);

            if (item.getSugestoesTransferenciaSazonal() != null && !item.getSugestoesTransferenciaSazonal().isEmpty()) {
                sb.append("""
                                            <tr>
                                                <td colspan="2" style="padding-top: 15px; border-top: 1px solid #e0e0e0;">
                                                    <p style="color: #388e3c; font-size: 13px; font-weight: 600; margin: 0 0 10px 0;">
                                                        💡 Sugestões de Transferência (Projeção)
                                                    </p>
                                                    <table width="100%" cellspacing="0" cellpadding="0">
                """);
                for (SugestaoTransferenciaDTO sugestao : item.getSugestoesTransferenciaSazonal()) {
                    sb.append("""
                                                        <tr>
                                                            <td style="padding: 8px 12px; background-color: #e8f5e9; border-radius: 4px; margin-bottom: 5px;">
                                                                <span style="color: #2e7d32; font-size: 14px;">
                                                                    📍 
                    """);
                    sb.append(sugestao.getNomePontoDoador());
                    sb.append("""
                                                                </span>
                                                                <span style="float: right; color: #388e3c; font-weight: 600; font-size: 14px;">
                    """);
                    sb.append("                                            Disponível: ").append(sugestao.getQuantidadeDisponivelNoDoador()).append(" un.\n");
                    sb.append("""
                                                                </span>
                                                            </td>
                                                        </tr>
                                                        <tr><td style="height: 5px;"></td></tr>
                    """);
                }
                sb.append("""
                                                    </table>
                                                </td>
                                            </tr>
                """);
            } else {
                sb.append("""
                                            <tr>
                                                <td colspan="2" style="padding-top: 15px; border-top: 1px solid #e0e0e0;">
                                                    <p style="color: #9e9e9e; font-size: 13px; font-style: italic; margin: 0;">
                                                        ℹ️ Nenhum ponto com excedente projetado identificado para transferência.
                                                    </p>
                                                </td>
                                            </tr>
                """);
            }

            sb.append("""
                                        </table>
                                    </td>
                                </tr>
                                <tr><td style="height: 15px;"></td></tr>
            """);
        }

        sb.append("""
                            </table>
                            
                            <!-- Call to Action -->
                            <table width="100%" cellspacing="0" cellpadding="0" style="background-color: #f3e5f5; border-radius: 8px; padding: 20px;">
                                <tr>
                                    <td style="padding: 20px;">
                                        <p style="color: #7b1fa2; font-size: 14px; margin: 0; line-height: 1.6;">
                                            <strong>📋 Planejamento Recomendado:</strong> Utilize esta previsão sazonal para antecipar necessidades de reposição e evitar o desabastecimento no próximo mês.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    
                    <!-- Footer -->
                    <tr>
                        <td style="background-color: #37474f; padding: 25px 40px; border-radius: 0 0 8px 8px;">
                            <p style="color: rgba(255,255,255,0.7); font-size: 12px; margin: 0; text-align: center;">
                                Este é um e-mail automático do Sistema de Gestão de Insumos.<br>
                                Por favor, não responda diretamente a esta mensagem.
                            </p>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """);

        return sb.toString();
    }
}
