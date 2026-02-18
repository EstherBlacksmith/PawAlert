package itacademy.pawalert.infrastructure.persistence.user;


import itacademy.pawalert.application.user.port.outbound.UserRepositoryPort;
import itacademy.pawalert.domain.user.Role;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.UserWithPassword;

import itacademy.pawalert.domain.user.model.Email;
import itacademy.pawalert.domain.user.model.PhoneNumber;
import itacademy.pawalert.domain.user.model.Surname;
import itacademy.pawalert.domain.user.model.Username;
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
    public Optional<User> findByUsername(Username username) {
        return jpaUserRepository.findByUsername(String.valueOf(username))
                .map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findBySurname(Surname surname) {
        return jpaUserRepository.findBySurname(surname.value())
                .map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaUserRepository.findByEmail(String.valueOf(email))
                .map(UserEntity::toDomain);
    }

    @Override
    public Optional<UserWithPassword> findByUsernameWithPassword(Username username) {
        return jpaUserRepository.findByUsername(String.valueOf(username))
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


    @Override
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
    public boolean existsBySurname(Surname surname) {
        return jpaUserRepository.existsBySurname(String.valueOf(surname));
    }
    @Override
    public boolean existsByEmail(Email email) {
        return jpaUserRepository.existsByEmail(String.valueOf(email));
    }

    @Override
    public void delete(User user) {
        jpaUserRepository.deleteById(user.getId().toString());
    }

    @Override
    public Role getUserRol(UUID userId) {
        return jpaUserRepository.findById(String.valueOf(userId))
                .map(UserEntity::getRole)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    private UserEntity toEntity(User user, String passwordHash) {
        return new UserEntity(
                user.getId().toString(),
                user.getUsername().value(),
                user.getEmail().value(),
                passwordHash,
                user.getSurname().value(),
                user.getPhoneNumber().value(),
                user.getRole(),
                LocalDateTime.now(),null
        );
    }
    @Override
    public User saveWithPasswordHash(User user, String passwordHash) {
        UserEntity entity = toEntity(user, passwordHash);
        UserEntity saved = jpaUserRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public String getPasswordHashByEmail(Email email) {
        return jpaUserRepository.findByEmail(String.valueOf(email))
                .map(UserEntity::getPasswordHash)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }

    @Override
    public String getPasswordHashById(UUID userId) {
        return jpaUserRepository.findById(String.valueOf(userId))
                .map(UserEntity::getPasswordHash)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public void updatePasswordHash(UUID userId, String newHash) {
        UserEntity entity = jpaUserRepository.findById(String.valueOf(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        entity.setPasswordHash(newHash);
        jpaUserRepository.save(entity);
    }

    @Override
    public User updatePhoneNumber(UUID userId, PhoneNumber phoneNumber) {
        UserEntity entity = jpaUserRepository.findById(String.valueOf(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        entity.setPhoneNumber(phoneNumber.value());
        UserEntity saved = jpaUserRepository.save(entity);
        return saved.toDomain();
    }


    @Override
    public User updateSurname(UUID userId, Surname surname) {
        UserEntity entity = jpaUserRepository.findById(String.valueOf(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        entity.setSurname(surname.value());
        UserEntity saved = jpaUserRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public User updateUsername(UUID userId, Username username) {
        UserEntity entity = jpaUserRepository.findById(String.valueOf(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        entity.setUsername(username.value());
        UserEntity saved = jpaUserRepository.save(entity);
        return saved.toDomain();
    }



    @Override
    public boolean existsByRole(Role role) {
        return jpaUserRepository.existsByRole(role);
    }

}
