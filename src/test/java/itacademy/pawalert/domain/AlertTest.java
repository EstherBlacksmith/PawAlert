package itacademy.pawalert.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Alert State Machine Tests")
class AlertTest {

    private Alert alert;
    private Tittle tittle;
    Description description;

    @BeforeEach
    void setUp() {
        tittle = new Tittle("Test Alert");
        description = new Description("Test Description");
        alert = new Alert(tittle, description);
    }

    @Nested
    @DisplayName("Initial State Tests")
    class InitialStateTests {

        @Test
        @DisplayName("New alert should start in OPENED state")
        void newAlertShouldStartInOpenedState() {
            assertEquals(StatusNames.OPENED, alert.currentState().getStateName());
        }
    }

    @Nested
    @DisplayName("State Transition Tests - OPENED to SEEN")
    class OpenedToSeenTests {

        @Test
        @DisplayName("Alert should transition from OPENED to SEEN when marked as seen")
        void alertShouldTransitionFromOpenedToSeen() {
            // Given: Alert is in OPENED state
            assertEquals(StatusNames.OPENED, alert.currentState().getStateName());

            // When: Mark as seen
            alert.currentState().seen(alert);

            // Then: Alert should be in SEEN state
            assertEquals(StatusNames.SEEN, alert.currentState().getStateName());
        }
    }

    @Nested
    @DisplayName("State Transition Tests - SEEN to SAFE")
    class SeenToSafeTests {

        @Test
        @DisplayName("Alert should transition from SEEN to SAFE when marked as safe")
        void alertShouldTransitionFromSeenToSafe() {
            // Given: Alert is in SEEN state
            alert.currentState().seen(alert);
            assertEquals(StatusNames.SEEN, alert.currentState().getStateName());

            // When: Mark as safe
            alert.currentState().safe(alert);

            // Then: Alert should be in SAFE state
            assertEquals(StatusNames.SAFE, alert.currentState().getStateName());
        }
    }

    @Nested
    @DisplayName("State Transition Tests - SAFE to CLOSED")
    class SafeToClosedTests {

        @Test
        @DisplayName("Alert should transition from SAFE to CLOSED when closed")
        void alertShouldTransitionFromSafeToClosed() {
            // Given: Alert is in SAFE state
            alert.currentState().seen(alert);
            alert.currentState().safe(alert);
            assertEquals(StatusNames.SAFE, alert.currentState().getStateName());

            // When: Close the alert
            alert.currentState().closed(alert);

            // Then: Alert should be in CLOSED state
            assertEquals(StatusNames.CLOSED, alert.currentState().getStateName());
        }
    }

    @Nested
    @DisplayName("Full State Transition Flow Tests")
    class FullFlowTests {

        @Test
        @DisplayName("Alert should complete full lifecycle: OPENED → SEEN → SAFE → CLOSED")
        void alertShouldCompleteFullLifecycle() {
            // Initial state: OPENED
            assertEquals(StatusNames.OPENED, alert.currentState().getStateName());

            // Transition: OPENED → SEEN
            alert.currentState().seen(alert);
            assertEquals(StatusNames.SEEN, alert.currentState().getStateName());

            // Transition: SEEN → SAFE
            alert.currentState().safe(alert);
            assertEquals(StatusNames.SAFE, alert.currentState().getStateName());

            // Transition: SAFE → CLOSED
            alert.currentState().closed(alert);
            assertEquals(StatusNames.CLOSED, alert.currentState().getStateName());
        }

        @Test
        @DisplayName("Alert should allow skipping states (OPENED → SAFE → CLOSED)")
        void alertShouldAllowSkippingStates() {
            // Direct from OPENED to SAFE (valid in your implementation)
            alert.currentState().safe(alert);
            assertEquals(StatusNames.SAFE, alert.currentState().getStateName());

            // From SAFE to CLOSED
            alert.currentState().closed(alert);
            assertEquals(StatusNames.CLOSED, alert.currentState().getStateName());
        }

        @Test
        @DisplayName("Alert should allow skipping states (OPENED → CLOSED)")
        void alertShouldAllowDirectClosure() {
            // Direct from OPENED to CLOSED (valid in your implementation)
            alert.currentState().closed(alert);
            assertEquals(StatusNames.CLOSED, alert.currentState().getStateName());
        }
    }

    @Nested
    @DisplayName("Invalid Transition Tests")
    class InvalidTransitionTests {

        @Test
        @DisplayName("Alert in CLOSED state should not transition back to OPENED")
        void closedAlertShouldNotReopen() {
            // Close the alert
            alert.currentState().closed(alert);
            assertEquals(StatusNames.CLOSED, alert.currentState().getStateName());

            // Try to reopen (should not change state)
            alert.currentState().open(alert);
            assertEquals(StatusNames.CLOSED, alert.currentState().getStateName());
        }

        @Test
        @DisplayName("Alert in CLOSED state should not transition to SAFE")
        void closedAlertShouldNotGoToSafe() {
            alert.currentState().closed(alert);
            alert.currentState().safe(alert);
            assertEquals(StatusNames.CLOSED, alert.currentState().getStateName());
        }

        @Test
        @DisplayName("Alert in SAFE state should not transition back to SEEN")
        void safeAlertShouldNotGoBackToSeen() {
            // Given: Alert is in SAFE state
            alert.currentState().seen(alert);
            alert.currentState().safe(alert);
            assertEquals(StatusNames.SAFE, alert.currentState().getStateName());

            // When: Try to mark as seen (should not work from SAFE)
            alert.currentState().seen(alert);

            // Then: Should remain in SAFE state
            assertEquals(StatusNames.SAFE, alert.currentState().getStateName());
        }

        @Test
        @DisplayName("Alert in SEEN state should not transition back to OPENED")
        void seenAlertShouldNotGoBackToOpened() {
            // Given: Alert is in SEEN state
            alert.currentState().seen(alert);
            assertEquals(StatusNames.SEEN, alert.currentState().getStateName());

            // When: Try to open (should not work from SEEN)
            alert.currentState().open(alert);

            // Then: Should remain in SEEN state
            assertEquals(StatusNames.SEEN, alert.currentState().getStateName());
        }

        @Test
        @DisplayName("Alert in SAFE state should not transition back to OPENED")
        void safeAlertShouldNotGoBackToOpened() {
            // Given: Alert is in SAFE state
            alert.currentState().safe(alert);
            assertEquals(StatusNames.SAFE, alert.currentState().getStateName());

            // When: Try to open (should not work from SAFE)
            alert.currentState().open(alert);

            // Then: Should remain in SAFE state
            assertEquals(StatusNames.SAFE, alert.currentState().getStateName());
        }

        @Test
        @DisplayName("Alert in SEEN state should not transition to SEEN again (idempotency)")
        void seenAlertShouldRemainSeenWhenMarkedSeenAgain() {
            // Given: Alert is in SEEN state
            alert.currentState().seen(alert);
            assertEquals(StatusNames.SEEN, alert.currentState().getStateName());

            // When: Mark as seen again
            alert.currentState().seen(alert);

            // Then: Should remain in SEEN state
            assertEquals(StatusNames.SEEN, alert.currentState().getStateName());
        }

        @Test
        @DisplayName("Alert in CLOSED state should not transition to SEEN")
        void closedAlertShouldNotGoToSeen() {
            alert.currentState().closed(alert);
            alert.currentState().seen(alert);
            assertEquals(StatusNames.CLOSED, alert.currentState().getStateName());
        }

        @Test
        @DisplayName("Multiple invalid transitions should not affect final state")
        void multipleInvalidTransitionsShouldNotAffectState() {
            // Given: Alert is in SEEN state
            alert.currentState().seen(alert);
            StatusNames initialState = alert.currentState().getStateName();

            // When: Apply multiple invalid transitions
            alert.currentState().open(alert);      // Invalid: SEEN -> OPENED
            alert.currentState().seen(alert);      // Valid: SEEN -> SEEN (idempotent)
            alert.currentState().open(alert);      // Invalid again

            // Then: Should remain in SEEN state
            assertEquals(initialState, alert.currentState().getStateName());
        }

        @Test
        @DisplayName("Closed alert should remain closed after any transition attempt")
        void closedAlertShouldRemainClosedAfterAnyAttempt() {
            // Given: Alert is closed
            alert.currentState().closed(alert);
            assertEquals(StatusNames.CLOSED, alert.currentState().getStateName());

            // When: Try all possible invalid transitions
            alert.currentState().open(alert);
            assertEquals(StatusNames.CLOSED, alert.currentState().getStateName());

            alert.currentState().seen(alert);
            assertEquals(StatusNames.CLOSED, alert.currentState().getStateName());

            alert.currentState().safe(alert);
            assertEquals(StatusNames.CLOSED, alert.currentState().getStateName());

            alert.currentState().closed(alert);
            assertEquals(StatusNames.CLOSED, alert.currentState().getStateName());
        }
    }

    @Nested
    @DisplayName("Boundary and Edge Case Tests")
    class BoundaryEdgeCaseTests {

        @Test
        @DisplayName("Direct transition from OPENED to CLOSED should be allowed")
        void directOpenedToClosedShouldBeAllowed() {
            // This is a valid transition in your implementation
            alert.currentState().closed(alert);
            assertEquals(StatusNames.CLOSED, alert.currentState().getStateName());
        }

        @Test
        @DisplayName("Direct transition from OPENED to SAFE should be allowed")
        void directOpenedToSafeShouldBeAllowed() {
            // This is a valid transition in your implementation
            alert.currentState().safe(alert);
            assertEquals(StatusNames.SAFE, alert.currentState().getStateName());
        }

        @Test
        @DisplayName("State transitions should be reversible until CLOSED")
        void stateTransitionsShouldBeReversibleUntilClosed() {
            // OPENED -> SEEN -> SAFE -> CLOSED
            alert.currentState().seen(alert);
            assertEquals(StatusNames.SEEN, alert.currentState().getStateName());

            // Cannot go back from SAFE to SEEN in your current implementation
            alert.currentState().safe(alert);
            alert.currentState().seen(alert); // Invalid - should stay in SAFE
            assertEquals(StatusNames.SAFE, alert.currentState().getStateName());

            // But can close from any state
            alert.currentState().closed(alert);
            assertEquals(StatusNames.CLOSED, alert.currentState().getStateName());
        }
    }

}
