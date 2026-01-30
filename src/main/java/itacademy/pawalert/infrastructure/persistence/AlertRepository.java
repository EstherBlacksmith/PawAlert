package itacademy.pawalert.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<AlertEntity, Long> {
    List<AlertEntity> findByStatus(String status);
    List <AlertEntity> findAllByPetId(Long petID);
}
