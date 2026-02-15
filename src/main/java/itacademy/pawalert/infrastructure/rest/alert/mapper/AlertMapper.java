
package itacademy.pawalert.infrastructure.rest.alert.mapper;

import itacademy.pawalert.application.alert.port.outbound.AlertEventRepositoryPort;
import itacademy.pawalert.domain.alert.model.*;
import itacademy.pawalert.infrastructure.rest.alert.dto.AlertDTO;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.infrastructure.rest.alert.dto.AlertWithContactDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AlertMapper {
    
    private final AlertEventRepositoryPort eventRepository;

    public AlertMapper(AlertEventRepositoryPort eventRepository) {
        this.eventRepository = eventRepository;
    }

    public AlertDTO toDTO(Alert alert) {
        GeographicLocation location = eventRepository
                .findLatestByAlertId(alert.getId())
                .map(AlertEvent::getLocation)
                .orElse(null);

        return AlertDTO.builder()
                .id(alert.getId().toString())
                .petId(alert.getPetId().toString())
                .userId(alert.getUserId().toString())
                .title(alert.getTitle().getValue())
                .description(alert.getDescription().getValue())
                .status(alert.currentStatus().getStatusName().name())
                .latitude(location != null ? location.latitude() : null)
                .longitude(location != null ? location.longitude() : null)
                .build();
    }

    public Alert toDomain(AlertDTO alertDTO, StatusAlert status) {
        Title title = Title.of(alertDTO.getTitle());
        Description description = Description.of(alertDTO.getDescription());
        UUID userId = UUID.fromString(alertDTO.getUserId());

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
                alert.getId(),
                alert.getPetId(),
                alert.getUserId(),
                alert.getTitle(),
                alert.getDescription(),
                alert.currentStatus().getStatusName(),
                creator.getPhoneNumber(),
                creator.getSurname()
        );
    }
    public List<AlertDTO> toDTOList(List<Alert> alerts) {
        return alerts.stream()
                .map(this::toDTO)
                .toList();
    }
}
