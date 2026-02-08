package itacademy.pawalert.infrastructure.notification.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.antlr.v4.runtime.misc.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

@Service
public class EmailServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    public EmailServiceImpl(SpringTemplateEngine templateEngine, JavaMailSender mailSender) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }

    public boolean sendEmail(String  emailBody) {
        Context context = new Context();
        LOGGER.info("EmailBody: {}", emailBody.toString());
        String htmlContent = templateEngine.process("email.html", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        String senderEmail = "arikhel@gmail.com";
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setSubject("Testing 1,2,3");
            message.setFrom(senderEmail, "SendGrid Tester");
            message.setTo("arikhel@gmail.com");
            message.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

       return true;
    }

    private void sendEmailTool(String content, String email, String subject) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom("PawAlwert@gmail.com");
       // message.setTo(new String[] {"recipient1@example.com", "recipient2@example.com", "recipient3@example.com"});
        message.setRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject(subject);
        message.setText(content);

        String htmlTemplate = Arrays.toString(Utils.readFile("change-status-notification.html"));
        htmlTemplate = htmlTemplate.replace("${name}", "John Doe");
        htmlTemplate = htmlTemplate.replace("${message}", "Hello, this is a test email.");


        message.setContent(htmlTemplate, "text/html; charset=utf-8");

        mailSender.send(message);
    }


    public void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true indicates HTML
        mailSender.send(message);
    }



    public void send(SimpleMailMessage... simpleMessages) throws MailException {
        Context context = new Context();
        LOGGER.info("EmailBody: {}", simpleMessages.toString());
        String htmlContent = templateEngine.process("email.html", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        String senderEmail = "arikhel@gmial.com";
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setSubject("Testing 1,2,3");
            message.setFrom(senderEmail, "SendGrid Tester");
            message.setTo("arikhel@gmail.com");
            message.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}

