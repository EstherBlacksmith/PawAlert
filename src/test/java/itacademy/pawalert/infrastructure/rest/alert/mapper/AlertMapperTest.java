package itacademy.pawalert.infrastructure.rest.alert.mapper;

import itacademy.pawalert.application.alert.port.outbound.AlertEventRepositoryPort;
import itacademy.pawalert.domain.alert.model.*;
import itacademy.pawalert.infrastructure.persistence.alert.AlertEntity;
import itacademy.pawalert.infrastructure.rest.alert.dto.AlertDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("AlertMapper Tests")
@ExtendWith(MockitoExtension.class)
class AlertMapperTest {

    @Mock
    private AlertEventRepositoryPort eventRepository;

    private AlertMapper alertMapper;

    @BeforeEach
    void setUp() {
        alertMapper = new AlertMapper(eventRepository);
    }

    @Nested
    @DisplayName("toDTO Tests")
    class ToDTOTests {

        @Test
        @DisplayName("Should map Alert to AlertDTO correctly")
        void shouldMapAlertToDTO() {
            UUID alertId = UUID.randomUUID();
            UUID petId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            Alert alert = new Alert(
                    alertId, petId, userId,
                    Title.of("Lost Dog"),
                    Description.of("Golden Retriever"),
                    new OpenedStateAlert()
            );

            when(eventRepository.findLatestByAlertId(alertId)).thenReturn(Optional.empty());

            AlertDTO dto = alertMapper.toDTO(alert);

            assertEquals(alertId.toString(), dto.getId());
            assertEquals(petId.toString(), dto.getPetId());
            assertEquals("Lost Dog", dto.getTitle());
            assertEquals("Golden Retriever", dto.getDescription());
            assertEquals(StatusNames.OPENED.toString(), dto.getStatus());
        }
    }

    @Nested
    @DisplayName("toEntity Tests")
    class ToEntityTests {

        @Test
        @DisplayName("Should map Alert to AlertEntity correctly")
        void shouldMapAlertToEntity() {
            UUID alertId = UUID.randomUUID();
            UUID petId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            Alert alert = new Alert(
                    alertId, petId, userId,
                    Title.of("Lost Dog"),
                    Description.of("Golden Retriever"),
                    new SeenStatusAlert()
            );

            AlertEntity entity =alert.toEntity();

            assertEquals(alertId.toString(), entity.getId());
            assertEquals(petId.toString(), entity.getPetId());
            assertEquals(StatusNames.SEEN.toString(), entity.getStatus());
        }
    }
}
