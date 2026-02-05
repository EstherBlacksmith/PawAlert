package itacademy.pawalert.domain.user;

import lombok.Getter;
import org.jspecify.annotations.Nullable;

import java.util.UUID;
@Getter // Getters (just get for immutability)
public class User {

    private final UUID id;
    private final String username;
    private final String email;
    private final String fullName;
    private final String phoneNumber;
    private final Role role;

    public User(UUID id, String username, String email, String fullName, String phoneNumber, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

}
