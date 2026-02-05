package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.user.User;

public interface GetUserUseCase {
   User getById(String userId);
   User getByUsername(String userName);
   User getByEmail(String email);

   boolean existsByUsername(String username);
   boolean existsByEmail(String email);
}
