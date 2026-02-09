package itacademy.pawalert.domain.notification.exception;

import jakarta.mail.MessagingException;

public class EmailSendException extends Throwable {
    public EmailSendException(String string, MessagingException messagingException) {
    }
}
