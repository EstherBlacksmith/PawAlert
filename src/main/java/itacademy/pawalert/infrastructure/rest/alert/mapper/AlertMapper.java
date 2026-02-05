package itacademy.pawalert.infrastructure.rest.alert.mapper;

import itacademy.pawalert.application.port.inbound.AlertWithContactDTO;
import itacademy.pawalert.domain.alert.model.*;
import itacademy.pawalert.infrastructure.rest.alert.dto.AlertDTO;
import itacademy.pawalert.domain.user.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AlertMapper {

    public AlertDTO toDTO(Alert alert) {
        return AlertDTO.builder()
                .id(alert.getId().toString())
                .petId(alert.getPetId().toString())
                .userId(alert.getUserID().value())
                .title(alert.getTitle().getValue())
                .description(alert.getDescription().getValue())
                .status(alert.currentStatus().getStatusName().name())
                .build();
    }

    public Alert toDomain(AlertDTO alertDTO, StatusAlert status) {
        Title title = new Title(alertDTO.getTitle());
        Description description = new Description(alertDTO.getDescription());
        UserId userId = new UserId(alertDTO.getUserId());

        return new Alert(
                UUID.fromString(alertDTO.getId()),
                UUID.fromString(alertDTO.getPetId()),
                userId,
                title,
                description,
                status);
    }

    public AlertWithContactDTO toWithContact(Alert alert, User creator) {
        return new AlertWithContactDTO(
                alert.getId().toString(),
                alert.getPetId().toString(),
                alert.getUserID().value(),
                alert.getTitle().getValue(),
                alert.getDescription().getValue(),
                alert.currentStatus().getStatusName().name(),
                creator.getPhoneNumber(),
                creator.getFullName()
        );
    }
}
