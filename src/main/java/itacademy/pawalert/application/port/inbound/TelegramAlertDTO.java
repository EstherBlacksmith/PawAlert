package itacademy.pawalert.application.port.inbound;

import java.util.UUID;

public record TelegramAlertDTO(
        UUID alertId,
        String petName,
        String location,
        String status
) {}