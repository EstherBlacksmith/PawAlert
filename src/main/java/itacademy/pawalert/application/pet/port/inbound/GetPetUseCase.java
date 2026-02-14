package itacademy.pawalert.application.pet.port.inbound;

import itacademy.pawalert.domain.pet.model.*;
import itacademy.pawalert.infrastructure.persistence.pet.PetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public interface GetPetUseCase  {
      Pet getPetById(UUID petId);
      List<Pet> getAllPetsByUserId(UUID userId);
      List<Pet> searchPets(Specification<PetEntity> spec, Sort sort);
      Page<Pet> searchPets(Specification<PetEntity> spec, Pageable pageable);


}
