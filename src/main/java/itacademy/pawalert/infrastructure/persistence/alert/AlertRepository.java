package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface AlertRepository extends JpaRepository<AlertEntity, UUID>,
        JpaSpecificationExecutor<AlertEntity> {
    List<AlertEntity> findByStatus(String status);
    List<AlertEntity> findAllByPetId(UUID petId);
    StatusNames findLastStatusById(UUID alertId);
}
