package itacademy.pawalert.application.service;


import itacademy.pawalert.application.alert.service.AlertService;
import itacademy.pawalert.application.exception.AlertNotFoundException;
import itacademy.pawalert.application.exception.UnauthorizedException;
import itacademy.pawalert.domain.alert.exception.AlertModificationNotAllowedException;
import itacademy.pawalert.application.user.port.inbound.GetUserUseCase;
import itacademy.pawalert.application.alert.port.outbound.AlertEventRepositoryPort;
import itacademy.pawalert.application.alert.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.domain.alert.exception.InvalidAlertStatusChange;
import itacademy.pawalert.domain.alert.model.*;
import itacademy.pawalert.domain.user.Role;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.model.Email;
import itacademy.pawalert.domain.user.model.PhoneNumber;
import itacademy.pawalert.domain.user.model.Surname;
import itacademy.pawalert.domain.user.model.Username;
import itacademy.pawalert.infrastructure.persistence.alert.AlertEntity;
import itacademy.pawalert.infrastructure.persistence.alert.AlertEventEntity;
import itacademy.pawalert.infrastructure.rest.alert.dto.AlertWithContactDTO;
import itacademy.pawalert.infrastructure.rest.alert.mapper.AlertMapper;
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
    private AlertRepositoryPort alertRepository;

    @Mock
    private AlertEventRepositoryPort eventRepository;

    @Mock
    private GetUserUseCase userUseCase;

    @Mock
    private AlertMapper alertMapper;

    @InjectMocks
    private AlertService alertService;

    // ═══════════════════════════════════════════════════════════════════════
    // @BeforeEach - Using TestAlertFactory
    // ═══════════════════════════════════════════════════════════════════════

    private UUID  alertId;
    private UUID userId;
    private UUID petId;
    private Alert testAlert;
    private GeographicLocation location = GeographicLocation.of(40.4168, -3.7025);;

    @BeforeEach
    void setUp() {
        // Initialize IDs
        petId = UUID.randomUUID();
        alertId = UUID.randomUUID();
        userId = UUID.randomUUID();
        testAlert = TestAlertFactory.createOpenedAlert(
                alertId,
                petId,
                userId
        );

        // Configure shared mocks
        lenient().when(alertRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        location = GeographicLocation.of(40.4168, -3.7025);
    }

    @Test
    @DisplayName("Should throw when changing status of non-existent alert")
    void shouldThrowWhenAlertNotFound() {
        // Given
        UUID nonExistentId = UUID.fromString("12345678-1234-1234-1234-123456789012");
        when(alertRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(AlertNotFoundException.class,
                () -> alertService.changeStatus(nonExistentId, StatusNames.SEEN, userId,location));
    }

    @Test
    @DisplayName("Should throw when changing status of already closed alert")
    void shouldThrowWhenAlertAlreadyClosed() {

        // Given
        Alert closedAlert = TestAlertFactory.createClosedAlert(
               alertId, petId, userId
        );
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(closedAlert));

        // When/Then
        assertThrows(InvalidAlertStatusChange.class,
                () -> alertService.changeStatus(alertId, StatusNames.SEEN, userId,location));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // changeStatus Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void updateTitle_WhenCreator_ShouldUpdateTitle() {
        // Given
        UUID alertId = UUID.randomUUID();
        UUID creatorId = UUID.fromString("12345678-1234-1234-1234-123456789012");
        Title newTitle = Title.of("Dog found in the park");

        Alert alert = TestAlertFactory.createModificableAlert(alertId.toString(), creatorId.toString(), "Original Title", "Description");
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When
        Alert result = alertService.updateTitle(alertId, creatorId, newTitle);

        // Then
        assertEquals(newTitle.getValue(), result.getTitle().getValue());
        verify(alertRepository).save(any());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Convenience Method Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void updateTitle_WhenNotCreator_ShouldThrowUnauthorized() {
        // Given
        UUID alertId = UUID.randomUUID();

        UUID creatorId = UUID.fromString("12345678-1234-1234-1234-123456789012");
        UUID otherUserId = UUID.fromString("87654321-4321-4321-4321-210987654321");

        Alert alert = TestAlertFactory.createModificableAlert(String.valueOf(alertId), String.valueOf(creatorId), "Title", "Description");
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));

        // When/Then
        assertThrows(UnauthorizedException.class,
                () -> alertService.updateTitle(alertId, otherUserId, Title.of("New title")));

        verify(alertRepository, never()).save(any());

    }

    // ═══════════════════════════════════════════════════════════════════════
    // getAlertHistory Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void updateDescription_WhenCreator_ShouldUpdateDescription() {
        // Given
        UUID alertId = UUID.randomUUID();
        Description newDescription = Description.of( "The dog is brown, has a blue collar");
        UUID creatorId = UUID.fromString("12345678-1234-1234-1234-123456789012");

        Alert alert = TestAlertFactory.createModificableAlert(alertId.toString(), creatorId.toString(), "Title", "Original Description");
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When
        Alert result = alertService.updateDescription(alertId, creatorId, newDescription);

        // Then
        assertEquals(newDescription.getValue(), result.getDescription().getValue());

        verify(alertRepository, times(1)).save(any());
    }

    @Test
    void updateTitle_WhenAlertNotFound_ShouldThrowAlertNotFoundException() {
        // Given
        UUID alertId = UUID.fromString("12345678-1234-1234-1234-123456789012");
        when(alertRepository.findById(alertId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(AlertNotFoundException.class,
                () -> alertService.updateTitle(alertId, userId, Title.of("New title")));
        verify(alertRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when alert is closed and trying to update title")
    void updateTitle_WhenAlertIsClosed_ShouldThrowException() {
        // Given
        UUID alertId = UUID.randomUUID();
        UUID creatorId = UUID.fromString("12345678-1234-1234-1234-123456789012");


        Alert closedAlert = TestAlertFactory.createClosedAlert(
                alertId, UUID.randomUUID(), creatorId
        );
        when(alertRepository.findById(alertId)).thenReturn(Optional.ofNullable(closedAlert));

        // When/Then
        assertThrows(AlertModificationNotAllowedException.class,
                () -> alertService.updateTitle(alertId, creatorId, Title.of("New Title")));
    }

    @Test
    @DisplayName("Should throw exception when alert is closed and trying to update description")
    void updateDescription_WhenAlertIsClosed_ShouldThrowException() {
        // Given
        UUID alertId = UUID.randomUUID();
        UUID creatorId = UUID.fromString("12345678-1234-1234-1234-123456789012");

        Alert closedAlert = TestAlertFactory.createClosedAlert(
                alertId, UUID.randomUUID(), creatorId
        );
        when(alertRepository.findById(alertId)).thenReturn(Optional.ofNullable(closedAlert));

        // When/Then
        assertThrows(AlertModificationNotAllowedException.class,
                () -> alertService.updateDescription(alertId, creatorId, Description.of("New Description")));
    }

    @Test
    @DisplayName("Should NOT save event when unauthorized user tries to update title")
    void updateTitle_WhenNotCreator_ShouldNotSaveEvent() {
        // Given
        UUID alertId = UUID.randomUUID();
        UUID creatorId = UUID.fromString("12345678-1234-1234-1234-123456789012");
        UUID otherUserId = UUID.fromString("87654321-4321-4321-4321-210987654321");

        Alert alert = TestAlertFactory.createModificableAlert(alertId.toString(), creatorId.toString(), "Title", "Description");
        when(alertRepository.findById(alertId)).thenReturn(Optional.ofNullable(alert));

        // When/Then
        assertThrows(UnauthorizedException.class,
                () -> alertService.updateTitle(alertId, otherUserId, Title.of("New Title")));

        // Key assertion: NO event saved
        verify(eventRepository, never()).save(any(AlertEvent.class));
    }

    @Test
    @DisplayName("Should NOT save event when unauthorized user tries to update description")
    void updateDescription_WhenNotCreator_ShouldNotSaveEvent() {
        // Given
        UUID alertId = UUID.randomUUID();
        UUID creatorId = UUID.fromString("12345678-1234-1234-1234-123456789012");
        UUID otherUserId = UUID.fromString("87654321-4321-4321-4321-210987654321");

        Alert alert = TestAlertFactory.createModificableAlert(alertId.toString(), creatorId.toString(), "Title", "Description");
        when(alertRepository.findById(alertId)).thenReturn(Optional.ofNullable(alert));

        // When/Then
        assertThrows(UnauthorizedException.class,
                () -> alertService.updateDescription(alertId, otherUserId, Description.of("New Description")));

        // Key assertion: NO event saved
        verify(eventRepository, never()).save(any(AlertEvent.class));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // findById Tests
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("createOpenedAlert Tests")
    class CreateOpenedAlertTests {

        @Test
        @DisplayName("Should create alert with OPENED status and save initial event")
        void shouldCreateAlertWithInitialEvent() {
            // Given - Use valid UUID
            Title title = Title.of("Test Alert");
            Description description = Description.of("Test description");

            when(alertRepository.save(any(Alert.class))).thenAnswer(inv -> inv.getArgument(0));

            when(eventRepository.save(any(AlertEvent.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            Alert result = alertService.createOpenedAlert(petId, title, description, userId,location);

            // Then
            assertNotNull(result);
            assertEquals(StatusNames.OPENED, result.currentStatus().getStatusName());
            verify(eventRepository, times(1)).save(any(AlertEvent.class));
        }

        @Test
        @DisplayName("Should call alertRepository.save() when creating alert")
        void shouldSaveAlertWhenCreating() {
            // Given
            when(alertRepository.save(any(Alert.class))).thenAnswer(inv -> inv.getArgument(0));
            when(eventRepository.save(any(AlertEvent.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            Title title = Title.of("Title");
            Description description = Description.of("Desc");
            alertService.createOpenedAlert(petId, title, description, userId,location);

            // Then
            verify(alertRepository, times(1)).save(any(Alert.class));
        }
    }

    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return alert when found")
        void shouldReturnAlertWhenFound() {
            // Given
            when(alertRepository.findById(alertId)).thenReturn(Optional.of(testAlert));

            // When
            Alert result = alertService.getAlertById(alertId);

            // Then
            assertNotNull(result);
            assertEquals(alertId.toString(), result.getId().toString());
            verify(alertRepository).findById(alertId);
        }

        @Test
        @DisplayName("Should throw AlertNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            // Given
            when(alertRepository.findById(UUID.fromString("12345678-1234-1234-1234-123456789012"))).thenReturn(Optional.empty());

            // When/Then
            assertThrows(AlertNotFoundException.class, () -> alertService.getAlertById(UUID.fromString("12345678-1234-1234-1234-123456789012")));
        }
    }

    @Nested
    @DisplayName("changeStatus Tests")
    class ChangeStatusTests {

        @Test
        void shouldChangeStatusFromOpenedToSeen() {
            // Given - Create entity WITH the initial event in its history
            new AlertEntity(alertId.toString(), petId.toString(), userId.toString(), "Test", "Test", StatusNames.OPENED);

            AlertEventEntity initialEvent = new AlertEventEntity(
                    UUID.randomUUID().toString(),
                    null,
                    "OPENED",
                    LocalDateTime.now(),
                    "user-1",
                    location
            );

            when(alertRepository.findById(alertId)).thenReturn(Optional.of(testAlert));

            // When
            Alert result = alertService.changeStatus(alertId, StatusNames.SEEN, userId,location);

            // Then - Expect 2 saves: initial event (already saved) + change event
            verify(eventRepository, times(1)).save(any(AlertEvent.class));
            assertEquals(StatusNames.SEEN, result.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("Should throw exception when alert not found")
        void shouldThrowWhenAlertNotFoundForChangeStatus() {
            // Given
            when(alertRepository.findById(UUID.fromString("12345678-1234-1234-1234-123456789012"))).thenReturn(Optional.empty());

            // When/Then
            assertThrows(AlertNotFoundException.class,
                    () -> alertService.changeStatus(UUID.fromString("12345678-1234-1234-1234-123456789012"), StatusNames.SEEN, userId,location));
        }
    }

    @Nested
    @DisplayName("Convenience Method Tests")
    class ConvenienceMethodTests {

        @Test
        @DisplayName("markAsSeen should delegate to changeStatus")
        void markAsSeenShouldDelegate() {
            // Given
            when(alertRepository.findById(alertId)).thenReturn(Optional.of(testAlert));

            // When
            Alert result = alertService.markAsSeen(alertId, userId,location);

            // Then
            assertEquals(StatusNames.SEEN, result.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("markAsSafe should delegate to changeStatus")
        void markAsSafeShouldDelegate() {
            // Given
            when(alertRepository.findById(alertId)).thenReturn(Optional.of(testAlert));

            // When
            Alert result = alertService.markAsSafe(alertId, userId,location);

            // Then
            assertEquals(StatusNames.SAFE, result.currentStatus().getStatusName());
        }

        @Test
        @DisplayName("close should delegate to changeStatus")
        void closeShouldDelegate() {
            // Given
            when(alertRepository.findById(alertId)).thenReturn(Optional.of(testAlert));

            // When
            Alert result = alertService.markAsClosed(alertId, userId,location);

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
            AlertEvent event1 = AlertEvent.createStatusEvent(
                    StatusNames.OPENED, StatusNames.SEEN,UUID.fromString("12345678-1234-1234-1234-123456789012"),location
            );

            AlertEvent event2 = AlertEvent.createStatusEvent(
                    StatusNames.SEEN, StatusNames.CLOSED,UUID.fromString("87654321-4321-4321-4321-210987654321"),location
            );
            UUID testAlertId = UUID.fromString("12345678-1234-1234-1234-123456789012");

            when(eventRepository.findByAlertIdOrderByChangedAtDesc(testAlertId))
                    .thenReturn(Arrays.asList(event1, event2));

            // When
            List<AlertEvent> history = alertService.getAlertHistory(testAlertId);

            // Then
            assertNotNull(history);
            assertEquals(2, history.size());
            assertEquals(StatusNames.SEEN, history.get(0).getNewStatus());
            assertEquals(StatusNames.CLOSED, history.get(1).getNewStatus());

        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // getPhoneUser in alert Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getAlertWithCreatorPhone Tests")
    class GetAlertWithCreatorPhoneTests {

        @Test
        @DisplayName("Should return AlertWithContactDTO with creator phone")
        void shouldReturnAlertWithCreatorPhone() {
            // Given
            UUID alertId = UUID.randomUUID();
            UUID creatorId = UUID.randomUUID();
            UUID petId = UUID.randomUUID();

            Alert alert = TestAlertFactory.createOpenedAlert(
                    alertId,
                    petId,
                    creatorId
            );

            User creator = new User(
                    creatorId,
                    Username.of("john_doe"),
                    Email.of("john@example.com"),
                    Surname.of("John Doe"),
                    PhoneNumber.of("+34612345678"),
                    Role.USER
            );

            AlertWithContactDTO expectedDTO = new AlertWithContactDTO(
                    alertId,
                    petId,
                    creatorId,
                    Title.of("Test Alert"),
                    Description.of("Test description"),
                    StatusNames.OPENED,
                    PhoneNumber.of("+34612345678"),
                    Surname.of("Garcia")
            );

            when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
            when(userUseCase.getById(creatorId)).thenReturn(creator);
            when(alertMapper.toWithContact(alert, creator)).thenReturn(expectedDTO);

            // When
            AlertWithContactDTO result = alertService.getAlertWithCreatorPhone(alertId);

            // Then
            assertNotNull(result);
            assertEquals(alertId, result.id());
            assertEquals("+34612345678", result.creatorPhone().value());
            assertEquals("Garcia", result.creatorName().value());

            verify(alertRepository).findById(alertId);
            verify(userUseCase).getById(creatorId);
            verify(alertMapper).toWithContact(alert, creator);
        }

        @Test
        @DisplayName("Should throw AlertNotFoundException when alert does not exist")
        void shouldThrowWhenAlertNotFound() {
            // Given
            String nonExistentId = "12345678-1234-1234-1234-123456789012";
            when(alertRepository.findById(UUID.fromString(nonExistentId))).thenReturn(Optional.empty());

            // When/Then
            assertThrows(AlertNotFoundException.class,
                    () -> alertService.getAlertWithCreatorPhone(UUID.fromString(nonExistentId)));

            verify(userUseCase, never()).getById(any());
            verifyNoInteractions(alertMapper);
        }
    }
}
