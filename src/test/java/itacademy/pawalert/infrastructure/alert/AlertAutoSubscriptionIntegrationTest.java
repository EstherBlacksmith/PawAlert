package itacademy.pawalert.infrastructure.alert;

import itacademy.pawalert.application.alert.port.inbound.CreateAlertUseCase;
import itacademy.pawalert.application.alert.port.inbound.AlertSubscriptionUseCase;
import itacademy.pawalert.application.alert.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.application.alert.port.outbound.AlertSubscriptionRepositoryPort;
import itacademy.pawalert.domain.alert.model.*;
import itacademy.pawalert.domain.user.Role;
import itacademy.pawalert.infrastructure.persistence.pet.PetEntity;
import itacademy.pawalert.infrastructure.persistence.pet.PetRepository;
import itacademy.pawalert.infrastructure.persistence.user.UserEntity;
import itacademy.pawalert.infrastructure.persistence.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AlertAutoSubscriptionIntegrationTest {

    @Autowired
    private CreateAlertUseCase createAlertUseCase;

    @Autowired
    private AlertSubscriptionUseCase alertSubscriptionUseCase;

    @Autowired
    private AlertRepositoryPort alertRepository;

    @Autowired
    private AlertSubscriptionRepositoryPort alertSubscriptionRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    private UUID userId;
    private UUID petId;

    @BeforeEach
    void setUp() {
        // Create test user
        UserEntity user = new UserEntity(
                UUID.randomUUID().toString(),
                "testuser",
                "test@test.com",
                "hash",
                null,
                null,
                Role.USER,
                LocalDateTime.now(),
                null,
                false,
                false
        );
        user = userRepository.save(user);
        userId = UUID.fromString(user.getId());

        // Create test pet
        PetEntity pet = new PetEntity(
                UUID.randomUUID().toString(),
                user.getId(),
                null,
                "Fluffy",
                null,
                "DOG",
                null,
                null,
                null,
                null,
                null,
                null
        );
        pet = petRepository.save(pet);
        petId = UUID.fromString(pet.getId());
    }

    @Test
    @DisplayName("When an alert is created, the creator should be automatically subscribed")
    void whenAlertCreated_thenCreatorShouldBeAutomaticallySubscribed() {
        // Given
        Title title = Title.of("Lost Dog");
        Description description = Description.of("My dog is missing");
        GeographicLocation location = GeographicLocation.of(40.7128, -74.0060);

        // When
        Alert createdAlert = createAlertUseCase.createOpenedAlert(petId, title, description, userId, location);

        // Then - Verify alert was created
        assertThat(createdAlert).isNotNull();
        assertThat(createdAlert.getId()).isNotNull();

        // Then - Verify creator is subscribed
        boolean isSubscribed = alertSubscriptionUseCase.isUserSubscribed(createdAlert.getId(), userId);
        assertThat(isSubscribed).isTrue();

        // Verify subscription exists in database
        assertThat(alertSubscriptionRepository.existsByAlertIdAndUserId(createdAlert.getId(), userId)).isTrue();
    }

    @Test
    @DisplayName("When admin creates an alert, admin should be automatically subscribed")
    void whenAdminCreatesAlert_thenAdminShouldBeAutomaticallySubscribed() {
        // Given - Create admin user
        UserEntity admin = new UserEntity(
                UUID.randomUUID().toString(),
                "adminuser",
                "admin@test.com",
                "hash",
                null,
                null,
                Role.ADMIN,
                LocalDateTime.now(),
                null,
                false,
                false
        );
        admin = userRepository.save(admin);
        UUID adminUserId = UUID.fromString(admin.getId());

        Title title = Title.of("Lost Cat");
        Description description = Description.of("My cat is missing");
        GeographicLocation location = GeographicLocation.of(34.0522, -118.2437);

        // When
        Alert createdAlert = createAlertUseCase.createOpenedAlert(petId, title, description, adminUserId, location);

        // Then - Verify alert was created
        assertThat(createdAlert).isNotNull();
        assertThat(createdAlert.getId()).isNotNull();

        // Then - Verify admin is subscribed
        boolean isSubscribed = alertSubscriptionUseCase.isUserSubscribed(createdAlert.getId(), adminUserId);
        assertThat(isSubscribed).isTrue();

        assertThat(alertSubscriptionRepository.existsByAlertIdAndUserId(createdAlert.getId(), adminUserId)).isTrue();
    }
}
