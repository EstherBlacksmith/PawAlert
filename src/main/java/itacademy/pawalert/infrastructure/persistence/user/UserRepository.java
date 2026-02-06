package itacademy.pawalert.infrastructure.persistence.user;

import itacademy.pawalert.domain.user.Role;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.model.Email;
import itacademy.pawalert.domain.user.model.PhoneNumber;
import itacademy.pawalert.domain.user.model.Surname;
import itacademy.pawalert.domain.user.model.Username;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByUsername(Username username);
    Optional<UserEntity> findByEmail(Email email);
    boolean existsBySurname(Surname surname);
    boolean existsByEmail(Email email);
    void delete(Optional<UserEntity> user);

    Role findRoleById(UUID userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.passwordHash = :newHash WHERE u.id = :userId")
    void updatePasswordHashById(@Param(value ="userId") UUID userId, @Param("newHash") String newHash);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.phonenumber = :phoneNumber WHERE u.id = :userId")
    User updatePhonenumber(@Param(value ="userId") UUID userId, @Param(value ="phoneNumber") PhoneNumber phoneNumber);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.surname = :surname WHERE u.id = :userId")
    User updateSurname(@Param(value = "userId") UUID userId, @Param(value ="surname") Surname surname);


    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.name = :name WHERE u.id = :userId")
    User updateName(@Param(value = "userId") UUID userId, @Param(value ="name") Username name);

    Optional<UserEntity> findBySurname(Surname surname);
}
