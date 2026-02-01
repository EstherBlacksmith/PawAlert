package itacademy.pawalert.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<AlertEntity, String> {
    List<AlertEntity> findByStatus(String status);

    List<AlertEntity> findAllByPetId(String petID);
}
