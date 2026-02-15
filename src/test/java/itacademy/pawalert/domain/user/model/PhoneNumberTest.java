package itacademy.pawalert.domain.user.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PhoneNumber Tests")
class PhoneNumberTest {

    // ═══════════════════════════════════════════════════════════════════════
    // Valid PhoneNumber Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Valid PhoneNumber Tests")
    class ValidPhoneNumberTests {

        @Test
        @DisplayName("Should create phone number with valid input")
        void shouldCreatePhoneNumberWithValidInput() {
            PhoneNumber phoneNumber = PhoneNumber.of("612345678");
            assertEquals("612345678", phoneNumber.value());
        }

        @Test
        @DisplayName("Should handle phone number with country code")
        void shouldHandlePhoneNumberWithCountryCode() {
            PhoneNumber phoneNumber = PhoneNumber.of("+34612345678");
            assertEquals("+34612345678", phoneNumber.value());
        }

        @Test
        @DisplayName("Should handle phone number with spaces")
        void shouldHandlePhoneNumberWithSpaces() {
            PhoneNumber phoneNumber = PhoneNumber.of("612 345 678");
            assertEquals("612 345 678", phoneNumber.value());
        }

        @Test
        @DisplayName("Should handle phone number with parentheses")
        void shouldHandlePhoneNumberWithParentheses() {
            PhoneNumber phoneNumber = PhoneNumber.of("(612) 345678");
            assertEquals("(612) 345678", phoneNumber.value());
        }

        @Test
        @DisplayName("Should handle phone number with hyphen")
        void shouldHandlePhoneNumberWithHyphen() {
            PhoneNumber phoneNumber = PhoneNumber.of("612-345-678");
            assertEquals("612-345-678", phoneNumber.value());
        }

        @Test
        @DisplayName("Should handle phone number at minimum length")
        void shouldHandlePhoneNumberAtMinLength() {
            PhoneNumber phoneNumber = PhoneNumber.of("1234567");
            assertEquals("1234567", phoneNumber.value());
        }

        @Test
        @DisplayName("Should handle phone number at maximum length")
        void shouldHandlePhoneNumberAtMaxLength() {
            PhoneNumber phoneNumber = PhoneNumber.of("123456789012345");
            assertEquals("123456789012345", phoneNumber.value());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Invalid PhoneNumber Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Invalid PhoneNumber Tests")
    class InvalidPhoneNumberTests {

        @Test
        @DisplayName("Should throw for null phone number")
        void shouldThrowForNullPhoneNumber() {
            assertThrows(IllegalArgumentException.class, () -> PhoneNumber.of(null));
        }

        @Test
        @DisplayName("Should throw for empty phone number")
        void shouldThrowForEmptyPhoneNumber() {
            assertThrows(IllegalArgumentException.class, () -> PhoneNumber.of(""));
        }

        @Test
        @DisplayName("Should throw for blank phone number")
        void shouldThrowForBlankPhoneNumber() {
            assertThrows(IllegalArgumentException.class, () -> PhoneNumber.of("   "));
        }

        @Test
        @DisplayName("Should throw for phone number too short")
        void shouldThrowForPhoneNumberTooShort() {
            assertThrows(IllegalArgumentException.class, () -> PhoneNumber.of("123456"));
        }

        @Test
        @DisplayName("Should throw for phone number too long")
        void shouldThrowForPhoneNumberTooLong() {
            assertThrows(IllegalArgumentException.class, () -> PhoneNumber.of("1234567890123456"));
        }

        @Test
        @DisplayName("Should throw for phone number with letters")
        void shouldThrowForPhoneNumberWithLetters() {
            assertThrows(IllegalArgumentException.class, () -> PhoneNumber.of("612abc456"));
        }

        @Test
        @DisplayName("Should throw for phone number with special characters")
        void shouldThrowForPhoneNumberWithSpecialChars() {
            assertThrows(IllegalArgumentException.class, () -> PhoneNumber.of("612@345#678"));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PhoneNumber Helper Methods Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("PhoneNumber Helper Methods Tests")
    class PhoneNumberHelperMethodsTests {

        @Test
        @DisplayName("isMobile should return true for mobile starting with 6")
        void isMobileShouldReturnTrueFor6() {
            PhoneNumber phoneNumber = PhoneNumber.of("612345678");
            assertTrue(phoneNumber.isMobile());
        }

        @Test
        @DisplayName("isMobile should return true for mobile starting with 7")
        void isMobileShouldReturnTrueFor7() {
            PhoneNumber phoneNumber = PhoneNumber.of("712345678");
            assertTrue(phoneNumber.isMobile());
        }

        @Test
        @DisplayName("isMobile should return false for landline starting with 9")
        void isMobileShouldReturnFalseForLandline() {
            PhoneNumber phoneNumber = PhoneNumber.of("912345678");
            assertFalse(phoneNumber.isMobile());
        }

        @Test
        @DisplayName("getCountryCode should return country code with +")
        void getCountryCodeShouldReturnCodeWithPlus() {
            PhoneNumber phoneNumber = PhoneNumber.of("+34612345678");
            assertEquals("+34", phoneNumber.getCountryCode());
        }

        @Test
        @DisplayName("getCountryCode should return empty for numbers without +")
        void getCountryCodeShouldReturnEmptyForNoPlus() {
            PhoneNumber phoneNumber = PhoneNumber.of("612345678");
            assertEquals("", phoneNumber.getCountryCode());
        }

        @Test
        @DisplayName("getNationalNumber should return number without country code")
        void getNationalNumberShouldReturnWithoutCode() {
            PhoneNumber phoneNumber = PhoneNumber.of("+34612345678");
            assertEquals("612345678", phoneNumber.getNationalNumber());
        }

        @Test
        @DisplayName("getNationalNumber should return full number without +")
        void getNationalNumberShouldReturnFullWithoutPlus() {
            PhoneNumber phoneNumber = PhoneNumber.of("612345678");
            assertEquals("612345678", phoneNumber.getNationalNumber());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PhoneNumber Factory Method Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("PhoneNumber Factory Method Tests")
    class PhoneNumberFactoryTests {

        @Test
        @DisplayName("Factory method should create phone number successfully")
        void factoryMethodShouldCreatePhoneNumber() {
            PhoneNumber phoneNumber = PhoneNumber.of("612345678");
            assertNotNull(phoneNumber);
            assertEquals("612345678", phoneNumber.value());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PhoneNumber Equality Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("PhoneNumber Equality Tests")
    class PhoneNumberEqualityTests {

        @Test
        @DisplayName("PhoneNumbers with same value should be equal")
        void phoneNumbersWithSameValueShouldBeEqual() {
            PhoneNumber phoneNumber1 = PhoneNumber.of("612345678");
            PhoneNumber phoneNumber2 = PhoneNumber.of("612345678");
            assertEquals(phoneNumber1, phoneNumber2);
        }

        @Test
        @DisplayName("PhoneNumbers with different values should not be equal")
        void phoneNumbersWithDifferentValuesShouldNotBeEqual() {
            PhoneNumber phoneNumber1 = PhoneNumber.of("612345678");
            PhoneNumber phoneNumber2 = PhoneNumber.of("612345679");
            assertNotEquals(phoneNumber1, phoneNumber2);
        }
    }
}
