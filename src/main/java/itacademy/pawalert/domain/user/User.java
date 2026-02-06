package itacademy.pawalert.domain.user;

import itacademy.pawalert.domain.user.model.Email;
import itacademy.pawalert.domain.user.model.PhoneNumber;
import itacademy.pawalert.domain.user.model.Surname;
import itacademy.pawalert.domain.user.model.Username;
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

    public User(UUID id, Username username, Email email, Surname surname, PhoneNumber phoneNumber, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }


}
