package itacademy.pawalert.domain.user.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Email Tests")
class EmailTest {

    // ═══════════════════════════════════════════════════════════════════════
    // Valid Email Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Valid Email Tests")
    class ValidEmailTests {

        @Test
        @DisplayName("Should create email with valid input")
        void shouldCreateEmailWithValidInput() {
            Email email = Email.of("test@example.com");
            assertEquals("test@example.com", email.value());
        }

        @Test
        @DisplayName("Should handle email with subdomain")
        void shouldHandleEmailWithSubdomain() {
            Email email = Email.of("user@mail.example.com");
            assertEquals("user@mail.example.com", email.value());
        }

        @Test
        @DisplayName("Should handle email with plus sign")
        void shouldHandleEmailWithPlusSign() {
            Email email = Email.of("user+tag@example.com");
            assertEquals("user+tag@example.com", email.value());
        }

        @Test
        @DisplayName("Should convert to lowercase")
        void shouldConvertToLowercase() {
            Email email = Email.of("USER@EXAMPLE.COM");
            assertEquals("user@example.com", email.value());
        }

        @Test
        @DisplayName("Should trim whitespace")
        void shouldTrimWhitespace() {
            Email email = Email.of("  test@example.com  ");
            assertEquals("test@example.com", email.value());
        }

        @Test
        @DisplayName("Should handle email with dots")
        void shouldHandleEmailWithDots() {
            Email email = Email.of("first.middle.last@example.com");
            assertEquals("first.middle.last@example.com", email.value());
        }

        @Test
        @DisplayName("Should handle email with underscore")
        void shouldHandleEmailWithUnderscore() {
            Email email = Email.of("user_name@example.com");
            assertEquals("user_name@example.com", email.value());
        }

        @Test
        @DisplayName("Should handle email with hyphen in domain")
        void shouldHandleEmailWithHyphenInDomain() {
            Email email = Email.of("user@my-domain.com");
            assertEquals("user@my-domain.com", email.value());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Invalid Email Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Invalid Email Tests")
    class InvalidEmailTests {

        @Test
        @DisplayName("Should throw for null email")
        void shouldThrowForNullEmail() {
            assertThrows(IllegalArgumentException.class, () -> Email.of(null));
        }

        @Test
        @DisplayName("Should throw for empty email")
        void shouldThrowForEmptyEmail() {
            assertThrows(IllegalArgumentException.class, () -> Email.of(""));
        }

        @Test
        @DisplayName("Should throw for blank email")
        void shouldThrowForBlankEmail() {
            assertThrows(IllegalArgumentException.class, () -> Email.of("   "));
        }

        @Test
        @DisplayName("Should throw for email without @")
        void shouldThrowForEmailWithoutAt() {
            assertThrows(IllegalArgumentException.class, 
                () -> Email.of("testexample.com"));
        }

        @Test
        @DisplayName("Should throw for email without domain")
        void shouldThrowForEmailWithoutDomain() {
            assertThrows(IllegalArgumentException.class, 
                () -> Email.of("test@"));
        }

        @Test
        @DisplayName("Should throw for email without username")
        void shouldThrowForEmailWithoutUsername() {
            assertThrows(IllegalArgumentException.class, 
                () -> Email.of("@example.com"));
        }

        @Test
        @DisplayName("Should throw for email with spaces")
        void shouldThrowForEmailWithSpaces() {
            assertThrows(IllegalArgumentException.class, 
                () -> Email.of("test @example.com"));
        }

        @Test
        @DisplayName("Should throw for email with double @")
        void shouldThrowForEmailWithDoubleAt() {
            assertThrows(IllegalArgumentException.class, 
                () -> Email.of("test@@example.com"));
        }

        @Test
        @DisplayName("Should throw for invalid domain format")
        void shouldThrowForInvalidDomainFormat() {
            assertThrows(IllegalArgumentException.class, 
                () -> Email.of("test@.com"));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Email Factory Method Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Email Factory Method Tests")
    class EmailFactoryTests {

        @Test
        @DisplayName("Factory method should create email successfully")
        void factoryMethodShouldCreateEmail() {
            Email email = Email.of("test@example.com");
            assertNotNull(email);
            assertEquals("test@example.com", email.value());
        }

        @Test
        @DisplayName("ofNullable should return null for null input")
        void ofNullableShouldReturnNullForNull() {
            Email email = Email.ofNullable(null);
            assertNull(email);
        }

        @Test
        @DisplayName("ofNullable should return null for blank input")
        void ofNullableShouldReturnNullForBlank() {
            Email email = Email.ofNullable("   ");
            assertNull(email);
        }

        @Test
        @DisplayName("ofNullable should return null for invalid email")
        void ofNullableShouldReturnNullForInvalid() {
            Email email = Email.ofNullable("invalid");
            assertNull(email);
        }

        @Test
        @DisplayName("ofNullable should return email for valid input")
        void ofNullableShouldReturnEmailForValid() {
            Email email = Email.ofNullable("test@example.com");
            assertNotNull(email);
            assertEquals("test@example.com", email.value());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Email Equality Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Email Equality Tests")
    class EmailEqualityTests {

        @Test
        @DisplayName("Emails with same value should be equal")
        void emailsWithSameValueShouldBeEqual() {
            Email email1 = Email.of("test@example.com");
            Email email2 = Email.of("test@example.com");
            assertEquals(email1, email2);
        }

        @Test
        @DisplayName("Emails with different values should not be equal")
        void emailsWithDifferentValuesShouldNotBeEqual() {
            Email email1 = Email.of("test1@example.com");
            Email email2 = Email.of("test2@example.com");
            assertNotEquals(email1, email2);
        }

        @Test
        @DisplayName("Should handle case insensitivity in equality")
        void shouldHandleCaseInsensitivityInEquality() {
            Email email1 = Email.of("TEST@EXAMPLE.COM");
            Email email2 = Email.of("test@example.com");
            assertEquals(email1, email2);
        }
    }
}
