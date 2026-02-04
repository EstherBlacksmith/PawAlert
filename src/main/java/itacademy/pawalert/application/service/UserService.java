package itacademy.pawalert.application.service;

import itacademy.pawalert.application.port.inbound.CreateUserUseCase;
import itacademy.pawalert.application.port.inbound.DeleteUserUseCase;
import itacademy.pawalert.application.port.inbound.GetUserUseCase;
import itacademy.pawalert.application.port.inbound.UpdateUserUseCase;

import itacademy.pawalert.application.port.outbound.UserRepositoryPort;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.infrastructure.persistence.user.UserEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements
        CreateUserUseCase,
        GetUserUseCase,
        DeleteUserUseCase,
        UpdateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public UserService(UserRepositoryPort userRepository) {
        this.userRepositoryPort = userRepository;
    }

    @Override
    public User register(String username, String fullname, String email, String phoneNumber,String password) {
        User user = new User(
                java.util.UUID.randomUUID(),
                username,
                email,
                fullname,
                phoneNumber
        );
        return userRepositoryPort.saveWithPlainPassword(user, password);
    }

    @Override
    public boolean existsByUsername(String username) {
       return userRepositoryPort.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepositoryPort.existsByEmail(email);
    }

    @Override
    public User getById(String userId) {
        return userRepositoryPort.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
    }


    @Override
    public void deleteByEmail(String email) {
        User user = getByEmail(email);
        userRepositoryPort.delete(user);

    }

    @Override
    public User getByUsername(String username) {
        return userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }

    @Override
    public User getByEmail(String email) {
        return userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Override
    public User updateUsername(String email, String newUsername) {
        User user = getByEmail(email);
        User updated = new User(
                user.getId(),
                newUsername,
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber()
        );
        return userRepositoryPort.save(updated);
    }

    @Override
    public User updateFullname(String email, String fullName) {
        User user = getByEmail(email);
        User updated = new User(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                fullName,
                user.getPhoneNumber()
        );
        return userRepositoryPort.save(updated);
    }


}
