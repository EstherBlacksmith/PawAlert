package itacademy.pawalert.application.service;

import itacademy.pawalert.application.AlertNotFoundException;
import itacademy.pawalert.domain.Alert;
import itacademy.pawalert.domain.Description;
import itacademy.pawalert.domain.Tittle;
import itacademy.pawalert.infrastructure.persistence.AlertEntity;
import itacademy.pawalert.infrastructure.persistence.AlertRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AlertService {
    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public Alert createOpenedAlert(String petId, String tittleString, String descriptionString) {

        Tittle tittle = new Tittle(tittleString);
        Description description = new Description(descriptionString);
        Alert alert = new Alert( UUID.fromString(petId), tittle, description);

        AlertEntity entity = alert.toEntity();
        AlertEntity saved = alertRepository.save(entity);
        return saved.toDomain();
    }

    public List<Alert> findAllByPetId(String petId) {
        return alertRepository.findAllByPetId(petId)
                .stream().map(AlertEntity::toDomain)
                .filter(alert -> alert.getPetId().toString().equals(petId))
                .toList();
    }

    public Alert findById(String alertId){
        return alertRepository.findById(alertId)
                .map(AlertEntity::toDomain)
                .orElseThrow(() -> new AlertNotFoundException("Alert not found: " + alertId));
    }
}
