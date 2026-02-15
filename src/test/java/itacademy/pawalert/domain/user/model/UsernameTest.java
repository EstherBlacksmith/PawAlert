package itacademy.pawalert.domain.user.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Username Tests")
class UsernameTest {

    // ═══════════════════════════════════════════════════════════════════════
    // Valid Username Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Valid Username Tests")
    class ValidUsernameTests {

        @Test
        @DisplayName("Should create username with valid input")
        void shouldCreateUsernameWithValidInput() {
            Username username = Username.of("john_doe");
            assertEquals("john_doe", username.value());
        }

        @Test
        @DisplayName("Should handle username with underscore")
        void shouldHandleUsernameWithUnderscore() {
            Username username = Username.of("user_name");
            assertEquals("user_name", username.value());
        }

        @Test
        @DisplayName("Should handle username with hyphen")
        void shouldHandleUsernameWithHyphen() {
            Username username = Username.of("user-name");
            assertEquals("user-name", username.value());
        }

        @Test
        @DisplayName("Should handle username with spaces (compound names)")
        void shouldHandleUsernameWithSpaces() {
            Username username = Username.of("Juan Perez");
            assertEquals("Juan Perez", username.value());
        }

        @Test
        @DisplayName("Should handle username with Spanish characters")
        void shouldHandleSpanishCharacters() {
            Username username = Username.of("José García");
            assertEquals("José García", username.value());
        }

        @Test
        @DisplayName("Should handle username with apostrophe")
        void shouldHandleUsernameWithApostrophe() {
            Username username = Username.of("O'Brien");
            assertEquals("O'Brien", username.value());
        }

        @Test
        @DisplayName("Should handle username at minimum length")
        void shouldHandleUsernameAtMinLength() {
            Username username = Username.of("abc");
            assertEquals("abc", username.value());
        }

        @Test
        @DisplayName("Should handle username at maximum length")
        void shouldHandleUsernameAtMaxLength() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 50; i++) sb.append("a");
            Username username = Username.of(sb.toString());
            assertEquals(50, username.value().length());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Invalid Username Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Invalid Username Tests")
    class InvalidUsernameTests {

        @Test
        @DisplayName("Should throw for null username")
        void shouldThrowForNullUsername() {
            assertThrows(IllegalArgumentException.class, () -> Username.of(null));
        }

        @Test
        @DisplayName("Should throw for empty username")
        void shouldThrowForEmptyUsername() {
            assertThrows(IllegalArgumentException.class, () -> Username.of(""));
        }

        @Test
        @DisplayName("Should throw for blank username")
        void shouldThrowForBlankUsername() {
            assertThrows(IllegalArgumentException.class, () -> Username.of("   "));
        }

        @Test
        @DisplayName("Should throw for username too short")
        void shouldThrowForUsernameTooShort() {
            assertThrows(IllegalArgumentException.class, () -> Username.of("ab"));
        }

        @Test
        @DisplayName("Should throw for username too long")
        void shouldThrowForUsernameTooLong() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 51; i++) sb.append("a");
            assertThrows(IllegalArgumentException.class, () -> Username.of(sb.toString()));
        }

        @Test
        @DisplayName("Should throw for username with special characters")
        void shouldThrowForUsernameWithSpecialChars() {
            assertThrows(IllegalArgumentException.class, () -> Username.of("user@name"));
        }

        @Test
        @DisplayName("Should throw for username with numbers only")
        void shouldThrowForUsernameWithNumbersOnly() {
            assertThrows(IllegalArgumentException.class, () -> Username.of("12345"));
        }

        @Test
        @DisplayName("Should throw for username with dots")
        void shouldThrowForUsernameWithDots() {
            assertThrows(IllegalArgumentException.class, () -> Username.of("user.name"));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Username Factory Method Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Username Factory Method Tests")
    class UsernameFactoryTests {

        @Test
        @DisplayName("Factory method should create username successfully")
        void factoryMethodShouldCreateUsername() {
            Username username = Username.of("testuser");
            assertNotNull(username);
            assertEquals("testuser", username.value());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Username Equality Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Username Equality Tests")
    class UsernameEqualityTests {

        @Test
        @DisplayName("Usernames with same value should be equal")
        void usernamesWithSameValueShouldBeEqual() {
            Username username1 = Username.of("john_doe");
            Username username2 = Username.of("john_doe");
            assertEquals(username1, username2);
        }

        @Test
        @DisplayName("Usernames with different values should not be equal")
        void usernamesWithDifferentValuesShouldNotBeEqual() {
            Username username1 = Username.of("user1");
            Username username2 = Username.of("user2");
            assertNotEquals(username1, username2);
        }
    }
}
