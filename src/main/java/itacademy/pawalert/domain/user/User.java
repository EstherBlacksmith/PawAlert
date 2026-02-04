package itacademy.pawalert.domain.user;

import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class User {
    private final UUID id;
    private final String username;
    private final String email;
    private final String fullName;
    private final String phoneNumber;

    public User(UUID id, String username, String email, String fullName, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    // Getters (solo lectura para inmutabilidad)
    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPhoneNumber() { return phoneNumber; }

    public @Nullable String getPassword() {
        return phoneNumber;
    }
}
