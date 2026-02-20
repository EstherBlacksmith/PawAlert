package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.domain.alert.model.*;
import itacademy.pawalert.infrastructure.persistence.pet.PetEntity;
import itacademy.pawalert.infrastructure.persistence.pet.PetRepository;
import itacademy.pawalert.infrastructure.persistence.user.UserEntity;
import itacademy.pawalert.infrastructure.persistence.user.UserRepository;
import itacademy.pawalert.domain.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AlertCascadeDeleteIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private AlertEventRepository alertEventRepository;

    @Autowired
    private AlertSubscriptionRepository alertSubscriptionRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    private AlertEntity alert;
    private AlertEventEntity event1;
    private AlertEventEntity event2;
    private AlertSubscriptionEntity subscription;
    private PetEntity pet;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        // Create user using constructor
        user = new UserEntity(
                UUID.randomUUID().toString(),
                "testuser",
                "test@test.com",
                "hash",
                null,  // surname
                null,  // phoneNumber
                Role.USER,
                LocalDateTime.now(),
                null,  // telegramChatId
                false, // emailNotificationsEnabled
                false  // telegramNotificationsEnabled
        );
        user = entityManager.persist(user);

        // Create pet using constructor
        pet = new PetEntity(
                UUID.randomUUID().toString(),
                user.getId(),
                null,  // chipNumber
                "Fluffy",
                null,  // workingPetName
                "DOG",
                null,  // breed
                null,  // size
                null,  // color
                null,  // gender
                null,  // petDescription
                null   // petImage
        );
        pet = entityManager.persist(pet);

        // Create alert using constructor
        alert = new AlertEntity(
                UUID.randomUUID().toString(),
                pet.getId(),
                user.getId(),
                "Lost Dog",
                "My dog is missing",
                StatusNames.OPENED
        );
        alert = entityManager.persist(alert);

        // Create alert events using constructor
        event1 = new AlertEventEntity(
                UUID.randomUUID().toString(),
                null,  // previousStatus
                "OPENED",
                LocalDateTime.now(),
                user.getId(),
                null,  // location
                null   // closureReason
        );
        event1 = entityManager.persist(event1);

        event2 = new AlertEventEntity(
                UUID.randomUUID().toString(),
                "OPENED",
                "SEEN",
                LocalDateTime.now(),
                user.getId(),
                null,  // location
                null   // closureReason
        );
        event2 = entityManager.persist(event2);

        // Create subscription using constructor
        subscription = new AlertSubscriptionEntity(
                UUID.randomUUID(),
                alert,
                UUID.fromString(user.getId()),
                LocalDateTime.now()
        );
        entityManager.persist(subscription);

        entityManager.flush();
    }

    // ==================== CASCADE DELETE TESTS ====================

    @Test
    @DisplayName("When alert is deleted, associated events should be cascade deleted")
    void whenAlertDeleted_thenEventsShouldBeCascadeDeleted() {
        // Given
        assertThat(alertEventRepository.findAll()).hasSize(2);

        // When
        alertRepository.delete(alert);

        // Then
        List<AlertEventEntity> remainingEvents = alertEventRepository.findAll();
        assertThat(remainingEvents).isEmpty();
    }

    @Test
    @DisplayName("When alert is deleted, associated subscriptions should be cascade deleted")
    void whenAlertDeleted_thenSubscriptionsShouldBeCascadeDeleted() {
        // Given
        assertThat(alertSubscriptionRepository.findAll()).hasSize(1);

        // When
        alertRepository.delete(alert);

        // Then
        List<AlertSubscriptionEntity> remainingSubscriptions = alertSubscriptionRepository.findAll();
        assertThat(remainingSubscriptions).isEmpty();
    }

    @Test
    @DisplayName("When alert is deleted, pet should NOT be deleted (no cascade)")
    void whenAlertDeleted_thenPetShouldNotBeDeleted() {
        // Given
        String petId = pet.getId();

        // When
        alertRepository.delete(alert);

        // Then
        Optional<PetEntity> remainingPet = petRepository.findById(petId);
        assertThat(remainingPet).isPresent();
    }

    @Test
    @DisplayName("When alert is deleted, user should NOT be deleted (no cascade)")
    void whenAlertDeleted_thenUserShouldNotBeDeleted() {
        // Given
        String userId = user.getId();

        // When
        alertRepository.delete(alert);

        // Then
        Optional<UserEntity> remainingUser = userRepository.findById(userId);
        assertThat(remainingUser).isPresent();
    }

    // ==================== SOFT DELETE TESTS ====================

    @Test
    @DisplayName("When alert is soft deleted, it should be filtered by notDeleted specification")
    void whenAlertSoftDeleted_thenShouldBeFilteredBySpecification() {
        // Given - Create a new alert and manually set deletedAt
        AlertEntity softDeletedAlert = new AlertEntity(
                UUID.randomUUID().toString(),
                pet.getId(),
                user.getId(),
                "Deleted Alert",
                "This is deleted",
                StatusNames.OPENED
        );
        softDeletedAlert = entityManager.persist(softDeletedAlert);

        // Manually update deletedAt via native query since there's no setter
        entityManager.getEntityManager().createNativeQuery(
                "UPDATE alerts SET deleted_at = NOW() WHERE id = ?"
        ).setParameter(1, softDeletedAlert.getId()).executeUpdate();
        entityManager.flush();
        entityManager.clear();

        // When
        List<AlertEntity> activeAlerts = alertRepository.findAll(
                AlertSpecifications.notDeleted()
        );

        // Then
        assertThat(activeAlerts).hasSize(1); // Only the original alert
        assertThat(activeAlerts.getFirst().getId()).isEqualTo(alert.getId());
    }

    @Test
    @DisplayName("When alert is not deleted, it should be found by notDeleted specification")
    void whenAlertNotDeleted_thenShouldBeFoundBySpecification() {
        // When
        List<AlertEntity> activeAlerts = alertRepository.findAll(
                AlertSpecifications.notDeleted()
        );

        // Then
        assertThat(activeAlerts).hasSize(1);
        assertThat(activeAlerts.get(0).getId()).isEqualTo(alert.getId());
    }

    // ==================== SEARCH CRITERIA TESTS ====================

    @Test
    @DisplayName("Search with status should filter correctly and exclude soft deleted")
    void whenSearchWithStatus_thenShouldFilterCorrectly() {
        // Given
        AlertEntity closedAlert = new AlertEntity(
                UUID.randomUUID().toString(),
                pet.getId(),
                user.getId(),
                "Another Alert",
                "Description",
                StatusNames.CLOSED
        );
        entityManager.persist(closedAlert);

        // When
        List<AlertEntity> openAlerts = alertRepository.findAll(
                AlertSpecifications.notDeleted()
                        .and(AlertSpecifications.withStatus(StatusNames.OPENED))
        );

        // Then
        assertThat(openAlerts).hasSize(1);
        assertThat(openAlerts.get(0).getStatus()).isEqualTo("OPENED");
    }

    @Test
    @DisplayName("Search with title should filter correctly")
    void whenSearchWithTitle_thenShouldFilterCorrectly() {
        // When
        List<AlertEntity> found = alertRepository.findAll(
                AlertSpecifications.notDeleted()
                        .and(AlertSpecifications.titleContains("Lost"))
        );

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getTitle()).containsIgnoringCase("Lost");
    }

    // ==================== PET RELATIONSHIP TESTS (SUBQUERY) ====================

    @Test
    @DisplayName("Search by pet name should work with subquery (no JPA relationship)")
    void whenSearchByPetName_thenShouldWorkWithSubquery() {
        // When
        List<AlertEntity> found = alertRepository.findAll(
                AlertSpecifications.notDeleted()
                        .and(AlertSpecifications.petNameContains("Fluffy"))
        );

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getPetId()).isEqualTo(pet.getId());
    }

    @Test
    @DisplayName("Search by pet species should work with subquery")
    void whenSearchByPetSpecies_thenShouldWorkWithSubquery() {
        // When
        List<AlertEntity> found = alertRepository.findAll(
                AlertSpecifications.notDeleted()
                        .and(AlertSpecifications.withPetSpecies("DOG"))
        );

        // Then
        assertThat(found).hasSize(1);
    }
}
