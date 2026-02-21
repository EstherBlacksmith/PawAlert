package itacademy.pawalert.application.user.port.inbound;

import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.model.Email;
import itacademy.pawalert.domain.user.model.Surname;
import itacademy.pawalert.domain.user.model.Username;

import java.util.List;
import java.util.UUID;

public interface GetUserUseCase {
   User getById(UUID userId);

   User getByUsername(Username username);

   User getByEmail(Email email);
   boolean existsBySurname(Surname surname);
   boolean existsByEmail(Email email);
   User getBySurname(Surname surname);
   List<User> getAllUsers();

}
