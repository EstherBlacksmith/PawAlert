package itacademy.pawalert.infrastructure.rest.alert.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for exposing AlertEvent data through the REST API.
 * Contains all event information including location data for route mapping.
 */
@Data
@Builder
public class AlertEventDTO {
    
    /**
     * Unique identifier of the event
     */
    private String id;
    
    /**
     * ID of the alert this event belongs to
     */
    private String alertId;
    
    /**
     * Type of event: STATUS_CHANGED, TITLE_CHANGED, or DESCRIPTION_CHANGED
     */
    private String eventType;
    
    /**
     * Previous status (for STATUS_CHANGED events)
     */
    private String previousStatus;
    
    /**
     * New status (for STATUS_CHANGED events)
     */
    private String newStatus;
    
    /**
     * Old value (for TITLE_CHANGED or DESCRIPTION_CHANGED events)
     */
    private String oldValue;
    
    /**
     * New value (for TITLE_CHANGED or DESCRIPTION_CHANGED events)
     */
    private String newValue;
    
    /**
     * Latitude where the event occurred (for STATUS_CHANGED events with location)
     */
    private Double latitude;
    
    /**
     * Longitude where the event occurred (for STATUS_CHANGED events with location)
     */
    private Double longitude;
    
    /**
     * Reason for closure (for CLOSED status events)
     */
    private String closureReason;
    
    /**
     * ID of the user who made the change
     */
    private String changedBy;
    
    /**
     * Timestamp when the event occurred
     */
    private LocalDateTime changedAt;
}
