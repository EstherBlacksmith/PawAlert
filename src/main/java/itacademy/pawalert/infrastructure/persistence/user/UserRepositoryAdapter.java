package itacademy.pawalert.infrastructure.persistence.user;

import itacademy.pawalert.application.port.outbound.UserRepositoryPort;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.UserWithPassword;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository jpaUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRepositoryAdapter(UserRepository jpaRepository, PasswordEncoder passwordEncoder) {
        this.jpaUserRepository = jpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id.toString())
                .map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username)
                .map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(UserEntity::toDomain);
    }

    @Override
    public Optional<UserWithPassword> findByUsernameWithPassword(String username) {
        return jpaUserRepository.findByUsername(username)
                .map(entity -> new UserWithPassword(
                        entity.toDomain(),
                        entity.getPasswordHash()
                ));
    }

    @Override
    public User save(User user) {
        String passwordHash = jpaUserRepository.findById(user.getId().toString())
                .map(UserEntity::getPasswordHash)
                .orElse(null);

        UserEntity entity = toEntity(user,passwordHash);
        UserEntity saved = jpaUserRepository.save(entity);

        return saved.toDomain();
    }


    public User saveWithPlainPassword(User user, String plainPassword) {
        // Hash the password here (adapter responsibility)
        String hashedPassword = passwordEncoder.encode(plainPassword);

        UserEntity entity = toEntity(user, hashedPassword);
        UserEntity saved = jpaUserRepository.save(entity);

        return saved.toDomain();
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaUserRepository.existsById(id.toString());
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaUserRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public void delete(User user) {
        jpaUserRepository.deleteById(user.getId().toString());
    }

    private UserEntity toEntity(User user, String passwordHash) {
        return new UserEntity(
                user.getId().toString(),
                user.getUsername(),
                user.getEmail(),
                passwordHash,
                user.getFullName(),
                user.getPhoneNumber(),
                LocalDateTime.now()
        );
    }
}
