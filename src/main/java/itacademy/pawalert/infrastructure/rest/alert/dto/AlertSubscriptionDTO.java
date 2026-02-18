package itacademy.pawalert.infrastructure.rest.alert.dto;

import java.time.LocalDateTime;

public record AlertSubscriptionDTO(
        String id,
        String alertId,
        String userId,
        boolean active,
        LocalDateTime subscribedAt
) {}