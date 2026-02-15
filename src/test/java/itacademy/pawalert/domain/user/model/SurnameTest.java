package itacademy.pawalert.domain.user.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Surname Tests")
class SurnameTest {

    // ═══════════════════════════════════════════════════════════════════════
    // Valid Surname Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Valid Surname Tests")
    class ValidSurnameTests {

        @Test
        @DisplayName("Should create surname with valid input")
        void shouldCreateSurnameWithValidInput() {
            Surname surname = Surname.of("Garcia");
            assertEquals("Garcia", surname.value());
        }

        @Test
        @DisplayName("Should handle compound surname with space")
        void shouldHandleCompoundSurnameWithSpace() {
            Surname surname = Surname.of("Garcia Lopez");
            assertEquals("Garcia Lopez", surname.value());
        }

        @Test
        @DisplayName("Should handle compound surname with hyphen")
        void shouldHandleCompoundSurnameWithHyphen() {
            Surname surname = Surname.of("Garcia-Lopez");
            assertEquals("Garcia-Lopez", surname.value());
        }

        @Test
        @DisplayName("Should handle compound surname with apostrophe")
        void shouldHandleCompoundSurnameWithApostrophe() {
            Surname surname = Surname.of("O'Connor");
            assertEquals("O'Connor", surname.value());
        }

        @Test
        @DisplayName("Should handle Spanish characters")
        void shouldHandleSpanishCharacters() {
            Surname surname = Surname.of("García");
            assertEquals("García", surname.value());
        }

        @Test
        @DisplayName("Should handle surname at maximum length")
        void shouldHandleSurnameAtMaxLength() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 200; i++) sb.append("a");
            Surname surname = Surname.of(sb.toString());
            assertEquals(200, surname.value().length());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Invalid Surname Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Invalid Surname Tests")
    class InvalidSurnameTests {

        @Test
        @DisplayName("Should throw for null surname")
        void shouldThrowForNullSurname() {
            assertThrows(IllegalArgumentException.class, () -> Surname.of(null));
        }

        @Test
        @DisplayName("Should throw for empty surname")
        void shouldThrowForEmptySurname() {
            assertThrows(IllegalArgumentException.class, () -> Surname.of(""));
        }

        @Test
        @DisplayName("Should throw for blank surname")
        void shouldThrowForBlankSurname() {
            assertThrows(IllegalArgumentException.class, () -> Surname.of("   "));
        }

        @Test
        @DisplayName("Should throw for surname too long")
        void shouldThrowForSurnameTooLong() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 201; i++) sb.append("a");
            assertThrows(IllegalArgumentException.class, () -> Surname.of(sb.toString()));
        }

        @Test
        @DisplayName("Should throw for surname with numbers")
        void shouldThrowForSurnameWithNumbers() {
            assertThrows(IllegalArgumentException.class, () -> Surname.of("Garcia123"));
        }

        @Test
        @DisplayName("Should throw for surname with special characters")
        void shouldThrowForSurnameWithSpecialChars() {
            assertThrows(IllegalArgumentException.class, () -> Surname.of("Garcia@"));
        }

        @Test
        @DisplayName("Should throw for surname with brackets")
        void shouldThrowForSurnameWithBrackets() {
            assertThrows(IllegalArgumentException.class, () -> Surname.of("Garcia( Jr)"));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Surname Helper Methods Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Surname Helper Methods Tests")
    class SurnameHelperMethodsTests {

        @Test
        @DisplayName("isCompound should return true for surname with space")
        void isCompoundShouldReturnTrueForSpace() {
            Surname surname = Surname.of("Garcia Lopez");
            assertTrue(surname.isCompound());
        }

        @Test
        @DisplayName("isCompound should return true for surname with hyphen")
        void isCompoundShouldReturnTrueForHyphen() {
            Surname surname = Surname.of("Garcia-Lopez");
            assertTrue(surname.isCompound());
        }

        @Test
        @DisplayName("isCompound should return true for surname with apostrophe")
        void isCompoundShouldReturnTrueForApostrophe() {
            Surname surname = Surname.of("O'Brien");
            assertTrue(surname.isCompound());
        }

        @Test
        @DisplayName("isCompound should return false for simple surname")
        void isCompoundShouldReturnFalseForSimple() {
            Surname surname = Surname.of("Garcia");
            assertFalse(surname.isCompound());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Surname Factory Method Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Surname Factory Method Tests")
    class SurnameFactoryTests {

        @Test
        @DisplayName("Factory method should create surname successfully")
        void factoryMethodShouldCreateSurname() {
            Surname surname = Surname.of("Garcia");
            assertNotNull(surname);
            assertEquals("Garcia", surname.value());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Surname Equality Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Surname Equality Tests")
    class SurnameEqualityTests {

        @Test
        @DisplayName("Surnames with same value should be equal")
        void surnamesWithSameValueShouldBeEqual() {
            Surname surname1 = Surname.of("Garcia");
            Surname surname2 = Surname.of("Garcia");
            assertEquals(surname1, surname2);
        }

        @Test
        @DisplayName("Surnames with different values should not be equal")
        void surnamesWithDifferentValuesShouldNotBeEqual() {
            Surname surname1 = Surname.of("Garcia");
            Surname surname2 = Surname.of("Lopez");
            assertNotEquals(surname1, surname2);
        }
    }
}
