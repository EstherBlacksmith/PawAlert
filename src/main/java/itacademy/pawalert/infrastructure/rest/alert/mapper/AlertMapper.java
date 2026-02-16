
package itacademy.pawalert.infrastructure.rest.alert.mapper;

import itacademy.pawalert.application.alert.port.outbound.AlertEventRepositoryPort;
import itacademy.pawalert.domain.alert.model.*;
import itacademy.pawalert.infrastructure.rest.alert.dto.AlertDTO;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.infrastructure.rest.alert.dto.AlertEventDTO;
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
        // Get the latest event for location
        AlertEvent latestEvent = eventRepository
                .findLatestByAlertId(alert.getId())
                .orElse(null);
        
        GeographicLocation location = latestEvent != null ? latestEvent.getLocation() : null;

        // Get closure reason from the closure event (if alert is closed)
        ClosureReason closureReason = null;
        if (alert.currentStatus().getStatusName() == StatusNames.CLOSED) {
            // Find the closure event to get the reason
            closureReason = eventRepository.findByAlertIdOrderByChangedAtDesc(alert.getId()).stream()
                    .filter(e -> e.getNewStatus() == StatusNames.CLOSED && e.getClosureReason() != null)
                    .findFirst()
                    .map(AlertEvent::getClosureReason)
                    .orElse(null);
        }

        return AlertDTO.builder()
                .id(alert.getId().toString())
                .petId(alert.getPetId().toString())
                .userId(alert.getUserId().toString())
                .title(alert.getTitle().getValue())
                .description(alert.getDescription().getValue())
                .status(alert.currentStatus().getStatusName().name())
                .latitude(location != null ? location.latitude() : null)
                .longitude(location != null ? location.longitude() : null)
                .closureReason(closureReason != null ? closureReason.name() : null)
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

    public AlertEventDTO toEventDTO(AlertEvent event) {
        GeographicLocation location = event.getLocation();
        return AlertEventDTO.builder()
                .id(event.getId().toString())
                .alertId(event.getAlertId() != null ? event.getAlertId().toString() : null)
                .eventType(event.getEventType().name())
                .previousStatus(event.getPreviousStatus() != null ? event.getPreviousStatus().name() : null)
                .newStatus(event.getNewStatus() != null ? event.getNewStatus().name() : null)
                .oldValue(event.getOldValue())
                .newValue(event.getNewValue())
                .latitude(location != null ? location.latitude() : null)
                .longitude(location != null ? location.longitude() : null)
                .closureReason(event.getClosureReason() != null ? event.getClosureReason().name() : null)
                .changedBy(event.getChangedBy().toString())
                .changedAt(event.getChangedAt().value())
                .build();
    }

    public List<AlertEventDTO> toEventDTOList(List<AlertEvent> events) {
        return events.stream()
                .map(this::toEventDTO)
                .toList();
    }

}
