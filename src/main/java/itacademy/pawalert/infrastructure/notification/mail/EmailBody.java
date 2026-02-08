package itacademy.pawalert.infrastructure.notification.mail;

import itacademy.pawalert.domain.user.model.Email;
import lombok.Getter;

@Getter
public class EmailBody {
    private String content;
    private Email email;
    private String subject;

}
