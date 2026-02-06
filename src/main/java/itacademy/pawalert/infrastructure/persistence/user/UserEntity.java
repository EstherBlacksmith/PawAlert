package itacademy.pawalert.infrastructure.persistence.user;

import itacademy.pawalert.domain.user.Role;
import itacademy.pawalert.domain.user.UserWithPassword;
import itacademy.pawalert.domain.user.model.Email;
import itacademy.pawalert.domain.user.model.PhoneNumber;
import itacademy.pawalert.domain.user.model.Surname;
import itacademy.pawalert.domain.user.model.Username;
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

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @Setter
    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "surname")
    private String surname;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    // Empty Constructor for JPA
    public UserEntity() {}

    public UserEntity(String id, String username, String email, String passwordHash,
                      String surname, String phoneNumber, Role role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.role = role;
    }

    public User toDomain() {
        return new User(
                UUID.fromString(this.id),
                Username.of(this.username),
                Email.of(this.email),
                Surname.of(this.surname),
                PhoneNumber.of(this.phoneNumber),
                this.role
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
                        this.role
                ),
                this.passwordHash
        );
    }

}
