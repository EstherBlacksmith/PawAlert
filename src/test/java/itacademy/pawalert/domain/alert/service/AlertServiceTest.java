package itacademy.pawalert.domain.alert.service;

import itacademy.pawalert.domain.alert.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AlertFactory Tests")
class AlertFactoryTest {

    private UUID petId;
    private UserId userId;
    private Title title;
    private Description description;

    @BeforeEach
    void setUp() {
        petId = UUID.randomUUID();
        userId = new UserId(UUID.randomUUID().toString());
        title = new Title("Lost Dog");
        description = new Description("Golden Retriever, friendly");
    }

    @Nested
    @DisplayName("createAlert Tests")
    class CreateAlertTests {

        @Test
        @DisplayName("Should create alert with OPENED status")
        void shouldCreateAlertWithOpenedStatus() {
            // When
            Alert alert = AlertFactory.createAlert(petId, userId, title, description);

            // Then
            assertEquals(StatusNames.OPENED, alert.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("Should generate unique ID for each alert")
        void shouldGenerateUniqueIds() {
            // When
            Alert alert1 = AlertFactory.createAlert(petId, userId, title, description);
            Alert alert2 = AlertFactory.createAlert(petId, userId, title, description);

            // Then
            assertNotEquals(alert1.getId(), alert2.getId());
        }

        @Test
        @DisplayName("Should preserve all provided fields")
        void shouldPreserveAllFields() {
            // When
            Alert alert = AlertFactory.createAlert(petId, userId, title, description);

            // Then
            assertEquals(petId, alert.getPetId());
            assertEquals(userId, alert.getUserID());
            assertEquals(title, alert.getTitle());
            assertEquals(description, alert.getDescription());
        }
    }

    @Nested
    @DisplayName("markAsSeen Tests")
    class MarkAsSeenTests {

        @Test
        @DisplayName("Should transition from OPENED to SEEN")
        void shouldTransitionFromOpenedToSeen() {
            // Given
            Alert alert = AlertFactory.createAlert(petId, userId, title, description);
            assertEquals(StatusNames.OPENED, alert.currentStatus().getStatusName());

            // When
            Alert seenAlert = AlertFactory.markAsSeen(alert);

            // Then
            assertEquals(StatusNames.SEEN, seenAlert.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("Should preserve all fields during transition")
        void shouldPreserveFieldsWhenMarkingAsSeen() {
            // Given
            Alert alert = AlertFactory.createAlert(petId, userId, title, description);

            // When
            Alert seenAlert = AlertFactory.markAsSeen(alert);

            // Then
            assertEquals(alert.getId(), seenAlert.getId());
            assertEquals(alert.getPetId(), seenAlert.getPetId());
            assertEquals(alert.getUserID(), seenAlert.getUserID());
            assertEquals(alert.getTitle(), seenAlert.getTitle());
            assertEquals(alert.getDescription(), seenAlert.getDescription());
        }
    }

    @Nested
    @DisplayName("markAsSafe Tests")
    class MarkAsSafeTests {

        @Test
        @DisplayName("Should transition from OPENED to SAFE directly")
        void shouldTransitionFromOpenedToSafe() {
            // Given
            Alert alert = AlertFactory.createAlert(petId, userId, title, description);

            // When
            Alert safeAlert = AlertFactory.markAsSafe(alert);

            // Then
            assertEquals(StatusNames.SAFE, safeAlert.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("Should transition from SEEN to SAFE")
        void shouldTransitionFromSeenToSafe() {
            // Given
            Alert alert = AlertFactory.createAlert(petId, userId, title, description);
            Alert seenAlert = AlertFactory.markAsSeen(alert);
            assertEquals(StatusNames.SEEN, seenAlert.currentStatus().getStatusName());

            // When
            Alert safeAlert = AlertFactory.markAsSafe(seenAlert);

            // Then
            assertEquals(StatusNames.SAFE, safeAlert.currentStatus().getStatusName());
        }
    }

    @Nested
    @DisplayName("markAsClose Tests")
    class MarkAsCloseTests {

        @Test
        @DisplayName("Should transition from OPENED to CLOSED directly")
        void shouldTransitionFromOpenedToClosed() {
            // Given
            Alert alert = AlertFactory.createAlert(petId, userId, title, description);

            // When
            Alert closedAlert = AlertFactory.markAsClose(alert);

            // Then
            assertEquals(StatusNames.CLOSED, closedAlert.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("Should transition from SEEN to CLOSED")
        void shouldTransitionFromSeenToClosed() {
            // Given
            Alert alert = AlertFactory.createAlert(petId, userId, title, description);
            Alert seenAlert = AlertFactory.markAsSeen(alert);

            // When
            Alert closedAlert = AlertFactory.markAsClose(seenAlert);

            // Then
            assertEquals(StatusNames.CLOSED, closedAlert.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("Should transition from SAFE to CLOSED")
        void shouldTransitionFromSafeToClosed() {
            // Given
            Alert alert = AlertFactory.createAlert(petId, userId, title, description);
            Alert safeAlert = AlertFactory.markAsSafe(alert);

            // When
            Alert closedAlert = AlertFactory.markAsClose(safeAlert);

            // Then
            assertEquals(StatusNames.CLOSED, closedAlert.currentStatus().getStatusName());
        }
    }

    @Nested
    @DisplayName("Full Lifecycle Tests")
    class FullLifecycleTests {

        @Test
        @DisplayName("Should complete full lifecycle: OPENED → SEEN → SAFE → CLOSED")
        void shouldCompleteFullLifecycle() {
            // Given
            Alert alert = AlertFactory.createAlert(petId, userId, title, description);

            // When & Then: OPENED → SEEN
            Alert seenAlert = AlertFactory.markAsSeen(alert);
            assertEquals(StatusNames.SEEN, seenAlert.currentStatus().getStatusName());

            // When & Then: SEEN → SAFE
            Alert safeAlert = AlertFactory.markAsSafe(seenAlert);
            assertEquals(StatusNames.SAFE, safeAlert.currentStatus().getStatusName());

            // When & Then: SAFE → CLOSED
            Alert closedAlert = AlertFactory.markAsClose(safeAlert);
            assertEquals(StatusNames.CLOSED, closedAlert.currentStatus().getStatusName());

            // Verify ID is preserved throughout
            assertEquals(alert.getId(), seenAlert.getId());
            assertEquals(alert.getId(), safeAlert.getId());
            assertEquals(alert.getId(), closedAlert.getId());
        }
    }
}
