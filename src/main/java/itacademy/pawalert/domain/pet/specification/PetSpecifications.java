package itacademy.pawalert.domain.pet.specification;

import itacademy.pawalert.domain.pet.model.Pet;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class PetSpecifications {

    private PetSpecifications() {
    }

    public static Specification<Pet> byOwner(UUID ownerId) {
        return (root, query, cb) -> {
            if (ownerId == null) return cb.conjunction();
            return cb.equal(root.get("userId"), ownerId.toString());
        };
    }


    public static Specification<Pet> breedContains(String breed) {
        return (petRoot, query, criteriaBuilder) -> {
            if (breed == null || breed.isBlank()) return criteriaBuilder.conjunction();
            return criteriaBuilder.like(criteriaBuilder.lower(petRoot.get("breed")), "%" + breed.toLowerCase() + "%");
        };
    }

    public static Specification<Pet> speciesEquals(String species) {
        return (petRoot, query, criteriaBuilder) -> {
            if (species == null || species.isBlank()) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(criteriaBuilder.lower(petRoot.get("species")), species.toLowerCase());
        };
    }

    public static Specification<Pet> nameContains(String name) {
        return (petRoot, query, criteriaBuilder) -> {
            if (name == null || name.isBlank()) return criteriaBuilder.conjunction();

            String searchPattern = "%" + name.toLowerCase() + "%";

            // Search in officialPetName OR workingPetName
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(petRoot.get("officialPetName")), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(petRoot.get("workingPetName")), searchPattern)
            );
        };
    }

    public static Specification<Pet> sortByOfficialName() {
        return (petRoot, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.asc(petRoot.get("officialPetName")));
            return null;
        };
    }

    public static Specification<Pet> sortByWorkingName() {
        return (petRoot, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.asc(petRoot.get("workingPetName")));
            return null;
        };
    }

    public static Specification<Pet> genderEquals(String gender) {
        return (root, query, cb) -> {
            if (gender == null || gender.isBlank()) return cb.conjunction();
            return cb.equal(cb.lower(root.get("gender")), gender.toLowerCase());
        };
    }

    public static Specification<Pet> sizeEquals(String size) {
        return (root, query, cb) -> {
            if (size == null || size.isBlank()) return cb.conjunction();
            return cb.equal(cb.lower(root.get("size")), size.toLowerCase());
        };
    }

}
