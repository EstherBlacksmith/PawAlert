package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.model.Email;
import itacademy.pawalert.infrastructure.rest.user.dto.RegistrationInput;

public interface CreateUserUseCase {
    User register(RegistrationInput input);
    boolean existsByEmail(Email email);
}
