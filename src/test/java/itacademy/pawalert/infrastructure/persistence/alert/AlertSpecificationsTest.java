package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.domain.alert.model.StatusNames;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertSpecificationsTest {

    @Mock
    private Root<AlertEntity> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Path<Object> objectPath;

    @Mock
    private Expression<String> stringExpression;

    @Mock
    private Expression<LocalDateTime> dateTimeExpression;

    @Mock
    private Predicate predicate;

    // ==================== NOT DELETED SPECIFICATION ====================

    @Test
    @DisplayName("notDeleted should check deletedAt is null")
    void notDeleted_shouldCheckDeletedAtIsNull() {
        // Given
        doReturn(objectPath).when(root).get("deletedAt");
        when(cb.isNull(objectPath)).thenReturn(predicate);

        // When
        Specification<AlertEntity> spec = AlertSpecifications.notDeleted();
        spec.toPredicate(root, query, cb);

        // Then
        verify(root).get("deletedAt");
        verify(cb).isNull(objectPath);
    }

    // ==================== STATUS SPECIFICATION ====================

    @Test
    @DisplayName("withStatus should check status equality")
    void withStatus_shouldCheckStatusEquality() {
        // Given
        doReturn(objectPath).when(root).get("status");
        when(cb.equal(objectPath, StatusNames.OPENED)).thenReturn(predicate);

        // When
        Specification<AlertEntity> spec = AlertSpecifications.withStatus(StatusNames.OPENED);
        spec.toPredicate(root, query, cb);

        // Then
        verify(root).get("status");
        verify(cb).equal(objectPath, StatusNames.OPENED);
    }

    @Test
    @DisplayName("withStatus with null should return conjunction")
    void withStatus_withNull_shouldReturnConjunction() {
        // Given
        when(cb.conjunction()).thenReturn(predicate);

        // When
        Specification<AlertEntity> spec = AlertSpecifications.withStatus(null);
        spec.toPredicate(root, query, cb);

        // Then
        verify(cb).conjunction();
        verify(root, never()).get(anyString());
    }

    // ==================== TITLE SPECIFICATION ====================

    @Test
    @DisplayName("titleContains should use like with lowercase")
    void titleContains_shouldUseLikeWithLowercase() {
        // Given
        doReturn(objectPath).when(root).get("title");
        when(cb.lower(any(Expression.class))).thenReturn(stringExpression);
        when(cb.like(any(Expression.class), anyString())).thenReturn(predicate);

        // When
        Specification<AlertEntity> spec = AlertSpecifications.titleContains("Lost");
        spec.toPredicate(root, query, cb);

        // Then
        verify(cb).lower(any(Expression.class));
        verify(cb).like(any(Expression.class), eq("%lost%"));
    }

    // ==================== DATE SPECIFICATIONS ====================

    @Test
    @DisplayName("createdAfter should use greaterThanOrEqualTo")
    void createdAfter_shouldUseGreaterThanOrEqualTo() {
        // Given
        LocalDateTime date = LocalDateTime.now();
        doReturn(objectPath).when(root).get("createdAt");
        when(cb.greaterThanOrEqualTo(any(Expression.class), eq(date))).thenReturn(predicate);

        // When
        Specification<AlertEntity> spec = AlertSpecifications.createdAfter(date);
        spec.toPredicate(root, query, cb);

        // Then
        verify(cb).greaterThanOrEqualTo(any(Expression.class), eq(date));
    }

    @Test
    @DisplayName("createdBefore should use lessThanOrEqualTo")
    void createdBefore_shouldUseLessThanOrEqualTo() {
        // Given
        LocalDateTime date = LocalDateTime.now();
        doReturn(objectPath).when(root).get("createdAt");
        when(cb.lessThanOrEqualTo(any(Expression.class), eq(date))).thenReturn(predicate);

        // When
        Specification<AlertEntity> spec = AlertSpecifications.createdBefore(date);
        spec.toPredicate(root, query, cb);

        // Then
        verify(cb).lessThanOrEqualTo(any(Expression.class), eq(date));
    }

    // ==================== PET ID SPECIFICATION ====================

    @Test
    @DisplayName("withPetId should check petId equality")
    void withPetId_shouldCheckPetIdEquality() {
        // Given
        UUID petId = UUID.randomUUID();
        doReturn(objectPath).when(root).get("petId");
        when(cb.equal(objectPath, petId.toString())).thenReturn(predicate);

        // When
        Specification<AlertEntity> spec = AlertSpecifications.withPetId(petId);
        spec.toPredicate(root, query, cb);

        // Then
        verify(root).get("petId");
        verify(cb).equal(objectPath, petId.toString());
    }
}
