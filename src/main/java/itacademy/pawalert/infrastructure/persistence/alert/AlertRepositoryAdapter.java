package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.application.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.domain.alert.model.Alert;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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
    public Optional<Alert> findById(String alertId) {
        return alertRepository.findById(alertId)
                .map(AlertEntity::toDomain);
    }

    @Override
    public List<Alert> findAllByPetId(String petId) {
        return alertRepository.findAllByPetId(petId)
                .stream()
                .map(AlertEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(String alertId) {
        return alertRepository.existsById(alertId);
    }
}
