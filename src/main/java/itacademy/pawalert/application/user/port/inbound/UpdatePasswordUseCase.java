package itacademy.pawalert.application.user.port.inbound;

import itacademy.pawalert.domain.user.model.Password;

import java.util.UUID;

public interface UpdatePasswordUseCase {
    void changePassword(UUID userId, Password currentPassword, Password newPassword);
}