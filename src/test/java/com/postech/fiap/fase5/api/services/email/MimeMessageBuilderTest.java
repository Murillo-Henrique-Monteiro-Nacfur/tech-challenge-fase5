package com.postech.fiap.fase5.api.services.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MimeMessageBuilderTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private MimeMessageBuilder mimeMessageBuilder;

    @Test
    void deveConstruirMimeMessageComSucesso() throws MessagingException {
        String from = "remetente@email.com";
        String to = "destinatario@email.com";
        String subject = "Assunto do Email";
        String body = "<html><body>Corpo do email em HTML</body></html>";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        MimeMessage resultado = mimeMessageBuilder.build(from, to, subject, body);

        assertNotNull(resultado);
        assertEquals(mimeMessage, resultado);
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void deveConstruirMimeMessageComTextoSimples() throws MessagingException {
        String from = "sender@test.com";
        String to = "receiver@test.com";
        String subject = "Test Subject";
        String body = "Simple text body";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        MimeMessage resultado = mimeMessageBuilder.build(from, to, subject, body);

        assertNotNull(resultado);
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void deveConstruirMimeMessageComMultiplosDestinatarios() throws MessagingException {
        String from = "system@company.com";
        String to = "user1@test.com";
        String subject = "Notification";
        String body = "<html><body><h1>Important Message</h1></body></html>";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        MimeMessage resultado = mimeMessageBuilder.build(from, to, subject, body);

        assertNotNull(resultado);
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void deveConstruirMimeMessageComAssuntoLongo() throws MessagingException {
        String from = "noreply@system.com";
        String to = "admin@company.com";
        String subject = "Este é um assunto muito longo para testar o comportamento do sistema com textos extensos no campo de assunto do email";
        String body = "Body content";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        MimeMessage resultado = mimeMessageBuilder.build(from, to, subject, body);

        assertNotNull(resultado);
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void deveConstruirMimeMessageComCorpoVazio() throws MessagingException {
        String from = "sender@email.com";
        String to = "recipient@email.com";
        String subject = "Empty Body Test";
        String body = "";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        MimeMessage resultado = mimeMessageBuilder.build(from, to, subject, body);

        assertNotNull(resultado);
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void deveConstruirMimeMessageComCaracteresEspeciais() throws MessagingException {
        String from = "sistema@saude.gov.br";
        String to = "usuario@saude.gov.br";
        String subject = "Notificação de Estoque - Atenção!";
        String body = "<html><body><p>Olá, José!</p><p>Açúcar em estoque: 500kg</p></body></html>";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        MimeMessage resultado = mimeMessageBuilder.build(from, to, subject, body);

        assertNotNull(resultado);
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void deveConstruirMimeMessageComHTMLComplexo() throws MessagingException {
        String from = "notifications@platform.com";
        String to = "user@domain.com";
        String subject = "Weekly Report";
        String body = """
                <html>
                    <head><style>body { font-family: Arial; }</style></head>
                    <body>
                        <h1>Report</h1>
                        <table>
                            <tr><td>Item</td><td>Value</td></tr>
                            <tr><td>Total</td><td>100</td></tr>
                        </table>
                    </body>
                </html>
                """;

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        MimeMessage resultado = mimeMessageBuilder.build(from, to, subject, body);

        assertNotNull(resultado);
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void deveConstruirMimeMessageComEmailsComplexos() throws MessagingException {
        String from = "no-reply+system@company.co.uk";
        String to = "user.name+tag@domain.com.br";
        String subject = "Test Complex Emails";
        String body = "Content";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        MimeMessage resultado = mimeMessageBuilder.build(from, to, subject, body);

        assertNotNull(resultado);
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void deveConstruirMimeMessageComDadosMinimos() throws MessagingException {
        String from = "a@b.c";
        String to = "x@y.z";
        String subject = "S";
        String body = "B";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        MimeMessage resultado = mimeMessageBuilder.build(from, to, subject, body);

        assertNotNull(resultado);
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void deveConstruirMimeMessageComConteudoEmPortugues() throws MessagingException {
        String from = "farmacia@saude.gov.br";
        String to = "gestor@unidade.gov.br";
        String subject = "Relatório de Dispensação de Medicamentos";
        String body = """
                <html>
                    <body>
                        <h2>Prezado Gestor,</h2>
                        <p>Segue o relatório mensal de dispensação:</p>
                        <ul>
                            <li>Paracetamol: 150 unidades</li>
                            <li>Ibuprofeno: 200 unidades</li>
                            <li>Dipirona: 180 unidades</li>
                        </ul>
                        <p>Atenciosamente,<br/>Sistema de Gestão</p>
                    </body>
                </html>
                """;

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        MimeMessage resultado = mimeMessageBuilder.build(from, to, subject, body);

        assertNotNull(resultado);
        verify(mailSender, times(1)).createMimeMessage();
    }
}

