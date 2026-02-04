package itacademy.pawalert.application.service;

import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.infrastructure.persistence.user.UserRepository;

import java.util.UUID;

public class UserService {
    UserRepository userRepository;
    public boolean existsByUsername(String username) {
       return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);

    }

    public User findById(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
    }


    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }

    public User findByEmail(String email) {
    }

    public User deleteByEmail(String email) {
       User user =  userRepository.findByEmail(email);
        userRepository.delete(user);
    }

    public User updateUsername(String email, String newUsername) {
        User user = findByEmail(email);
        User updated = new User(
                user.getId(),
                newUsername,
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber()
        );
    }
}
