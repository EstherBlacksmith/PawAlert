package itacademy.pawalert.application.alert.service;

import itacademy.pawalert.application.pet.service.PetService;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.pet.model.Pet;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AlertNotificationFormatter {

    private final AlertService alertService;
    private final PetService petService;

    public AlertNotificationFormatter(AlertService alertService, PetService petService) {
        this.alertService = alertService;
        this.petService = petService;
    }

    public String formatStatusChangeMessage(UUID alertId, StatusNames oldStatus, StatusNames newStatus) {
        Alert alert = alertService.getAlertById(alertId);
        Pet pet = petService.getPetdById(alert.getPetId());

        return String.format(
                "🔔 <b>Alert PawAlert - Status Update</b>\n\n" +
                        "🐕 Pet: <b>%s</b>\n" +
                        "📊 Status changed: %s → %s\n\n" +
                        "🔗 View details: /alerts/%s",
                pet.getOfficialPetName(),
                oldStatus,
                newStatus,
                alertId
        );
    }

    public String formatEmailSubject(StatusNames status) {
        return "Alert updated: " + status;
    }

    public String formatEmailBody(UUID alertId, StatusNames oldStatus, StatusNames newStatus) {
        return formatStatusChangeMessage(alertId, oldStatus, newStatus);
    }
}
