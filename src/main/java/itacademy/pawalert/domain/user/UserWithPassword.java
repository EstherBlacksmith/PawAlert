package itacademy.pawalert.domain.user;

import itacademy.pawalert.domain.user.model.TelegramChatId;
import lombok.Getter;

import java.util.UUID;


public class UserWithPassword {
    @Getter
    private final User user;
    @Getter
    private final String passwordHash;

    public UserWithPassword(User user, String passwordHash) {
        this.user = user;
        this.passwordHash = passwordHash;
    }

    public UUID getId() { return user.getId(); }
    public String getUsername() { return user.getUsername().value(); }
    public String getEmail() { return user.getEmail().value(); }
    public String getFullName() { return user.getSurname().value(); }
    public String getPhoneNumber() { return user.getPhoneNumber().value(); }
    public Role getRole() { return user.getRole(); }
    public TelegramChatId getTelegramChatId() {return user.getTelegramChatId(); }
}
