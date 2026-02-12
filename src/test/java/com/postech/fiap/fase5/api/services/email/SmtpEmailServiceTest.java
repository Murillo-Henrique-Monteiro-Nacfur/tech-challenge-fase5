package com.postech.fiap.fase5.api.services.email;

import com.postech.fiap.fase5.infrastructure.exceptions.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmtpEmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessageBuilder messageBuilder;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private SmtpEmailService smtpEmailService;

    private static final String SENDER = "sender@example.com";
    private static final String RECIPIENT = "recipient@example.com";
    private static final String SUBJECT = "Test Subject";
    private static final String BODY = "Test Body Content";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(smtpEmailService, "sender", SENDER);
    }

    @Test
    void sendEmailDeveEnviarEmailComSucesso() throws MessagingException {
        when(messageBuilder.build(SENDER, RECIPIENT, SUBJECT, BODY)).thenReturn(mimeMessage);

        smtpEmailService.sendEmail(RECIPIENT, SUBJECT, BODY);

        verify(messageBuilder).build(SENDER, RECIPIENT, SUBJECT, BODY);
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendEmailDeveLancarExcecaoQuandoMessageBuilderFalhar() throws MessagingException {
        MessagingException messagingException = new MessagingException("Failed to create message");
        when(messageBuilder.build(SENDER, RECIPIENT, SUBJECT, BODY)).thenThrow(messagingException);

        assertThatThrownBy(() -> smtpEmailService.sendEmail(RECIPIENT, SUBJECT, BODY))
                .isInstanceOf(EmailSendingException.class)
                .hasMessageContaining("Failed to send email to: " + RECIPIENT)
                .hasCause(messagingException);

        verify(messageBuilder).build(SENDER, RECIPIENT, SUBJECT, BODY);
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendEmailDeveLancarExcecaoQuandoMailSenderFalhar() throws MessagingException {
        when(messageBuilder.build(SENDER, RECIPIENT, SUBJECT, BODY)).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(mimeMessage);

        assertThatThrownBy(() -> smtpEmailService.sendEmail(RECIPIENT, SUBJECT, BODY))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Mail server error");

        verify(messageBuilder).build(SENDER, RECIPIENT, SUBJECT, BODY);
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendEmailDeveUtilizarParametrosCorretos() throws MessagingException {
        String customRecipient = "custom@example.com";
        String customSubject = "Custom Subject";
        String customBody = "Custom Body";

        when(messageBuilder.build(SENDER, customRecipient, customSubject, customBody)).thenReturn(mimeMessage);

        smtpEmailService.sendEmail(customRecipient, customSubject, customBody);

        verify(messageBuilder).build(SENDER, customRecipient, customSubject, customBody);
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendEmailDeveUtilizarRemetenteConfigurado() throws MessagingException {
        String configuredSender = "configured@example.com";
        ReflectionTestUtils.setField(smtpEmailService, "sender", configuredSender);

        when(messageBuilder.build(configuredSender, RECIPIENT, SUBJECT, BODY)).thenReturn(mimeMessage);

        smtpEmailService.sendEmail(RECIPIENT, SUBJECT, BODY);

        verify(messageBuilder).build(configuredSender, RECIPIENT, SUBJECT, BODY);
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendEmailDeveProcessarMultiplosDestinatarios() throws MessagingException {
        String recipient1 = "user1@example.com";
        String recipient2 = "user2@example.com";

        when(messageBuilder.build(eq(SENDER), anyString(), eq(SUBJECT), eq(BODY))).thenReturn(mimeMessage);

        smtpEmailService.sendEmail(recipient1, SUBJECT, BODY);
        smtpEmailService.sendEmail(recipient2, SUBJECT, BODY);

        verify(messageBuilder).build(SENDER, recipient1, SUBJECT, BODY);
        verify(messageBuilder).build(SENDER, recipient2, SUBJECT, BODY);
        verify(mailSender, times(2)).send(mimeMessage);
    }

    @Test
    void sendEmailDeveManterExcecaoOriginal() throws MessagingException {
        MessagingException originalException = new MessagingException("Original error message");
        when(messageBuilder.build(SENDER, RECIPIENT, SUBJECT, BODY)).thenThrow(originalException);

        assertThatThrownBy(() -> smtpEmailService.sendEmail(RECIPIENT, SUBJECT, BODY))
                .isInstanceOf(EmailSendingException.class)
                .extracting(Throwable::getCause)
                .isSameAs(originalException);

        verify(messageBuilder).build(SENDER, RECIPIENT, SUBJECT, BODY);
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}

