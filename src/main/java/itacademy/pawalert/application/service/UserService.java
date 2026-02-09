package itacademy.pawalert.application.service;

import itacademy.pawalert.application.port.inbound.*;

import itacademy.pawalert.application.port.outbound.UserRepositoryPort;
import itacademy.pawalert.domain.user.Role;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.model.*;
import itacademy.pawalert.infrastructure.rest.user.dto.RegistrationInput;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.UUID;

@Service
public class UserService implements
        CreateUserUseCase,
        GetUserUseCase,
        DeleteUserUseCase,
        UpdateUserUseCase,
        UpdatePasswordUseCase {


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
        Surname surname =  Surname.of(input.surname());
        PhoneNumber phoneNumber =  PhoneNumber.of(input.phoneNumber());


        User user = new User(
                UUID.randomUUID(),
                username,
                email,
                surname,
                phoneNumber,
                Role.USER,null
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

        return userRepositoryPort.updatePhoneNumber(userId,phoneNumber);
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
        return userRepositoryPort.existsSurname(surname);
    }


    @Override
    public User updateUsername(UUID userId, Username newUsername) {
        User user = getById(userId);
        User updated = new User(
                user.getId(),
                newUsername,
                user.getEmail(),
                user.getSurname(),
                user.getPhoneNumber(),
                user.getRole()
                ,null
        );
        return userRepositoryPort.save(updated);
    }

    @Override
    public User updateSurname(UUID userId, Surname newSurname) {
        User user = getById(userId);
        User updated = new User(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                newSurname,
                user.getPhoneNumber(),
                user.getRole(),null
        );
        return userRepositoryPort.save(updated);
    }

    @Override
    public User updateEmail(UUID userId, Email newEmail) {
        User user = getById(userId);
        User updated = new User(
                user.getId(),
                user.getUsername(),
                newEmail,  // 👈 Email actualizado
                user.getSurname(),
                user.getPhoneNumber(),
                user.getRole(),null
        );
        return userRepositoryPort.save(updated);
    }



}
