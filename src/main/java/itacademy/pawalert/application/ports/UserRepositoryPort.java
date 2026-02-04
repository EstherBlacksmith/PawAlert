package itacademy.pawalert.application.ports;

import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.UserWithPassword;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<UserWithPassword> findByUsernameWithPassword(String username);
    User save(User user, String passwordHash);
}
