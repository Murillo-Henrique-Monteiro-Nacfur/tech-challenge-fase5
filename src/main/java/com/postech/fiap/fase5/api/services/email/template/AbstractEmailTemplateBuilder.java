package com.postech.fiap.fase5.api.services.email.template;

import java.time.LocalDate;

public abstract class AbstractEmailTemplateBuilder {

    protected static final String CRITICO = "CRITICO";
    protected static final String ALERTA = "ALERTA";

    protected static final String COLOR_CRITICO = "#d32f2f";
    protected static final String COLOR_ALERTA = "#ff9800";
    protected static final String COLOR_SUCCESS = "#388e3c";

    protected static final String BG_COLOR_CRITICO = "#ffebee";
    protected static final String BG_COLOR_ALERTA = "#fff3e0";

    protected static final String ICON_CRITICO = "🔴";
    protected static final String ICON_ALERTA = "🟡";

    protected String determineStatusColor(String status) {
        return CRITICO.equals(status) ? COLOR_CRITICO : COLOR_ALERTA;
    }

    protected String determineStatusBackgroundColor(String status) {
        return CRITICO.equals(status) ? BG_COLOR_CRITICO : BG_COLOR_ALERTA;
    }

    protected String determineStatusIcon(String status) {
        return CRITICO.equals(status) ? ICON_CRITICO : ICON_ALERTA;
    }

    protected String buildDocumentStart() {
        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4;">
                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="max-width: 700px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
            """;
    }

    protected String buildHeader(String gradient, String title, String subtitle) {
        return String.format("""
                    <tr>
                        <td style="background: linear-gradient(135deg, %s); padding: 30px 40px; border-radius: 8px 8px 0 0;">
                            <table width="100%%">
                                <tr>
                                    <td>
                                        <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight: 600;">
                                            %s
                                        </h1>
                                        <p style="color: rgba(255,255,255,0.9); margin: 8px 0 0 0; font-size: 14px;">
                                            %s
                                        </p>
                                    </td>
                                    <td style="text-align: right;">
                                        <span style="color: rgba(255,255,255,0.8); font-size: 13px;">
                                            %s
                                        </span>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
            """, gradient, title, subtitle, LocalDate.now());
    }

    protected String buildGreeting(String nomePonto, String highlightColor) {
        return String.format("""
                    <tr>
                        <td style="padding: 40px;">
                            <p style="color: #333333; font-size: 16px; line-height: 1.6; margin: 0 0 20px 0;">
                                Prezado(a) responsável pelo <strong style="color: %s;">%s</strong>,
                            </p>
            """, highlightColor, nomePonto);
    }

    protected String buildDescription(String description) {
        return String.format("""
                            <p style="color: #555555; font-size: 15px; line-height: 1.6; margin: 0 0 30px 0;">
                                %s
                            </p>
                            
                            <table width="100%%" cellspacing="0" cellpadding="0" style="margin-bottom: 30px;">
            """, description);
    }

    protected String buildItemStart(String statusColor) {
        return String.format("""
                                <tr>
                                    <td style="padding: 20px; background-color: #fafafa; border-left: 4px solid %s; border-radius: 4px; margin-bottom: 15px;">
                                        <table width="100%%">
            """, statusColor);
    }

    protected String buildItemHeader(String itemName, String statusBgColor, String statusColor, String statusIcon, String statusLabel) {
        return String.format("""
                                            <tr>
                                                <td colspan="2" style="padding-bottom: 15px; border-bottom: 1px solid #e0e0e0;">
                                                    <h3 style="color: #333333; margin: 0; font-size: 18px; font-weight: 600;">
                                                        %s
                                                    </h3>
                                                    <span style="display: inline-block; margin-top: 8px; padding: 4px 12px; background-color: %s; color: %s; border-radius: 20px; font-size: 12px; font-weight: 600;">
                                                        %s %s
                                                    </span>
            """, itemName, statusBgColor, statusColor, statusIcon, statusLabel);
    }

    protected String buildItemHeaderClose() {
        return """
                                                </td>
                                            </tr>
            """;
    }

    protected String buildDataRow(String label1, String value1, String unit1, String valueColor1,
                                  String label2, String value2, String unit2, String valueColor2) {
        return String.format("""
                                            <tr>
                                                <td width="50%%" style="padding: 15px 0;">
                                                    <p style="color: #757575; font-size: 12px; margin: 0 0 4px 0; text-transform: uppercase;">%s</p>
                                                    <p style="color: %s; font-size: 20px; font-weight: 600; margin: 0;">
                                                        %s <span style="font-size: 14px; color: #757575;">%s</span>
                                                    </p>
                                                </td>
                                                <td width="50%%" style="padding: 15px 0;">
                                                    <p style="color: #757575; font-size: 12px; margin: 0 0 4px 0; text-transform: uppercase;">%s</p>
                                                    <p style="color: %s; font-size: 20px; font-weight: 600; margin: 0;">
                                                        %s <span style="font-size: 14px;">%s</span>
                                                    </p>
                                                </td>
                                            </tr>
            """, label1, valueColor1, value1, unit1, label2, valueColor2, value2, unit2);
    }

    protected String buildTransferSuggestionsHeader(String title) {
        return String.format("""
                                            <tr>
                                                <td colspan="2" style="padding-top: 15px; border-top: 1px solid #e0e0e0;">
                                                    <p style="color: %s; font-size: 13px; font-weight: 600; margin: 0 0 10px 0;">
                                                        💡 %s
                                                    </p>
                                                    <table width="100%%" cellspacing="0" cellpadding="0">
            """, COLOR_SUCCESS, title);
    }

    protected String buildTransferSuggestionRow(String nomePontoDoador, Integer quantidadeDisponivel) {
        return String.format("""
                                                        <tr>
                                                            <td style="padding: 8px 12px; background-color: #e8f5e9; border-radius: 4px; margin-bottom: 5px;">
                                                                <span style="color: #2e7d32; font-size: 14px;">
                                                                    📍 %s
                                                                </span>
                                                                <span style="float: right; color: %s; font-weight: 600; font-size: 14px;">
                                                                    Disponível: %d un.
                                                                </span>
                                                            </td>
                                                        </tr>
                                                        <tr><td style="height: 5px;"></td></tr>
            """, nomePontoDoador, COLOR_SUCCESS, quantidadeDisponivel);
    }

    protected String buildTransferSuggestionsFooter() {
        return """
                                                    </table>
                                                </td>
                                            </tr>
            """;
    }

    protected String buildNoTransferSuggestions(String message) {
        return String.format("""
                                            <tr>
                                                <td colspan="2" style="padding-top: 15px; border-top: 1px solid #e0e0e0;">
                                                    <p style="color: #9e9e9e; font-size: 13px; font-style: italic; margin: 0;">
                                                        ℹ️ %s
                                                    </p>
                                                </td>
                                            </tr>
            """, message);
    }

    protected String buildItemEnd() {
        return """
                                        </table>
                                    </td>
                                </tr>
                                <tr><td style="height: 15px;"></td></tr>
            """;
    }

    protected String buildItemsTableEnd() {
        return """
                            </table>
            """;
    }

    protected String buildCallToAction(String backgroundColor, String textColor, String message) {
        return String.format("""
                            <table width="100%%" cellspacing="0" cellpadding="0" style="background-color: %s; border-radius: 8px; padding: 20px;">
                                <tr>
                                    <td style="padding: 20px;">
                                        <p style="color: %s; font-size: 14px; margin: 0; line-height: 1.6;">
                                            %s
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
            """, backgroundColor, textColor, message);
    }

    protected String buildFooter() {
        return """
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
            """;
    }
}

