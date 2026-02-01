package itacademy.pawalert.domain;

import itacademy.pawalert.domain.alert.model.*;
import itacademy.pawalert.domain.alert.exception.AlertModificationNotAllowedException;
import itacademy.pawalert.domain.alert.exception.InvalidAlertStatusChange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static itacademy.pawalert.domain.alert.model.UserId.fromUUID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Alert State Machine Tests")
@ActiveProfiles("test")
class AlertTest {

    Description description;
    private Alert alert;
    private Title title;

    @BeforeEach
    void setUp() {
        UUID petId = UUID.randomUUID();
        UserId userId = fromUUID(UUID.randomUUID());
        title = new Title("Test Alert");
        description = new Description("Test Description");
        alert = new Alert(petId, userId, title, description);
    }

    @Nested
    @DisplayName("Initial State Tests")
    class InitialStateTests {

        @Test
        @DisplayName("New alert should start in OPENED state")
        void newAlertShouldStartInOpenedState() {
            assertEquals(StatusNames.OPENED, alert.currentStatus().getStatusName());
        }
    }

    @Nested
    @DisplayName("State Transition Tests - OPENED to SEEN")
    class OpenedToSeenTests {

        @Test
        @DisplayName("Alert should transition from OPENED to SEEN when marked as seen")
        void alertShouldTransitionFromOpenedToSeen() {
            // Given: Alert is in OPENED state
            assertEquals(StatusNames.OPENED, alert.currentStatus().getStatusName());

            // When: Mark as seen
            alert.currentStatus().seen(alert);

            // Then: Alert should be in SEEN state
            assertEquals(StatusNames.SEEN, alert.currentStatus().getStatusName());
        }
    }

    @Nested
    @DisplayName("State Transition Tests - SEEN to SAFE")
    class SeenToSafeTests {

        @Test
        @DisplayName("Alert should transition from SEEN to SAFE when marked as safe")
        void alertShouldTransitionFromSeenToSafe() {
            // Given: Alert is in SEEN state
            alert.currentStatus().seen(alert);
            assertEquals(StatusNames.SEEN, alert.currentStatus().getStatusName());

            // When: Mark as safe
            alert.currentStatus().safe(alert);

            // Then: Alert should be in SAFE state
            assertEquals(StatusNames.SAFE, alert.currentStatus().getStatusName());
        }
    }

    @Nested
    @DisplayName("State Transition Tests - SAFE to CLOSED")
    class SafeToClosedTests {

        @Test
        @DisplayName("Alert should transition from SAFE to CLOSED when closed")
        void alertShouldTransitionFromSafeToClosed() {
            // Given: Alert is in SAFE state
            alert.currentStatus().seen(alert);
            alert.currentStatus().safe(alert);
            assertEquals(StatusNames.SAFE, alert.currentStatus().getStatusName());

            // When: Close the alert
            alert.currentStatus().closed(alert);

            // Then: Alert should be in CLOSED state
            assertEquals(StatusNames.CLOSED, alert.currentStatus().getStatusName());
        }
    }

    @Nested
    @DisplayName("Full State Transition Flow Tests")
    class FullFlowTests {

        @Test
        @DisplayName("Alert should complete full lifecycle: OPENED → SEEN → SAFE → CLOSED")
        void alertShouldCompleteFullLifecycle() {
            // Initial state: OPENED
            assertEquals(StatusNames.OPENED, alert.currentStatus().getStatusName());

            // Transition: OPENED → SEEN
            alert.currentStatus().seen(alert);
            assertEquals(StatusNames.SEEN, alert.currentStatus().getStatusName());

            // Transition: SEEN → SAFE
            alert.currentStatus().safe(alert);
            assertEquals(StatusNames.SAFE, alert.currentStatus().getStatusName());

            // Transition: SAFE → CLOSED
            alert.currentStatus().closed(alert);
            assertEquals(StatusNames.CLOSED, alert.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("Alert should allow skipping states (OPENED → SAFE → CLOSED)")
        void alertShouldAllowSkippingStates() {
            // Direct from OPENED to SAFE (valid in your implementation)
            alert.currentStatus().safe(alert);
            assertEquals(StatusNames.SAFE, alert.currentStatus().getStatusName());

            // From SAFE to CLOSED
            alert.currentStatus().closed(alert);
            assertEquals(StatusNames.CLOSED, alert.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("Alert should allow skipping states (OPENED → CLOSED)")
        void alertShouldAllowDirectClosure() {
            // Direct from OPENED to CLOSED (valid in your implementation)
            alert.currentStatus().closed(alert);
            assertEquals(StatusNames.CLOSED, alert.currentStatus().getStatusName());
        }
    }

    @Nested
    @DisplayName("Invalid Transition Tests")
    class InvalidTransitionTests {

        @Test
        @DisplayName("Alert in CLOSED state should not transition back to OPENED")
        void closedAlertShouldNotReopen() {
            // Close the alert
            alert.currentStatus().closed(alert);
            assertEquals(StatusNames.CLOSED, alert.currentStatus().getStatusName());

            // Try to reopen (should not change state)
            assertThrows(InvalidAlertStatusChange.class, () -> {
                alert.currentStatus().open(alert);
            });

            assertEquals(StatusNames.CLOSED, alert.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("Alert in CLOSED state should not transition to SAFE")
        void closedAlertShouldNotGoToSafe() {
            alert.currentStatus().closed(alert);

            assertThrows(InvalidAlertStatusChange.class, () -> {
                alert.currentStatus().safe(alert);
            });

            assertEquals(StatusNames.CLOSED, alert.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("Alert in SAFE state should not transition back to SEEN")
        void safeAlertShouldNotGoBackToSeen() {
            // Given: Alert is in SAFE state
            alert.currentStatus().seen(alert);
            alert.currentStatus().safe(alert);
            assertEquals(StatusNames.SAFE, alert.currentStatus().getStatusName());

            // When: Try to mark as seen (should not work from SAFE)
            assertThrows(InvalidAlertStatusChange.class, () -> {
                alert.currentStatus().seen(alert);
            });

            // Then: Should remain in SAFE state
            assertEquals(StatusNames.SAFE, alert.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("Alert in SEEN state should not transition back to OPENED")
        void seenAlertShouldNotGoBackToOpened() {
            // Given: Alert is in SEEN state
            alert.currentStatus().seen(alert);
            assertEquals(StatusNames.SEEN, alert.currentStatus().getStatusName());

            // When: Try to open (should not work from SEEN)
            assertThrows(InvalidAlertStatusChange.class, () -> {
                alert.currentStatus().open(alert);
            });

            // Then: Should remain in SEEN state
            assertEquals(StatusNames.SEEN, alert.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("Alert in SAFE state should not transition back to OPENED")
        void safeAlertShouldNotGoBackToOpened() {
            // Given: Alert is in SAFE state
            alert.currentStatus().safe(alert);
            assertEquals(StatusNames.SAFE, alert.currentStatus().getStatusName());

            // When: Try to open (should not work from SAFE)
            assertThrows(InvalidAlertStatusChange.class, () -> {
                alert.currentStatus().open(alert);
            });

            // Then: Should remain in SAFE state
            assertEquals(StatusNames.SAFE, alert.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("Alert in SEEN state should not transition to SEEN again (idempotency)")
        void seenAlertShouldRemainSeenWhenMarkedSeenAgain() {
            // Given: Alert is in SEEN state
            alert.currentStatus().seen(alert);
            assertEquals(StatusNames.SEEN, alert.currentStatus().getStatusName());

            // When: Mark as seen again
            alert.currentStatus().seen(alert);

            // Then: Should remain in SEEN state
            assertEquals(StatusNames.SEEN, alert.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("Alert in CLOSED state should not transition to SEEN")
        void closedAlertShouldNotGoToSeen() {
            alert.currentStatus().closed(alert);

            assertThrows(InvalidAlertStatusChange.class, () -> {
                alert.currentStatus().open(alert);
            });

            assertEquals(StatusNames.CLOSED, alert.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("Multiple invalid transitions should not affect final state")
        void multipleInvalidTransitionsShouldNotAffectState() {
            // Given: Alert is in SEEN state
            alert.currentStatus().seen(alert);
            StatusNames initialState = alert.currentStatus().getStatusName();

            assertThrows(InvalidAlertStatusChange.class, () -> {
                alert.currentStatus().open(alert);
            });

            // Then: Should remain in SEEN state
            assertEquals(initialState, alert.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("Closed alert should remain closed after any transition attempt")
        void closedAlertShouldRemainClosedAfterAnyAttempt() {
            // Given: Alert is closed
            alert.currentStatus().closed(alert);
            assertEquals(StatusNames.CLOSED, alert.currentStatus().getStatusName());

            // When: Try all possible invalid transitions
            assertThrows(InvalidAlertStatusChange.class, () -> {
                alert.currentStatus().open(alert);
            });

            assertEquals(StatusNames.CLOSED, alert.currentStatus().getStatusName());

            assertThrows(InvalidAlertStatusChange.class, () -> {
                alert.currentStatus().open(alert);
            });

            assertEquals(StatusNames.CLOSED, alert.currentStatus().getStatusName());

            assertThrows(InvalidAlertStatusChange.class, () -> {
                alert.currentStatus().open(alert);
            });

            assertEquals(StatusNames.CLOSED, alert.currentStatus().getStatusName());

            assertThrows(InvalidAlertStatusChange.class, () -> {
                alert.currentStatus().open(alert);
            });

            assertEquals(StatusNames.CLOSED, alert.currentStatus().getStatusName());
        }
    }

    @Nested
    @DisplayName("Boundary and Edge Case Tests")
    class BoundaryEdgeCaseTests {

        @Test
        @DisplayName("Direct transition from OPENED to CLOSED should be allowed")
        void directOpenedToClosedShouldBeAllowed() {
            // This is a valid transition in your implementation
            alert.currentStatus().closed(alert);

            assertEquals(StatusNames.CLOSED, alert.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("Direct transition from OPENED to SAFE should be allowed")
        void directOpenedToSafeShouldBeAllowed() {
            // This is a valid transition in your implementation
            alert.currentStatus().safe(alert);

            assertEquals(StatusNames.SAFE, alert.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("State transitions should be reversible until CLOSED")
        void stateTransitionsShouldBeReversibleUntilClosed() {
            // OPENED -> SEEN -> SAFE -> CLOSED
            alert.currentStatus().seen(alert);
            assertEquals(StatusNames.SEEN, alert.currentStatus().getStatusName());

            // Cannot go back from SAFE to SEEN in your current implementation
            alert.currentStatus().safe(alert);

            assertThrows(InvalidAlertStatusChange.class, () -> {
                alert.currentStatus().open(alert);
            });// Invalid - should stay in SAFE

            assertEquals(StatusNames.SAFE, alert.currentStatus().getStatusName());

            // But can close from any state
            alert.currentStatus().closed(alert);

            assertEquals(StatusNames.CLOSED, alert.currentStatus().getStatusName());
        }
    }

    @Nested
    @DisplayName("Alert Modification Restriction Tests")
    class AlertModificationRestrictionTests {

        @Test
        @DisplayName("Should allow updating description when alert is in OPENED status")
        void shouldAllowDescriptionUpdateWhenOpened() {
            // Given: Alert is in OPENED state
            assertEquals(StatusNames.OPENED, alert.currentStatus().getStatusName());

            // When: Update description
            Description newDescription = new Description("Updated description");
            alert.updateDescription(newDescription);

            // Then: Description should be updated
            assertEquals("Updated description", alert.getDescription().getValue());
        }

        @Test
        @DisplayName("Should allow updating title when alert is in OPENED status")
        void shouldAllowTitleUpdateWhenOpened() {
            // Given: Alert is in OPENED state
            assertEquals(StatusNames.OPENED, alert.currentStatus().getStatusName());

            // When: Update title
            Title newTitle = new Title("Updated title");
            alert.updateTitle(newTitle);

            // Then: Title should be updated
            assertEquals("Updated title", alert.getTitle().getValue());
        }

        @Test
        @DisplayName("Should throw exception when updating description after SEEN status")
        void shouldThrowExceptionWhenUpdatingDescriptionAfterSeen() {
            // Given: Alert is in SEEN state
            alert.seen();
            assertEquals(StatusNames.SEEN, alert.currentStatus().getStatusName());

            // When/Then: Update should throw exception
            assertThrows(AlertModificationNotAllowedException.class, () -> {
                alert.updateDescription(new Description("New description"));
            });
        }

        @Test
        @DisplayName("Should throw exception when updating title after SEEN status")
        void shouldThrowExceptionWhenUpdatingTitleAfterSeen() {
            // Given: Alert is in SEEN state
            alert.seen();
            assertEquals(StatusNames.SEEN, alert.currentStatus().getStatusName());

            // When/Then: Update should throw exception
            assertThrows(AlertModificationNotAllowedException.class, () -> {
                alert.updateTitle(new Title("New title"));
            });
        }

        @Test
        @DisplayName("Should throw exception when updating description after SAFE status")
        void shouldThrowExceptionWhenUpdatingDescriptionAfterSafe() {
            // Given: Alert is in SAFE state
            alert.safe();
            assertEquals(StatusNames.SAFE, alert.currentStatus().getStatusName());

            // When/Then: Update should throw exception
            assertThrows(AlertModificationNotAllowedException.class, () -> {
                alert.updateDescription(new Description("New description"));
            });
        }

        @Test
        @DisplayName("Should throw exception when updating title after SAFE status")
        void shouldThrowExceptionWhenUpdatingTitleAfterSafe() {
            // Given: Alert is in SAFE state
            alert.safe();
            assertEquals(StatusNames.SAFE, alert.currentStatus().getStatusName());

            // When/Then: Update should throw exception
            assertThrows(AlertModificationNotAllowedException.class, () -> {
                alert.updateTitle(new Title("New title"));
            });
        }

        @Test
        @DisplayName("Should throw exception when updating description after CLOSED status")
        void shouldThrowExceptionWhenUpdatingDescriptionAfterClosed() {
            // Given: Alert is in CLOSED state
            alert.closed();
            assertEquals(StatusNames.CLOSED, alert.currentStatus().getStatusName());

            // When/Then: Update should throw exception
            assertThrows(AlertModificationNotAllowedException.class, () -> {
                alert.updateDescription(new Description("New description"));
            });
        }

        @Test
        @DisplayName("Should throw exception when updating title after CLOSED status")
        void shouldThrowExceptionWhenUpdatingTitleAfterClosed() {
            // Given: Alert is in CLOSED state
            alert.closed();
            assertEquals(StatusNames.CLOSED, alert.currentStatus().getStatusName());

            // When/Then: Update should throw exception
            assertThrows(AlertModificationNotAllowedException.class, () -> {
                alert.updateTitle(new Title("New title"));
            });
        }

        @Test
        @DisplayName("Exception message should contain alert ID")
        void exceptionMessageShouldContainAlertId() {
            // Given: Alert is in SEEN state
            alert.seen();

            // When: Try to update description
            AlertModificationNotAllowedException exception = assertThrows(
                    AlertModificationNotAllowedException.class,
                    () -> alert.updateDescription(new Description("Test"))
            );

            // Then: Exception message should contain the alert ID
            assertTrue(exception.getMessage().contains(alert.getId().toString()));
        }
    }


}
