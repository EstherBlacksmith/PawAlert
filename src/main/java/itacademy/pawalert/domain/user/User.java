package itacademy.pawalert.domain.user;

import itacademy.pawalert.domain.user.model.*;

import java.util.UUID;

// Getters (just get for immutability)
public record User(UUID id, Username username, Email email, Surname surname, PhoneNumber phoneNumber, Role role,
                   TelegramChatId telegramChatId, boolean emailNotificationsEnabled,
                   boolean telegramNotificationsEnabled) {

    // Primary constructor with all fields

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

    public User withRole(Role newRole) {
        return new User(
                this.id,
                this.username,
                this.email,
                this.surname,
                this.phoneNumber,
                newRole,
                this.telegramChatId,
                this.emailNotificationsEnabled,
                this.telegramNotificationsEnabled
        );
    }
}
