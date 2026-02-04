package itacademy.pawalert.application.port.outbound;

import itacademy.pawalert.domain.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByUsername(String username);

    boolean existsById(UUID id);
}
