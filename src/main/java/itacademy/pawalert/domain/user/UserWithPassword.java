package itacademy.pawalert.domain.user;

import itacademy.pawalert.domain.user.model.TelegramChatId;

import java.util.UUID;


public record UserWithPassword(User user, String passwordHash) {

    public UUID getId() {
        return user.id();
    }

    public String getUsername() {
        return user.username().value();
    }

    public String getEmail() {
        return user.email().value();
    }

    public String getFullName() {
        return user.surname().value();
    }

    public String getPhoneNumber() {
        return user.phoneNumber().value();
    }

    public Role getRole() {
        return user.role();
    }

    public TelegramChatId getTelegramChatId() {
        return user.telegramChatId();
    }
}
