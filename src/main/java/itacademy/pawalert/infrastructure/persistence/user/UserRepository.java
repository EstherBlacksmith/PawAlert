package itacademy.pawalert.infrastructure.persistence.user;

import itacademy.pawalert.domain.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    boolean existsBySurname(String surname);

    boolean existsByEmail(String email);

    Optional<UserEntity> findBySurname(String surname);

    boolean existsByRole(Role role);

    long countByRole(Role role);
}
