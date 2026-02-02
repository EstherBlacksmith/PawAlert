package itacademy.pawalert.infrastructure.persistence.pet;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository  extends JpaRepository<PetEntity, String> {
}
