package itacademy.pawalert.infrastructure.persistence.user;

import itacademy.pawalert.domain.user.Role;
import itacademy.pawalert.domain.user.UserWithPassword;
import itacademy.pawalert.domain.user.model.*;
import jakarta.persistence.*;
import lombok.Getter;
import itacademy.pawalert.domain.user.User;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @Column(name = "id")
    private String id;

    @Setter
    @Column(name = "username", unique = true)
    private String username;

    @Setter
    @Column(name = "password_hash")
    private String passwordHash;

    @Setter
    @Column(name = "surname")
    private String surname;

    @Setter
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Setter
    @Column(name = "telegram_chat_id")
    private String telegramChatId;

    @Setter
    @Column(name = "email_notifications_enabled")
    private boolean emailNotificationsEnabled;

    @Setter
    @Column(name = "telegram_notifications_enabled")
    private boolean telegramNotificationsEnabled;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Empty Constructor for JPA
    public UserEntity() {}

    public UserEntity(String id, String username, String email, String passwordHash,
                      String surname, String phoneNumber, Role role, LocalDateTime createdAt,
                      String telegramChatId, boolean emailNotificationsEnabled, boolean telegramNotificationsEnabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.role = role;
        this.telegramChatId = telegramChatId;
        this.emailNotificationsEnabled = emailNotificationsEnabled;
        this.telegramNotificationsEnabled = telegramNotificationsEnabled;
    }

    public User toDomain() {
        return new User(
                UUID.fromString(this.id),
                Username.of(this.username),
                Email.of(this.email),
                Surname.of(this.surname),
                PhoneNumber.of(this.phoneNumber),
                this.role,
                TelegramChatId.of(this.telegramChatId),
                this.emailNotificationsEnabled,
                this.telegramNotificationsEnabled
        );
    }

    public UserWithPassword toDomainWithPassword() {
        return new UserWithPassword(
                new User(
                        UUID.fromString(this.id),
                        Username.of(this.username),
                        Email.of(this.email),
                        Surname.of(this.surname),
                        PhoneNumber.of(this.phoneNumber),
                        this.role,
                        TelegramChatId.of(this.telegramChatId),
                        this.emailNotificationsEnabled,
                        this.telegramNotificationsEnabled
                ),
                this.passwordHash
        );
    }

}
