package itacademy.pawalert.domain.user;

import itacademy.pawalert.domain.user.model.*;
import lombok.Getter;
import java.util.UUID;

@Getter // Getters (just get for immutability)
public class User {

    private final UUID id;
    private final Username username;
    private final Email email;
    private final Surname surname;
    private final PhoneNumber phoneNumber;
    private final Role role;
    private final TelegramChatId telegramChatId;
    private final boolean emailNotificationsEnabled;
    private final boolean telegramNotificationsEnabled;

    // Primary constructor with all fields
    public User(UUID id, Username username, Email email, Surname surname, PhoneNumber phoneNumber,
                Role role, TelegramChatId telegramChatId, boolean emailNotificationsEnabled, boolean telegramNotificationsEnabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.telegramChatId = telegramChatId;
        this.emailNotificationsEnabled = emailNotificationsEnabled;
        this.telegramNotificationsEnabled = telegramNotificationsEnabled;
    }

    // Constructor with notification preferences but no telegramChatId
    public User(UUID id, Username username, Email email, Surname surname,
                PhoneNumber phoneNumber, Role role, boolean emailNotificationsEnabled, boolean telegramNotificationsEnabled) {
        this(id, username, email, surname, phoneNumber, role, null, emailNotificationsEnabled, telegramNotificationsEnabled);
    }

    // Constructor with telegramChatId but default notification preferences
    public User(UUID id, Username username, Email email, Surname surname, PhoneNumber phoneNumber, Role role, TelegramChatId telegramChatId) {
        this(id, username, email, surname, phoneNumber, role, telegramChatId, false, false);
    }

    // Constructor with only core user properties and default values
    public User(UUID id, Username username, Email email, Surname surname, PhoneNumber phoneNumber, Role role) {
        this(id, username, email, surname, phoneNumber, role, null, false, false);
    }

}
