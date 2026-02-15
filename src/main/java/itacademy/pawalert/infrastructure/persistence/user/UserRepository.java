package itacademy.pawalert.infrastructure.persistence.user;

import itacademy.pawalert.domain.user.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    @Query("SELECT u FROM UserEntity u WHERE u.username = :username")
    Optional<UserEntity> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email")
    Optional<UserEntity> findByEmail(@Param("email") String email);

    @Query("SELECT COUNT(u) > 0 FROM UserEntity u WHERE u.surname = :surname")
    boolean existsBySurname(@Param("surname") String surname);

    @Query("SELECT COUNT(u) > 0 FROM UserEntity u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT u.role FROM UserEntity u WHERE u.id = :userId")
    Role findRoleById(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.passwordHash = :newHash WHERE u.id = :userId")
    void updatePasswordHashById(@Param(value ="userId") UUID userId, @Param("newHash") String newHash);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.phoneNumber = :phoneNumber WHERE u.id = :userId")
    void updatePhoneNumber(@Param(value ="userId") UUID userId, @Param("phoneNumber") String phoneNumber);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.surname = :surname WHERE u.id = :userId")
    void updateSurname(@Param(value = "userId") UUID userId, @Param("surname") String surname);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.username = :username WHERE u.id = :userId")
    void updateUsername(@Param(value = "userId") UUID userId, @Param("username") String username);

    @Query("SELECT u FROM UserEntity u WHERE u.surname = :surname")
    Optional<UserEntity> findBySurname(@Param("surname") String surname);

}
