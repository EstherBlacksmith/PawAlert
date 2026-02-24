package itacademy.pawalert.infrastructure.messaging.email;

import lombok.Getter;


@Getter
public class EmailNotificationException extends RuntimeException {

    private final String email;

    public EmailNotificationException(String email, String message) {
        super(message);
        this.email = email;
    }

    public EmailNotificationException(String email, String message, Throwable cause) {
        super(message, cause);
        this.email = email;
    }
}
