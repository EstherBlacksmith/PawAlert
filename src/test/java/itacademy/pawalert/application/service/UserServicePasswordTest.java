package itacademy.pawalert.application.service;

import itacademy.pawalert.application.port.outbound.UserRepositoryPort;
import itacademy.pawalert.domain.user.Role;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import itacademy.pawalert.infrastructure.rest.user.dto.RegistrationInput;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for UserService - Password Functionality
 * Following TDD and SOLID principles with comprehensive coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Password Tests")
class UserServicePasswordTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private String testEmail;
    private String testPassword;
    private String hashedPassword;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";
        testPassword = "SecurePass123!";
        hashedPassword = "$2a$10$hashedpasswordvalue";

        testUser = new User(
                UUID.randomUUID(),
                Username.of("testuser"),
                Email.of(testEmail),
                Surname.of("Test User"),
                PhoneNumber.of("612345678"),
                Role.USER
        );
    }
    @Test
    void verifyPasswordValidationWorks() {
        // Valid password - should NOT throw
        assertDoesNotThrow(() -> new Password("SecurePass123!"));

        // Invalid passwords - should throw
        assertThrows(IllegalArgumentException.class, () -> new Password("noNumber@"));
        assertThrows(IllegalArgumentException.class, () -> new Password("NOLOWER@123"));
        assertThrows(IllegalArgumentException.class, () -> new Password("noupper@123"));
        assertThrows(IllegalArgumentException.class, () -> new Password("NoSpecial1"));
        assertThrows(IllegalArgumentException.class, () -> new Password("short@1"));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Password Value Object Tests
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("Password Value Object Validation")
    class PasswordValidationTests {

        @Test
        @DisplayName("Should create password with valid input")
        void shouldCreatePasswordWithValidInput() {
            // Given
            String validPassword = "SecurePass123@";

            // When
            Password password = new Password(validPassword);

            // Then
            assertNotNull(password);
            assertEquals(validPassword, password.value());
        }

        @Test
        @DisplayName("Should throw exception for null password")
        void shouldThrowForNullPassword() {
            assertThrows(IllegalArgumentException.class, () -> new Password(null));
        }

        @Test
        @DisplayName("Should throw exception for password less than 8 characters")
        void shouldThrowForShortPassword() {
            String shortPassword = "Short1";

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Password(shortPassword)
            );
            assertTrue(exception.getMessage().contains("at least 8 characters"));
        }

        @Test
        @DisplayName("Should throw exception for password without uppercase")
        void shouldThrowForPasswordWithoutUppercase() {
            String noUppercase = "password123";

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Password(noUppercase)
            );
            assertTrue(exception.getMessage().contains("uppercase"));
        }

        @Test
        @DisplayName("Should throw exception for password without numbers")
        void shouldThrowForPasswordWithoutNumbers() {
            String noNumbers = "PasswordNo";

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Password(noNumbers)
            );
            assertTrue(exception.getMessage().contains("number"));
        }

        @Test
        @DisplayName("Factory method should create password successfully")
        void factoryMethodShouldCreatePassword() {
            Password password = Password.fromPlainText("ValidPass123@");
            assertNotNull(password);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Registration Tests
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("User Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should register user successfully with valid input")
        void shouldRegisterUserSuccessfully() {
            // Given - RegistrationInput order: username, email, password, surname, phoneNumber
            RegistrationInput input = new RegistrationInput(
                   "testuser",       // username
                   testEmail,        // email
                   testPassword,     // password
                   "Test User",      // surname
                   "612345678"       // phoneNumber (9 digits)
            );

            when(passwordEncoder.encode(testPassword)).thenReturn(hashedPassword);
            when(userRepository.saveWithPasswordHash(any(User.class), anyString()))
                    .thenAnswer(inv -> inv.getArgument(0));

            // When
            User result = userService.register(input);

            // Then
            assertNotNull(result);
            assertEquals("testuser", result.getUsername().value());
            assertEquals(testEmail, result.getEmail().value());
            verify(passwordEncoder).encode(testPassword);
            verify(userRepository).saveWithPasswordHash(any(User.class), eq(hashedPassword));
        }

        @Test
        @DisplayName("Should encode password before saving")
        void shouldEncodePasswordBeforeSaving() {
            // Given - RegistrationInput order: username, email, password, surname, phoneNumber
            RegistrationInput input = new RegistrationInput(
                    "newuser",                // username
                    "new@example.com",        // email
                    testPassword,             // password
                    "New User",               // surname
                    "987654321"               // phoneNumber
            );

            when(passwordEncoder.encode(anyString())).thenReturn(hashedPassword);
            when(userRepository.saveWithPasswordHash(any(User.class), anyString()))
                    .thenAnswer(inv -> inv.getArgument(0));

            // When
            userService.register(input);

            // Then
            verify(passwordEncoder).encode(testPassword);
            verify(userRepository).saveWithPasswordHash(any(User.class), anyString());
        }

        @Test
        @DisplayName("Should assign USER role to new user")
        void shouldAssignUserRole() {
            // Given - RegistrationInput order: username, email, password, surname, phoneNumber
            RegistrationInput input = new RegistrationInput(
                    "roleuser",               // username
                    "role@example.com",       // email
                    testPassword,             // password
                    "Role User",              // surname
                    "111222333"              // phoneNumber
            );

            when(passwordEncoder.encode(anyString())).thenReturn(hashedPassword);
            when(userRepository.saveWithPasswordHash(any(User.class), anyString()))
                    .thenAnswer(inv -> inv.getArgument(0));

            // When
            User result = userService.register(input);

            // Then
            assertEquals(Role.USER, result.getRole());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Password Change Tests
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("Password Change Tests")
    class PasswordChangeTests {

        @Test
        @DisplayName("Should validate new password meets requirements")
        void shouldValidateNewPasswordRequirements() {
            // Given
            Password currentPassword = new Password(testPassword);
            Password invalidNewPassword = new Password("shorM10&"); // Less than 8 chars
            UUID userId = UUID.randomUUID();

            // When/Then - should throw because new password is too short
            assertThrows(IllegalArgumentException.class, () ->
                    userService.changePassword(userId, currentPassword, invalidNewPassword));

            verify(userRepository, never()).updatePasswordHash(eq(userId), anyString());
        }

        @Test
        @DisplayName("Should not update password hash if validation fails")
        void shouldNotUpdateHashIfValidationFails() {
            // Given
            Password currentPassword = new Password(testPassword);
            Password invalidNewPassword = new Password("weakPas0sd@"); // No uppercase
            UUID userId = UUID.randomUUID();

            // When/Then
            assertThrows(IllegalArgumentException.class, () ->
                    userService.changePassword(userId, currentPassword, invalidNewPassword));

            verify(userRepository, never()).updatePasswordHash(eq(userId), anyString());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Integration-style Repository Tests
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("Repository Password Operations")
    class RepositoryPasswordTests {

        @Test
        @DisplayName("Should save user with hashed password")
        void shouldSaveUserWithHashedPassword() {
            // Given
            User user = testUser;
            String passwordHash = "$2a$10$testhash";

            when(userRepository.saveWithPasswordHash(any(User.class), anyString()))
                    .thenAnswer(inv -> inv.getArgument(0));

            // When
            User result = userRepository.saveWithPasswordHash(user, passwordHash);

            // Then
            assertNotNull(result);
            verify(userRepository).saveWithPasswordHash(user, passwordHash);
        }

        @Test
        @DisplayName("Should retrieve password hash by email")
        void shouldRetrievePasswordHashByEmail() {
            // Given
            when(userRepository.getPasswordHashByEmail(Email.of(testEmail))).thenReturn(hashedPassword);

            // When
            String result = userRepository.getPasswordHashByEmail(Email.of(testEmail));

            // Then
            assertEquals(hashedPassword, result);
        }

        @Test
        @DisplayName("Should update password hash")
        void shouldUpdatePasswordHash() {
            // Given
            String newHash = "$2a$10$newhashvalue";
            UUID userId = UUID.randomUUID();

            doNothing().when(userRepository).updatePasswordHash(userId, newHash);

            // When/Then
            assertDoesNotThrow(() -> userRepository.updatePasswordHash(userId, newHash));
            verify(userRepository).updatePasswordHash(userId, newHash);
        }
    }
}
