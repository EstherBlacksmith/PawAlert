package itacademy.pawalert.domain.user;

import java.util.UUID;

public class UserWithPassword {
    private final User user;
    private final String passwordHash;

    public UserWithPassword(User user, String passwordHash) {
        this.user = user;
        this.passwordHash = passwordHash;
    }

    public User getUser() { return user; }
    public String getPasswordHash() { return passwordHash; }

    public UUID getId() { return user.getId(); }
    public String getUsername() { return user.getUsername(); }
    public String getEmail() { return user.getEmail(); }
    public String getFullName() { return user.getFullName(); }
    public String getPhoneNumber() { return user.getPhoneNumber(); }
}
