package itacademy.pawalert.infrastructure.rest.dto;

import itacademy.pawalert.domain.StatusNames;
import itacademy.pawalert.domain.UserId;
import jakarta.validation.constraints.NotNull;

public record StatusChangeRequest(
        @NotNull StatusNames newStatus,
        @NotNull UserId userId
) {}



