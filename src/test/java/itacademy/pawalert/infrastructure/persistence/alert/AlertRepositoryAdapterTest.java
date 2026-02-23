package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.application.alert.model.AlertSearchCriteria;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertRepositoryAdapterTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertRepositoryAdapter adapter;

    // ==================== SOFT DELETE FILTER TESTS ====================

    @Test
    @DisplayName("findById should filter out soft deleted alerts")
    void findById_shouldFilterSoftDeleted() {
        // Given
        UUID alertId = UUID.randomUUID();
        AlertEntity entity = new AlertEntity(
                alertId.toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "Test Alert",
                "Description",
                StatusNames.OPENED
        );
        // deletedAt is null by default
        when(alertRepository.findById(alertId.toString())).thenReturn(Optional.of(entity));

        // When
        Optional<Alert> result = adapter.findById(alertId);

        // Then
        assertThat(result).isPresent();
        verify(alertRepository).findById(alertId.toString());
    }

    @Test
    @DisplayName("findById should return empty for soft deleted alert")
    void findById_shouldReturnEmptyForSoftDeleted() {
        // Given
        UUID alertId = UUID.randomUUID();
        AlertEntity entity = mock(AlertEntity.class);
        when(entity.getDeletedAt()).thenReturn(LocalDateTime.now());
        when(entity.getId()).thenReturn(alertId.toString());
        when(alertRepository.findById(alertId.toString())).thenReturn(Optional.of(entity));

        // When
        Optional<Alert> result = adapter.findById(alertId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAll should use notDeleted specification")
    void findAll_shouldUseNotDeletedSpecification() {
        // Given
        AlertEntity entity = new AlertEntity(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "Test Alert",
                "Description",
                StatusNames.OPENED
        );
        when(alertRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(entity));

        // When
        List<Alert> result = adapter.findAll();

        // Then
        assertThat(result).hasSize(1);
        verify(alertRepository).findAll(any(Specification.class));
    }

    // ==================== SEARCH CRITERIA TESTS ====================

    @Test
    @DisplayName("search with empty criteria should return all non-deleted alerts")
    void search_withEmptyCriteria_shouldReturnAllNonDeleted() {
        // Given
        AlertEntity entity = new AlertEntity(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "Test Alert",
                "Description",
                StatusNames.OPENED
        );
        when(alertRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(entity));

        AlertSearchCriteria criteria = AlertSearchCriteria.empty();

        // When
        List<Alert> result = adapter.search(criteria);

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("search with status should apply status filter")
    void search_withStatus_shouldApplyStatusFilter() {
        // Given
        AlertEntity entity = new AlertEntity(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "Test Alert",
                "Description",
                StatusNames.OPENED
        );
        when(alertRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(entity));

        AlertSearchCriteria criteria = new AlertSearchCriteria(
                StatusNames.OPENED, null, null, null, null, null, null, null, null, null
        );

        // When
        List<Alert> result = adapter.search(criteria);

        // Then
        assertThat(result).hasSize(1);
        verify(alertRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("search with all criteria should apply all filters")
    void search_withAllCriteria_shouldApplyAllFilters() {
        // Given
        AlertEntity entity = new AlertEntity(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "Lost Dog",
                "Description",
                StatusNames.OPENED
        );
        when(alertRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(entity));

        AlertSearchCriteria criteria = new AlertSearchCriteria(
                StatusNames.OPENED,
                "Lost",
                "Fluffy",
                "DOG",
                "Labrador",
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now(),
                null,
                null,
                null
        );

        // When
        List<Alert> result = adapter.search(criteria);

        // Then
        assertThat(result).hasSize(1);
        verify(alertRepository).findAll(any(Specification.class));
    }
}
