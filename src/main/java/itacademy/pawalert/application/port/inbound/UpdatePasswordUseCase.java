package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.user.model.Password;

import java.util.UUID;

public interface UpdatePasswordUseCase {
    void changePassword(UUID userId, Password currentPassword, Password newPassword);
}