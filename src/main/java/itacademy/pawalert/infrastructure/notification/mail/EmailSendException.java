package itacademy.pawalert.infrastructure.notification.mail;

import jakarta.mail.MessagingException;

public class EmailSendException extends Throwable {
    public EmailSendException(String string, MessagingException messagingException) {
    }
}
