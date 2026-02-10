package itacademy.pawalert.application.alert.service;

import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.pet.model.Pet;
import org.springframework.stereotype.Service;


@Service
public class AlertNotificationFormatter {

    public AlertNotificationFormatter() {}

    public String formatStatusChangeMessage(Alert alert, Pet pet, StatusNames oldStatus, StatusNames newStatus) {

        return String.format(
                "🔔 <b>Alert PawAlert - Status Update</b>\n\n" +
                        "🐕 Pet: <b>%s</b>\n" +
                        "📊 Status changed: %s → %s\n\n" +
                        "🔗 View details: /alerts/%s",
                pet.getOfficialPetName(),
                oldStatus,
                newStatus,
                alert.getId()
        );
    }

    public String formatEmailSubject(StatusNames status) {
        return "Alert updated: " + status;
    }

    public String formatEmailBody(Alert alert, Pet pet, StatusNames oldStatus, StatusNames newStatus) {
        return formatStatusChangeMessage(alert, pet, oldStatus, newStatus);    }
}
