package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.application.alert.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.domain.alert.model.Alert;
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
                .map(AlertEntity::toDomain);
    }

    @Override
    public List<Alert> findAllByPetId(UUID petId) {
        return alertRepository.findAllByPetId(petId.toString())
                .stream()
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
        return alertRepository.findAll()
                .stream()
                .map(AlertEntity::toDomain)
                .toList();
    }

    @Override
    public List<Alert> findAll(Specification<Alert> spec) {
        return alertRepository.findAll(spec);
    }

}
