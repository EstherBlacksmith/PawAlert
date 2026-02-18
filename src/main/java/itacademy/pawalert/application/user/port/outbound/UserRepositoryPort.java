package itacademy.pawalert.application.user.port.outbound;

import itacademy.pawalert.domain.pet.model.Pet;
import itacademy.pawalert.domain.user.Role;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.UserWithPassword;
import itacademy.pawalert.domain.user.model.Email;
import itacademy.pawalert.domain.user.model.PhoneNumber;
import itacademy.pawalert.domain.user.model.Surname;
import itacademy.pawalert.domain.user.model.Username;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    Optional<UserWithPassword> findByUsernameWithPassword(Username username);
    User save(User user);
    User saveWithPlainPassword(User user, String plainPassword);
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(Username username);
    Optional<User> findBySurname(Surname surname);
    Optional<User> findByEmail(Email email);
    boolean existsById(UUID id);
    boolean existsByEmail(Email email);
    void delete(User user);
    Role getUserRol(UUID userId);
    User saveWithPasswordHash(User user, String passwordHash);
    String getPasswordHashByEmail(Email email);
    void updatePasswordHash(UUID userId, String newHash);
    User updatePhoneNumber(UUID userId, PhoneNumber phoneNumber);
    User updateSurname(UUID userId, Surname surname);

    User updateUsername(UUID userId, Username username);

    boolean existsBySurname(Surname surname);
    String getPasswordHashById(UUID userId);
    boolean existsByRole(Role role);

}
