package itacademy.pawalert.infrastructure.notification.mail;

import itacademy.pawalert.infrastructure.persistence.alert.AlertSubscriptionRepository;
import itacademy.pawalert.infrastructure.persistence.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring5.SpringTemplateEngine;


@Service
public class EmailServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

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

    public void sendToUser(String to, String subject, String htmlBody) {
       try{ MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        mailSender.send(message);
        LOGGER.info("Email enviado a: {}", to);
        } catch (MessagingException e) {
               LOGGER.error("Error sending email: {}", e.getMessage());
        }

    }

}

