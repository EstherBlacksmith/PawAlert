package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.application.alert.model.AlertSearchCriteria;
import itacademy.pawalert.application.alert.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AlertRepositoryAdapter implements AlertRepositoryPort {

    private final AlertRepository alertRepository;

    public AlertRepositoryAdapter(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Override
    public Alert save(Alert alert) {
        //Domain to entity
        AlertEntity entity = alert.toEntity();
        //Persistence JPA
        AlertEntity saved = alertRepository.save(entity);

        return saved.toDomain();
    }


    @Override
    public Optional<Alert> findById(UUID alertId) {
        return alertRepository.findById(alertId.toString())
                .filter(entity -> entity.getDeletedAt() == null)
                .map(AlertEntity::toDomain);
    }

    @Override
    public List<Alert> findAllByPetId(UUID petId) {
        return alertRepository.findAllByPetId(petId.toString())
                .stream()
                .filter(e -> e.getDeletedAt() == null)
                .map(AlertEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(UUID alertId) {
        return alertRepository.existsById(alertId.toString());
    }

    @Override
    public void deleteById(UUID alertId) {
        alertRepository.deleteById(alertId.toString());
    }

    @Override
    public List<Alert> findAll() {
        Specification<AlertEntity> spec = AlertSpecifications.notDeleted();
        return alertRepository.findAll(spec)
                .stream()
                .map(AlertEntity::toDomain)
                .toList();
    }

    @Override
    public List<Alert> search(AlertSearchCriteria criteria) {
        Specification<AlertEntity> spec = AlertSpecifications.notDeleted();

        if (criteria.status() != null) {
            spec = spec.and(AlertSpecifications.withStatus(criteria.status()));
        }
        if (criteria.title() != null && !criteria.title().isBlank()) {
            spec = spec.and(AlertSpecifications.titleContains(criteria.title()));
        }
        if (criteria.petName() != null && !criteria.petName().isBlank()) {
            spec = spec.and(AlertSpecifications.petNameContains(criteria.petName()));
        }
        if (criteria.species() != null && !criteria.species().isBlank()) {
            spec = spec.and(AlertSpecifications.withPetSpecies(criteria.species()));
        }
        if (criteria.breed() != null && !criteria.breed().isBlank()) {
            spec = spec.and(AlertSpecifications.petBreedContains(criteria.breed()));
        }
        if (criteria.createdFrom() != null) {
            spec = spec.and(AlertSpecifications.createdAfter(criteria.createdFrom()));
        }
        if (criteria.createdTo() != null) {
            spec = spec.and(AlertSpecifications.createdBefore(criteria.createdTo()));
        }
        if (criteria.updatedFrom() != null) {
            spec = spec.and(AlertSpecifications.lastUpdatedAfter(criteria.updatedFrom()));
        }
        if (criteria.updatedTo() != null) {
            spec = spec.and(AlertSpecifications.lastUpdatedBefore(criteria.updatedTo()));
        }

        return alertRepository.findAll(spec)
                .stream()
                .map(AlertEntity::toDomain)
                .toList();
    }

    @Override
    public StatusNames getLastStatusById(UUID alertId) {
        return alertRepository.findLastStatusById(alertId.toString());
    }

    @Override
    public boolean existsActiveAlertByPetId(UUID petId) {
        return alertRepository.existsByPetIdAndStatusIn(petId.toString(), StatusNames.getActiveStatusNames());
    }

    @Override
    public Optional<Alert> findActiveAlertByPetId(UUID petId) {
        return alertRepository.findTopByPetIdAndStatusInOrderByCreatedAtDesc(petId.toString(), StatusNames.getActiveStatusNames())
                .filter(e -> e.getDeletedAt() == null)
                .map(AlertEntity::toDomain);
    }


}
