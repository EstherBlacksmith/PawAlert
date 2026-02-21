package itacademy.pawalert.application.user.port.inbound;

import itacademy.pawalert.domain.user.model.Email;

import java.util.UUID;

public interface DeleteUserUseCase {
    void deleteByEmail(Email email);

    void deleteById(UUID userId);
}
