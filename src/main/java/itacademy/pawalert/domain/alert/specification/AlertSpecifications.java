package itacademy.pawalert.domain.alert.specification;

import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.pet.model.Pet;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class AlertSpecifications {

    private AlertSpecifications() {} // Utility class

    // Filter by state
    public static Specification<Alert> withStatus(StatusNames status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction(); // Si null, no filtra
            return cb.equal(root.get("status"), status);
        };
    }

    // Filter by title, case-sensitive
    public static Specification<Alert> titleContains(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) return cb.conjunction();
            return cb.like(
                    cb.lower(root.get("title")),
                    "%" + title.toLowerCase() + "%"
            );
        };
    }

    // Filter by user creator
    public static Specification<Alert> createdBy(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) return cb.conjunction();
            return cb.equal(root.get("user").get("id"), userId);
        };
    }

    //Filter by date of creation after
    public static Specification<Alert> createdAfter(LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) return cb.conjunction();
            return cb.greaterThanOrEqualTo(root.get("createdAt"), date);
        };
    }

    //Filter by date of creation before
    public static Specification<Alert> createdBefore(LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) return cb.conjunction();
            return cb.lessThanOrEqualTo(root.get("createdAt"), date);
        };
    }

    //Filter by Pet name
    public static Specification<Alert> petNameContains(String petName) {
        return (root, query, cb) -> {
            if (petName == null || petName.isBlank()) {
                return cb.conjunction(); // Si null, no filtra
            }

            // JOIN: Alert → Pet
            Join<Alert, Pet> petJoin = root.join("pet", JoinType.INNER);

            // Look in pet.name(case-insensitive)
            return cb.like(
                    cb.lower(petJoin.get("name")),
                    "%" + petName.toLowerCase() + "%"
            );
        };
    }


    //Filter by Pet id
    public static Specification<Alert> withPetId(Long petId) {
        return (root, query, cb) -> {
            if (petId == null) return cb.conjunction();
            return cb.equal(root.get("pet").get("id"), petId);
        };
    }

    //Filter by Breed
    public static Specification<Alert> petBreedContains(String breed) {
        return (root, query, cb) -> {
            if (breed == null || breed.isBlank()) return cb.conjunction();
            Join<Alert, Pet> petJoin = root.join("pet", JoinType.INNER);
            return cb.like(cb.lower(petJoin.get("breed")), "%" + breed.toLowerCase() + "%");
        };
    }

    //Filter by specie
    public static Specification<Alert> withPetSpecies(String species) {
        return (root, query, cb) -> {
            if (species == null || species.isBlank()) return cb.conjunction();
            Join<Alert, Pet> petJoin = root.join("pet", JoinType.INNER);
            return cb.equal(cb.lower(petJoin.get("species")), species.toLowerCase());
        };
    }
}
