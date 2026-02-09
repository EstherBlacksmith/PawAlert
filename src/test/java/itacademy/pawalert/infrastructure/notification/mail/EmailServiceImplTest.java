package itacademy.pawalert.infrastructure.notification.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring5.SpringTemplateEngine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void sendEmail_should_call_mailSender_send() throws MessagingException {
        // Arrange

        String to = "test@example.com";
        String subject = "Test Subject";
        String htmlBody = "<html><body><h1>Test email content</h1></body></html>";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any())).thenReturn("<html><body>Test</body></html>");

        // Act
        emailService.sendToUser(to, subject, htmlBody);

        // Assert

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendHtmlEmail_should_call_mailSender_with_correct_parameters() throws MessagingException {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String htmlBody = "<html><body><h1>Test</h1></body></html>";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        assertDoesNotThrow(() -> emailService.sendHtmlEmail(to, subject, htmlBody));

        // Assert
        verify(mailSender, times(1)).send(mimeMessage);
    }
}
