package itacademy.pawalert.application.user.service;

import itacademy.pawalert.application.user.port.inbound.*;
import itacademy.pawalert.application.user.port.outbound.UserRepositoryPort;
import itacademy.pawalert.domain.user.Role;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.exception.CannotModifyLastAdminException;
import itacademy.pawalert.domain.user.exception.UserNotFoundException;
import itacademy.pawalert.domain.user.model.*;
import itacademy.pawalert.infrastructure.rest.user.dto.RegistrationInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService implements
        CreateUserUseCase,
        GetUserUseCase,
        DeleteUserUseCase,
        UpdateUserUseCase,
        UpdatePasswordUseCase {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(RegistrationInput input) {

        Username username = Username.of(input.username());
        Email email = Email.of(input.email());
        Surname surname = Surname.of(input.surname());
        PhoneNumber phoneNumber = PhoneNumber.of(input.phoneNumber());


        User user = new User(
                UUID.randomUUID(),
                username,
                email,
                surname,
                phoneNumber,
                Role.USER
        );

        String hashedPassword = passwordEncoder.encode(input.password());
        return userRepositoryPort.saveWithPasswordHash(user, hashedPassword);
    }

    @Override
    public void changePassword(UUID userId, Password currentPassword, Password newPassword) {

        String storedHash = userRepositoryPort.getPasswordHashById(userId);

        if (!passwordEncoder.matches(currentPassword.value(), storedHash)) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        String newHash = passwordEncoder.encode(newPassword.value());

        userRepositoryPort.updatePasswordHash(userId, newHash);
    }


    @Override
    public User updatePhonenumber(UUID userId, PhoneNumber phoneNumber) {
        return userRepositoryPort.updatePhoneNumber(userId, phoneNumber);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return userRepositoryPort.existsByEmail(email);
    }

    @Override
    public User getBySurname(Surname surname) {
        return userRepositoryPort.findBySurname(surname)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + surname));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepositoryPort.findAll();
    }

    @Override
    public long countByRole() {
        return 0;
    }

    @Override
    public User getById(UUID userId) {
        return userRepositoryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
    }


    @Override
    public void deleteByEmail(Email email) {
        User user = getByEmail(email);
        userRepositoryPort.delete(user);

    }

    @Override
    public void deleteById(UUID userId) {
        User user = getById(userId);
        // Security check: Cannot delete the last admin
        if (user.role() == Role.ADMIN) {
            long adminCount = userRepositoryPort.countByRole(Role.ADMIN);
            if (adminCount <= 1) {
                throw new CannotModifyLastAdminException(
                        "Cannot delete the last admin user. At least one admin must exist."
                );
            }
        }
        userRepositoryPort.delete(user);
    }

    @Override
    public User getByUsername(Username username) {
        return userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }

    @Override
    public User getByEmail(Email email) {
        return userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Override
    public boolean existsBySurname(Surname surname) {
        return userRepositoryPort.existsBySurname(surname);
    }


    @Override
    public User updateUsername(UUID userId, Username newUsername) {
        User user = getById(userId);
        User updated = new User(
                user.id(),
                newUsername,
                user.email(),
                user.surname(),
                user.phoneNumber(),
                user.role(),
                user.telegramChatId(),
                user.emailNotificationsEnabled(),
                user.telegramNotificationsEnabled()
        );
        return userRepositoryPort.save(updated);
    }

    @Override
    public User updateSurname(UUID userId, Surname newSurname) {
        User user = getById(userId);
        User updated = new User(
                user.id(),
                user.username(),
                user.email(),
                newSurname,
                user.phoneNumber(),
                user.role(),
                user.telegramChatId(),
                user.emailNotificationsEnabled(),
                user.telegramNotificationsEnabled()
        );
        return userRepositoryPort.save(updated);
    }

    @Override
    public User updateEmail(UUID userId, Email newEmail) {
        User user = getById(userId);
        User updated = new User(
                user.id(),
                user.username(),
                newEmail,
                user.surname(),
                user.phoneNumber(),
                user.role(),
                user.telegramChatId(),
                user.emailNotificationsEnabled(),
                user.telegramNotificationsEnabled()
        );
        return userRepositoryPort.save(updated);
    }

    @Override
    public User updateEmailNotifications(UUID userId, boolean emailNotificationsEnabled) {
        User user = getById(userId);
        User updated = new User(
                user.id(),
                user.username(),
                user.email(),
                user.surname(),
                user.phoneNumber(),
                user.role(),
                user.telegramChatId(),
                emailNotificationsEnabled,
                user.telegramNotificationsEnabled()
        );
        return userRepositoryPort.save(updated);
    }

    @Override
    public User updateTelegramNotifications(UUID userId, boolean telegramNotificationsEnabled) {
        User user = getById(userId);
        User updated = new User(
                user.id(),
                user.username(),
                user.email(),
                user.surname(),
                user.phoneNumber(),
                user.role(),
                user.telegramChatId(),
                user.emailNotificationsEnabled(),
                telegramNotificationsEnabled
        );
        return userRepositoryPort.save(updated);
    }

    @Override
    public User updateTelegramChatId(UUID userId, TelegramChatId telegramChatId) {
        User user = getById(userId);
        User updated = new User(
                user.id(),
                user.username(),
                user.email(),
                user.surname(),
                user.phoneNumber(),
                user.role(),
                telegramChatId,
                user.emailNotificationsEnabled(),
                user.telegramNotificationsEnabled()
        );
        return userRepositoryPort.save(updated);
    }

    @Override
    public User updateRole(UUID userId, Role newRole) {

        logger.info("updateRole called for userId: {} with newRole: {}", userId, newRole);
        User user = getById(userId);
        logger.info("Current user role before update: {}", user.role());
        // Security check: Cannot demote the last admin
        if (user.role() == Role.ADMIN && newRole == Role.USER) {
            long adminCount = userRepositoryPort.countByRole(Role.ADMIN);
            if (adminCount <= 1) {
                throw new CannotModifyLastAdminException(
                        "Cannot demote the last admin user. At least one admin must exist."
                );
            }
        }

        user = user.withRole(newRole);
        logger.info("User role after withRole: {}", user.role());
        
        User saved = userRepositoryPort.save(user);
        logger.info("Saved user role: {}", saved.role());
        
        logger.debug("Role updated for user {} from {} to {}", userId, user.role(), newRole);
        return saved;
    }


    public PhoneNumber getPhoneNumberById(UUID userId) {
        User user = getById(userId);
        return user.phoneNumber();
    }
}
