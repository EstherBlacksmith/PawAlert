package itacademy.pawalert.domain.pet.service;

import itacademy.pawalert.application.exception.UnauthorizedException;
import itacademy.pawalert.application.service.PetService;
import itacademy.pawalert.domain.pet.exception.PetNotFoundException;
import itacademy.pawalert.domain.pet.model.*;
import itacademy.pawalert.infrastructure.persistence.pet.PetEntity;
import itacademy.pawalert.application.port.outbound.PetRepositoryPort;
import itacademy.pawalert.infrastructure.rest.pet.dto.UpdatePetRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PetService Unit Tests")
class PetServiceTest {

    @Mock
    private PetRepositoryPort petRepositoryPort;

    @InjectMocks
    private PetService petService;

    private String petId;
    private String userId;
    private Pet testPet;
    private PetEntity testPetEntity;

    @BeforeEach
    void setUp() {
        petId = UUID.randomUUID().toString();
        userId = UUID.randomUUID().toString();

        testPet = createTestPet(petId, userId);
        testPetEntity = testPet.toEntity();
    }

    private Pet createTestPet(String petId, String userId) {
        return Pet.builder()
                .userId(UUID.fromString(userId))
                .petId(UUID.fromString(petId))
                .chipNumber(new ChipNumber("123456789012345"))
                .officialPetName(new PetName("Max"))
                .workingPetName(new PetName("Buddy"))
                .species(Species.DOG)
                .breed(new Breed("Golden Retriever"))
                .size(Size.MEDIUM)
                .color(new Color("Golden"))
                .gender(Gender.MALE)
                .petDescription(new PetDescription("Friendly dog"))
                .petImage(new PetImage("http://example.com/image.jpg"))
                .build();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // updatePet - Success Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("updatePet - Success Tests")
    class UpdatePetSuccessTests {

        @Test
        @DisplayName("Should update pet when owner is valid")
        void updatePet_whenOwnerIsValid_updatesPetSuccessfully() {
            // Given
            String newWorkingName = "NewBuddy";
            UpdatePetRequest request = new UpdatePetRequest(
                    null, newWorkingName,null, null,
                    null, null, null, null, null, null
            );

            when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(testPet));
            when(petRepositoryPort.save(any(PetEntity.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            Pet result = petService.updatePet(petId, userId, request);

            // Then
            assertNotNull(result);
            assertEquals(newWorkingName, result.getWorkingPetName().value());
            verify(petRepositoryPort).save(any(PetEntity.class));
        }

        @Test
        @DisplayName("Should update multiple fields at once")
        void updatePet_whenUpdatingMultipleFields_updatesAllFields() {
            // Given
            UpdatePetRequest request = new UpdatePetRequest(
                    null, "NewName", null, "Cat", null,
                    "Small", "White", null, null, null
            );

            when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(testPet));
            when(petRepositoryPort.save(any(PetEntity.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            Pet result = petService.updatePet(petId, userId, request);

            // Then
            assertEquals("NewName", result.getWorkingPetName().value());
            assertEquals(Species.CAT, result.getSpecies());
            assertEquals(Size.SMALL, result.getSize());
            assertEquals("White", result.getColor().value());
        }

        @Test
        @DisplayName("Should not update fields that are null in request")
        void updatePet_whenFieldIsNull_keepsExistingValue() {
            // Given
            UpdatePetRequest request = new UpdatePetRequest(
                    null, null, null, null, null,
                    null, "Brown", null, null, null
            );

            when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(testPet));
            when(petRepositoryPort.save(any(PetEntity.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            Pet result = petService.updatePet(petId, userId, request);

            // Then
            assertEquals("Brown", result.getColor().value());
            assertEquals("Buddy", result.getWorkingPetName().value()); // Original value
            assertEquals(Species.DOG, result.getSpecies()); // Original value
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // updatePet - Error Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("updatePet - Error Tests")
    class UpdatePetErrorTests {

        @Test
        @DisplayName("Should throw PetNotFoundException when pet does not exist")
        void updatePet_whenPetNotFound_throwsPetNotFoundException() {
            // Given
            String nonExistentPetId = "non-existent-id";
            UpdatePetRequest request = new UpdatePetRequest(
                    null, "NewName", null, null, null,
                    null, null, null, null, null
            );

            when(petRepositoryPort.findById(nonExistentPetId)).thenReturn(Optional.empty());

            // When/Then
            assertThrows(PetNotFoundException.class,
                    () -> petService.updatePet(nonExistentPetId, userId, request));

            verify(petRepositoryPort, never()).save(any(PetEntity.class));
        }

        @Test
        @DisplayName("Should throw UnauthorizedException when user is not the owner")
        void updatePet_whenNotOwner_throwsUnauthorizedException() {
            // Given
            String differentUserId = UUID.randomUUID().toString();
            UpdatePetRequest request = new UpdatePetRequest(
                    null, "NewName", null, null, null,
                    null, null, null, null, null
            );

            when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(testPet));

            // When/Then
            assertThrows(UnauthorizedException.class,
                    () -> petService.updatePet(petId, differentUserId, request));

            verify(petRepositoryPort, never()).save(any(PetEntity.class));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // updatePet - Specific Field Update Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("updatePet - Specific Field Tests")
    class UpdatePetSpecificFieldTests {

        @Test
        @DisplayName("Should update chipNumber when provided")
        void updatePet_whenChipNumberProvided_updatesChipNumber() {
            // Given
            String newChipNumber = "987654321098765";
            UpdatePetRequest request = new UpdatePetRequest(
                    null, null, null, null, null,
                    null, null, null, newChipNumber, null
            );

            when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(testPet));
            when(petRepositoryPort.save(any(PetEntity.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            Pet result = petService.updatePet(petId, userId, request);

            // Then
            assertEquals(newChipNumber, result.getChipNumber().value());
        }

        @Test
        @DisplayName("Should update petDescription when provided")
        void updatePet_whenDescriptionProvided_updatesDescription() {
            // Given
            String newDescription = "Updated description";
            UpdatePetRequest request = new UpdatePetRequest(
                    null, null, newDescription, null, null,
                    null, null, null, null, null
            );

            when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(testPet));
            when(petRepositoryPort.save(any(PetEntity.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            Pet result = petService.updatePet(petId, userId, request);

            // Then
            assertEquals(newDescription, result.getPetDescription().value());
        }

        @Test
        @DisplayName("Should update gender when provided")
        void updatePet_whenGenderProvided_updatesGender() {
            // Given
            UpdatePetRequest request = new UpdatePetRequest(
                    null, null, null, null, null,
                    null, null, "FEMALE", null, null
            );

            when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(testPet));
            when(petRepositoryPort.save(any(PetEntity.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            Pet result = petService.updatePet(petId, userId, request);

            // Then
            assertEquals(Gender.FEMALE, result.getGender());
        }

        @Test
        @DisplayName("Should update size when provided")
        void updatePet_whenSizeProvided_updatesSize() {
            // Given
            UpdatePetRequest request = new UpdatePetRequest(
                    null, null, null, null, null,
                    "LARGE", null, null, null, null
            );

            when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(testPet));
            when(petRepositoryPort.save(any(PetEntity.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            Pet result = petService.updatePet(petId, userId, request);

            // Then
            assertEquals(Size.LARGE, result.getSize());
        }

        @Test
        @DisplayName("Should update breed when provided")
        void updatePet_whenBreedProvided_updatesBreed() {
            // Given
            String newBreed = "Labrador";
            UpdatePetRequest request = new UpdatePetRequest(
                    null, null, null, null, newBreed,
                    null, null, null, null, null
            );

            when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(testPet));
            when(petRepositoryPort.save(any(PetEntity.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            Pet result = petService.updatePet(petId, userId, request);

            // Then
            assertEquals(newBreed, result.getBreed().value());
        }

        @Test
        @DisplayName("Should update officialPetName when provided")
        void updatePet_whenOfficialNameProvided_updatesOfficialName() {
            // Given
            String newOfficialName = "Maximilian";
            UpdatePetRequest request = new UpdatePetRequest(
                    newOfficialName, null, null, null, null,
                    null, null, null, null, null
            );

            when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(testPet));
            when(petRepositoryPort.save(any(PetEntity.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            Pet result = petService.updatePet(petId, userId, request);

            // Then
            assertEquals(newOfficialName, result.getOfficialPetName().value());
        }

        @Test
        @DisplayName("Should not update other fields  when ChipNumber is provided")
        void updatePet_whenChipNumberProvided_updatesOnlyChipNumber() {
            // Given
            when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(testPet));
            Pet originalPet = petService.getPetdById(petId);

            String newChipNumber = "987654321098765";
            UpdatePetRequest request = new UpdatePetRequest(
                    null, null, null, null, null,
                    null, null, null, newChipNumber, null
            );

            when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(testPet));
            when(petRepositoryPort.save(any(PetEntity.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            Pet result = petService.updatePet(petId, userId, request);

            // Then
            assertEquals(newChipNumber, result.getChipNumber().value());
            assertEquals(originalPet.getWorkingPetName().value(), result.getWorkingPetName().value());
            assertEquals(originalPet.getPetId().toString(), result.getPetId().toString());
            assertEquals(originalPet.getPetDescription().value(), result.getPetDescription().value());
            assertEquals(originalPet.getColor().value(), result.getColor().value());
            assertEquals(originalPet.getSize().toString(), result.getSize().toString());
            assertEquals( originalPet.getSpecies().toString(), result.getSpecies().toString());
            assertEquals( originalPet.getGender().toString(), result.getGender().toString());
            assertEquals( originalPet.getOfficialPetName().value(), result.getOfficialPetName().value());
            assertEquals(originalPet.getUserId().toString(), result.getUserId().toString());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // findById - Tests
    // ═══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return pet when found")
        void findById_whenFound_returnsPet() {
            // Given
            when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(testPet));

            // When
            Pet result = petService.getPetdById(petId);

            // Then
            assertNotNull(result);
            assertEquals(petId, result.getPetId().toString());
            verify(petRepositoryPort).findById(petId);
        }

        @Test
        @DisplayName("Should throw PetNotFoundException when not found")
        void findById_whenNotFound_throwsException() {
            // Given
            when(petRepositoryPort.findById("non-existent")).thenReturn(Optional.empty());

            // When/Then
            assertThrows(PetNotFoundException.class,
                    () -> petService.getPetdById("non-existent"));
        }
    }
}
