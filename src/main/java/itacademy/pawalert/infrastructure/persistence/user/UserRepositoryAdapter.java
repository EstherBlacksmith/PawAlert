package itacademy.pawalert.infrastructure.persistence.user;


import itacademy.pawalert.application.port.outbound.UserRepositoryPort;
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
        return Optional.empty();
    }

    @Override
    public Optional<User> findBySurname(Surname surname) {
        return jpaUserRepository.findBySurname(surname)
                .map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaUserRepository.findByEmail(email)
                .map(UserEntity::toDomain);
    }

    @Override
    public Optional<UserWithPassword> findByUsernameWithPassword(Username username) {
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
    public boolean existsSurname(Surname surname) {
        return jpaUserRepository.existsBySurname(surname);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public void delete(User user) {
        jpaUserRepository.deleteById(user.getId().toString());
    }

    @Override
    public Role getUserRol(UUID userId) {
        return jpaUserRepository.findRoleById(userId);
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
                LocalDateTime.now()
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
        return jpaUserRepository.findByEmail(email)
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

        // You need to add a setter for passwordHash in UserEntity
        entity.setPasswordHash(newHash);
        jpaUserRepository.save(entity);
    }

    @Override
    public User updatePhoneNumber(UUID userId, PhoneNumber phoneNumber) {
        return jpaUserRepository.updatePhonenumber(userId,phoneNumber);
    }


    @Override
    public User updateSurname(UUID userId, Surname surname) {
        return jpaUserRepository.updateSurname(userId,surname);
    }

    @Override
    public boolean existsBySurname(Surname surname) {
         return jpaUserRepository.existsBySurname(surname);
    }

}
