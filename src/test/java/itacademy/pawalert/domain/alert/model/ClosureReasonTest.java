package itacademy.pawalert.domain.alert.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ClosureReason Tests")
class ClosureReasonTest {

    @Test
    @DisplayName("Should create closure reason with valid value")
    void shouldCreateClosureReasonWithValidValue() {
        ClosureReason reason = ClosureReason.fromString("FOUNDED");
        assertEquals("FOUNDED", reason.toString());
    }

    @Test
    @DisplayName("Should throw exception for empty reason")
    void shouldThrowExceptionForEmptyReason() {
        assertThrows(IllegalArgumentException.class,
                () -> ClosureReason.fromString(""));
    }

    @Test
    @DisplayName("Should throw exception for null reason")
    void shouldThrowExceptionForNullReason() {
        assertThrows(IllegalArgumentException.class,
                () -> ClosureReason.fromString(null));
    }
}
