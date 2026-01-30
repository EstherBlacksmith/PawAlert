package itacademy.pawalert.application.service;

import itacademy.pawalert.application.AlertNotFoundException;
import itacademy.pawalert.domain.StatusNames;
import itacademy.pawalert.infrastructure.persistence.AlertEntity;
import itacademy.pawalert.infrastructure.persistence.AlertRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {
    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public AlertEntity createOpenedAlert(long petId, String title, String description) {
        AlertEntity alertEntity = new AlertEntity(petId,title,description, StatusNames.OPENED);
       return  alertRepository.save(alertEntity);
    }

    public List<AlertEntity> findAllByPetId(Long petId) {
        return alertRepository.findAllByPetId(petId)
                .stream().filter(alertEntity -> alertEntity.getPetId().equals(petId)).toList();
    }

    public AlertEntity findById(Long AlertId){
        return alertRepository.findById(AlertId)
                .orElseThrow(() -> new AlertNotFoundException("Alert not found: " + AlertId));
    }
}
