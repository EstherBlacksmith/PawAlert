package itacademy.pawalert.application.service;


import itacademy.pawalert.application.exception.AlertNotFoundException;
import itacademy.pawalert.application.exception.UnauthorizedException;
import itacademy.pawalert.domain.Alert;
import itacademy.pawalert.domain.AlertEvent;
import itacademy.pawalert.domain.StatusNames;
import itacademy.pawalert.domain.UserId;
import itacademy.pawalert.domain.exception.InvalidAlertStatusChange;
import itacademy.pawalert.infrastructure.persistence.AlertEntity;
import itacademy.pawalert.infrastructure.persistence.AlertEventEntity;
import itacademy.pawalert.infrastructure.persistence.AlertEventRepository;
import itacademy.pawalert.infrastructure.persistence.AlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertService Unit Tests")
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private AlertEventRepository eventRepository;

    @InjectMocks
    private AlertService alertService;

    // ═══════════════════════════════════════════════════════════════════════
    // @BeforeEach - Using TestAlertFactory
    // ═══════════════════════════════════════════════════════════════════════

    private String alertId;
    private UserId userId;
    private UUID petId;
    private AlertEntity openedEntity;
    private AlertEntity seenEntity;
    private AlertEntity safeEntity;
    private AlertEntity closedEntity;

    @BeforeEach
    void setUp() {
        // Initialize IDs
        alertId = UUID.randomUUID().toString();
        userId = UserId.fromUUID(UUID.randomUUID());
        petId = UUID.randomUUID();

        // USING THE FACTORY METHOD to create entities
        // The factory ensures valid state transitions
        Alert openedAlert = TestAlertFactory.createOpenedAlert(
                UUID.fromString(alertId), petId, userId);
        openedEntity = openedAlert.toEntity();

        Alert seenAlert = TestAlertFactory.createSeenAlert(
                UUID.fromString(alertId), petId, userId);
        seenEntity = seenAlert.toEntity();

        Alert safeAlert = TestAlertFactory.createSafeAlert(
                UUID.fromString(alertId), petId, userId);
        safeEntity = safeAlert.toEntity();

        Alert closedAlert = TestAlertFactory.createClosedAlert(
                UUID.fromString(alertId), petId, userId);
        closedEntity = closedAlert.toEntity();

        // Configure shared mocks
        lenient().when(alertRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    @DisplayName("Should throw when changing status of non-existent alert")
    void shouldThrowWhenAlertNotFound() {
        // Given
        String nonExistentId = "non-existent-id";
        when(alertRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(InvalidAlertStatusChange.class,
                () -> alertService.changeStatus(nonExistentId, StatusNames.SEEN, userId.toString()));
    }

    @Test
    @DisplayName("Should throw when changing status of already closed alert")
    void shouldThrowWhenAlertAlreadyClosed() {
        // Given
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(closedEntity));

        // When/Then
        assertThrows(InvalidAlertStatusChange.class,
                () -> alertService.changeStatus(alertId, StatusNames.SEEN, userId.toString()));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // changeStatus Tests
    // ═══════════════════════════════════════════════════════════════════════

    // ═══════════════════════════════════════════════════════════════════════
    // findById Tests
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("createOpenedAlert Tests")
    class CreateOpenedAlertTests {

        @Test
        @DisplayName("Should create alert with OPENED status and save initial event")
        void shouldCreateAlertWithInitialEvent() {
            // Given - Usar UUID válido
            String title = "Test Alert";
            String description = "Test description";

            when(alertRepository.save(any(AlertEntity.class))).thenAnswer(inv -> inv.getArgument(0));

            when(eventRepository.save(any(AlertEventEntity.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            Alert result = alertService.createOpenedAlert(petId.toString(), title, description, userId.toString());

            // Then
            assertNotNull(result);
            assertEquals(StatusNames.OPENED, result.currentStatus().getStatusName());
            verify(eventRepository, times(1)).save(any(AlertEventEntity.class));
        }

        @Test
        @DisplayName("Should call alertRepository.save() when creating alert")
        void shouldSaveAlertWhenCreating() {
            // Given
            when(alertRepository.save(any(AlertEntity.class))).thenAnswer(inv -> inv.getArgument(0));
            when(eventRepository.save(any(AlertEventEntity.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            alertService.createOpenedAlert(petId.toString(), "Title", "Desc", userId.toString());

            // Then
            verify(alertRepository, times(1)).save(any(AlertEntity.class));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Convenience Method Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return alert when found")
        void shouldReturnAlertWhenFound() {
            // Given
            when(alertRepository.findById(alertId)).thenReturn(Optional.of(openedEntity));

            // When
            Alert result = alertService.findById(alertId);

            // Then
            assertNotNull(result);
            assertEquals(alertId, result.getId().toString());
            verify(alertRepository).findById(alertId);
        }

        @Test
        @DisplayName("Should throw AlertNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            // Given
            when(alertRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When/Then
            assertThrows(AlertNotFoundException.class, () -> alertService.findById("non-existent"));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // getAlertHistory Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("changeStatus Tests")
    class ChangeStatusTests {

        @Test
        void shouldChangeStatusFromOpenedToSeen() {
            // Given - Create entity WITH the initial event in its history
            new AlertEntity(alertId, petId.toString(), userId.toString(), "Test", "Test", StatusNames.OPENED);

            AlertEventEntity initialEvent = new AlertEventEntity(
                    UUID.randomUUID().toString(),
                    null,
                    "OPENED",
                    LocalDateTime.now(),
                    "user-1"
            );

            openedEntity.getHistory().add(initialEvent);

            when(alertRepository.findById(alertId)).thenReturn(Optional.of(openedEntity));

            // When
            Alert result = alertService.changeStatus(alertId, StatusNames.SEEN, userId.toString());

            // Then - Expect 2 saves: initial event (already saved) + change event
            verify(eventRepository, times(1)).save(any(AlertEventEntity.class));
            assertEquals(StatusNames.SEEN, result.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("Should throw exception when alert not found")
        void shouldThrowWhenAlertNotFoundForChangeStatus() {
            // Given
            when(alertRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When/Then
            assertThrows(InvalidAlertStatusChange.class,
                    () -> alertService.changeStatus("non-existent", StatusNames.SEEN, userId.toString()));
        }

        @Test
        @DisplayName("Should throw exception when alert is already closed")
        void shouldThrowWhenAlertAlreadyClosed() {
            // Given
            when(alertRepository.findById(alertId)).thenReturn(Optional.of(closedEntity));

            // When/Then
            assertThrows(InvalidAlertStatusChange.class,
                    () -> alertService.changeStatus(alertId, StatusNames.SEEN, userId.toString()));
        }
    }

    @Nested
    @DisplayName("Convenience Method Tests")
    class ConvenienceMethodTests {

        @Test
        @DisplayName("markAsSeen should delegate to changeStatus")
        void markAsSeenShouldDelegate() {
            // Given
            when(alertRepository.findById(alertId)).thenReturn(Optional.of(openedEntity));

            // When
            Alert result = alertService.markAsSeen(alertId, userId.toString());

            // Then
            assertEquals(StatusNames.SEEN, result.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("markAsSafe should delegate to changeStatus")
        void markAsSafeShouldDelegate() {
            // Given
            when(alertRepository.findById(alertId)).thenReturn(Optional.of(seenEntity));

            // When
            Alert result = alertService.markAsSafe(alertId, userId.toString());

            // Then
            assertEquals(StatusNames.SAFE, result.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("close should delegate to changeStatus")
        void closeShouldDelegate() {
            // Given
            when(alertRepository.findById(alertId)).thenReturn(Optional.of(safeEntity));

            // When
            Alert result = alertService.close(alertId, userId.toString());

            // Then
            assertEquals(StatusNames.CLOSED, result.currentStatus().getStatusName());
        }
    }

    @Nested
    @DisplayName("getAlertHistory Tests")
    class GetAlertHistoryTests {

        @Test
        @DisplayName("Should return alert history")
        void shouldReturnAlertHistory() {
            // Given
            String testAlertId = "123";

            AlertEventEntity event1 = new AlertEventEntity("event-1", null, "OPENED", java.time.LocalDateTime.now().minusHours(1), "user-1");
            AlertEventEntity event2 = new AlertEventEntity("event-2", "OPENED", "SEEN", java.time.LocalDateTime.now(), "user-2");

            when(eventRepository.findByAlertIdOrderByChangedAtDesc(testAlertId))
                    .thenReturn(Arrays.asList(event2, event1));

            // When
            List<AlertEvent> history = alertService.getAlertHistory(testAlertId);

            // Then
            assertNotNull(history);
            assertEquals(2, history.size());
            assertEquals(StatusNames.SEEN, history.get(0).getNewStatus());
            assertEquals(StatusNames.OPENED, history.get(1).getNewStatus());

        }
    }


    @Test
    void updateTitle_WhenCreator_ShouldUpdateTitle() {
        // Given
        String alertId = UUID.randomUUID().toString();
        String creatorId = "user-123";
        String newTitle = "Perro encontrado en el parque";

        Alert alert = TestAlertFactory.createModificableAlert(alertId, creatorId, "Título original", "Descripción");
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert.toEntity()));
        when(alertRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When
        Alert result = alertService.updateTitle(alertId, creatorId, newTitle);

        // Then
        assertEquals(newTitle, result.getTitle().getValue());
        verify(alertRepository).save(any());
    }

    @Test
    void updateTitle_WhenNotCreator_ShouldThrowUnauthorized() {
        // Given
        String alertId = UUID.randomUUID().toString();
        String creatorId = "user-123";
        String otherUserId = "user-456";

        Alert alert = TestAlertFactory.createModificableAlert(alertId, creatorId, "Título", "Descripción");
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert.toEntity()));

        // When/Then
        assertThrows(UnauthorizedException.class,
                () -> alertService.updateTitle(alertId, otherUserId, "Nuevo título"));

        verify(alertRepository, never()).save(any());

    }

    @Test
    void updateDescription_WhenCreator_ShouldUpdateDescription() {
        // Given
        String alertId = UUID.randomUUID().toString();
        String creatorId = "user-123";
        String newDescription = "El perro es de color marrón, tiene collar azul";

        Alert alert = TestAlertFactory.createModificableAlert(alertId, creatorId, "Título", "Descripción original");
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert.toEntity()));
        when(alertRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When
        Alert result = alertService.updateDescription(alertId, creatorId, newDescription);

        // Then
        assertEquals(newDescription, result.getDescription().getValue());

        verify(alertRepository,times(1)).save(any());
    }

    @Test
    void updateTitle_WhenAlertNotFound_ShouldThrowAlertNotFoundException() {
        // Given
        String alertId = "non-existent-id";
        String userId = "user-123";
        when(alertRepository.findById(alertId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(AlertNotFoundException.class,
                () -> alertService.updateTitle(alertId, userId, "Nuevo título"));
        verify(alertRepository, never()).save(any());
    }
}
