package itacademy.pawalert.infrastructure.persistence.user;

import itacademy.pawalert.application.ports.UserRepositoryPort;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.UserWithPassword;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository jpaRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRepositoryAdapter(UserRepository jpaRepository, PasswordEncoder passwordEncoder) {
        this.jpaRepository = jpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id.toString())
                .map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username)
                .map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(UserEntity::toDomain);
    }

    @Override
    public Optional<UserWithPassword> findByUsernameWithPassword(String username) {
        return jpaRepository.findByUsername(username)
                .map(entity -> new UserWithPassword(
                        entity.toDomain(),
                        entity.getPasswordHash()
                ));
    }

    @Override
    public User save(User user, String passwordHash) {
        // El password ya viene hasheado desde el servicio
        UserEntity entity = toEntity(user, passwordHash);
        UserEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    // ========== Convenience method for register ==========

    public User saveWithPlainPassword(User user, String plainPassword) {
        // Hashea el password aquí (responsabilidad del adapter)
        String hashedPassword = passwordEncoder.encode(plainPassword);
        return save(user, hashedPassword);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
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
