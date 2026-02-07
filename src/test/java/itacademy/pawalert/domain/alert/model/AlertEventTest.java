package itacademy.pawalert.domain.alert.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AlertEvent Tests")
class AlertEventTest {
    private GeographicLocation location;
    @BeforeEach
    public void setUp(){
        location = GeographicLocation.of(40.4168, -3.7025);
    }

    @Nested
    @DisplayName("createStatusEvent Tests")
    class CreateStatusEventTests {

        @Test
        @DisplayName("Should create status change event with fromStatus and toStatus")
        void shouldCreateStatusChangeEvent() {
            UUID userId = UUID.randomUUID();
            AlertEvent event = AlertEvent.createStatusEvent(
                    StatusNames.OPENED, StatusNames.SEEN, userId,location);

            assertEquals(StatusNames.OPENED, event.getPreviousStatus());
            assertEquals(StatusNames.SEEN, event.getNewStatus());
            assertEquals(userId, event.getChangedBy());
            assertNotNull(event.getChangedAt());
        }

        @Test
        @DisplayName("Should create event with null previous status for OPENED")
        void shouldCreateEventWithNullPreviousStatusForOpened() {
            UUID userId = UUID.randomUUID();
            AlertEvent event = AlertEvent.createStatusEvent(
                    null, StatusNames.OPENED, userId,location);

            assertNull(event.getPreviousStatus());
            assertEquals(StatusNames.OPENED, event.getNewStatus());
        }
    }

    @Nested
    @DisplayName("Event Type Tests")
    class EventTypeTests {

        @Test
        @DisplayName("Status change event should have STATUS_CHANGED type")
        void statusChangeEventShouldHaveCorrectType() {
            AlertEvent event = AlertEvent.createStatusEvent(
                    StatusNames.OPENED, StatusNames.SEEN, UUID.randomUUID(),location);

            assertEquals(EventType.STATUS_CHANGED, event.getEventType());
        }
    }
}
