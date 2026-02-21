package itacademy.pawalert.infrastructure.notification.mail;

import itacademy.pawalert.application.notification.port.outbound.EmailServicePort;
import itacademy.pawalert.infrastructure.persistence.alert.AlertSubscriptionRepository;
import itacademy.pawalert.infrastructure.persistence.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring5.SpringTemplateEngine;


@Service
public class EmailServiceImpl implements EmailServicePort {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final AlertSubscriptionRepository subscriptionRepository;

    public EmailServiceImpl(SpringTemplateEngine templateEngine,
                            JavaMailSender mailSender,
                            UserRepository userRepository,
                            AlertSubscriptionRepository subscriptionRepository) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public void sendToUser(String to, String subject, String htmlBody) {
        LOGGER.info("[EMAIL-SERVICE] Attempting to send email to: {}, subject: {}, from: {}", to, subject, fromEmail);
        try{ 
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            
            LOGGER.info("[EMAIL-SERVICE] Calling mailSender.send()...");
            mailSender.send(message);
            LOGGER.info("[EMAIL-SERVICE] Email successfully sent to: {}", to);
        } catch (MessagingException e) {
            LOGGER.error("[EMAIL-SERVICE] MessagingException sending email to {}: {}", to, e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("[EMAIL-SERVICE] Exception sending email to {}: {}", to, e.getMessage(), e);
        }

    }

}

