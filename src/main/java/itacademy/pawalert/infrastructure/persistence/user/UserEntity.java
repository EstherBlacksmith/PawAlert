package itacademy.pawalert.infrastructure.persistence.user;

import itacademy.pawalert.domain.user.Role;
import jakarta.persistence.*;
import lombok.Getter;
import itacademy.pawalert.domain.user.User;

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

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "full_name")
    private String fullName;

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
                      String fullName, String phoneNumber, Role role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.role = role;
    }

    public User toDomain() {
        return new User(
                UUID.fromString(this.id),
                this.username,
                this.email,
                this.fullName,
                this.phoneNumber,
                this.role
        );
    }
}
