package itacademy.pawalert.application.port.outbound;

import itacademy.pawalert.domain.user.Role;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.UserWithPassword;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    Optional<UserWithPassword> findByUsernameWithPassword(String username);
    User save(User user);
    User saveWithPlainPassword(User user, String plainPassword);
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsById(UUID id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void delete(User user);
    Role getUserRol(String userId);
}
