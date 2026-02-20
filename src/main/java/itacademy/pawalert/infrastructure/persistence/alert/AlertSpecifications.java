package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.infrastructure.persistence.pet.PetEntity;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

public final class AlertSpecifications {

    private AlertSpecifications() {} // Utility class

    public static Specification<AlertEntity> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<AlertEntity> withStatus(StatusNames status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction();
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<AlertEntity> titleContains(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<AlertEntity> createdBy(UUID userId) {
        return (root, query, cb) -> {
            if (userId == null) return cb.conjunction();
            return cb.equal(root.get("userId"), userId.toString());
        };
    }

    public static Specification<AlertEntity> createdAfter(LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) return cb.conjunction();
            return cb.greaterThanOrEqualTo(root.get("createdAt"), date);
        };
    }

    public static Specification<AlertEntity> createdBefore(LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) return cb.conjunction();
            return cb.lessThanOrEqualTo(root.get("createdAt"), date);
        };
    }

    public static Specification<AlertEntity> lastUpdatedAfter(LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) return cb.conjunction();

            Subquery<LocalDateTime> subquery = query.subquery(LocalDateTime.class);
            Root<AlertEventEntity> eventRoot = subquery.from(AlertEventEntity.class);
            subquery.select(cb.greatest(eventRoot.get("changedAt")));
            subquery.where(cb.equal(eventRoot.get("alert").get("id"), root.get("id")));

            return cb.greaterThanOrEqualTo(subquery, date);
        };
    }

    public static Specification<AlertEntity> lastUpdatedBefore(LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) return cb.conjunction();

            Subquery<LocalDateTime> subquery = query.subquery(LocalDateTime.class);
            Root<AlertEventEntity> eventRoot = subquery.from(AlertEventEntity.class);
            subquery.select(cb.greatest(eventRoot.get("changedAt")));
            subquery.where(cb.equal(eventRoot.get("alert").get("id"), root.get("id")));

            return cb.lessThanOrEqualTo(subquery, date);
        };
    }

    public static Specification<AlertEntity> petNameContains(String petName) {
        return (root, query, cb) -> {
            if (petName == null || petName.isBlank()) return cb.conjunction();

            // Subquery: buscar pets cuyo nombre coincida
            Subquery<String> subquery = query.subquery(String.class);
            Root<PetEntity> petRoot = subquery.from(PetEntity.class);
            subquery.select(petRoot.get("id"));
            subquery.where(
                    cb.or(
                            cb.like(cb.lower(petRoot.get("officialPetName")), "%" + petName.toLowerCase() + "%"),
                            cb.like(cb.lower(petRoot.get("workingPetName")), "%" + petName.toLowerCase() + "%")
                    )
            );

            // Alert.petId IN (subquery)
            return root.get("petId").in(subquery);
        };
    }

    public static Specification<AlertEntity> withPetId(UUID petId) {
        return (root, query, cb) -> {
            if (petId == null) return cb.conjunction();
            return cb.equal(root.get("petId"), petId.toString());
        };
    }

    public static Specification<AlertEntity> petBreedContains(String breed) {
        return (root, query, cb) -> {
            if (breed == null || breed.isBlank()) return cb.conjunction();

            Subquery<String> subquery = query.subquery(String.class);
            Root<PetEntity> petRoot = subquery.from(PetEntity.class);
            subquery.select(petRoot.get("id"));
            subquery.where(cb.like(cb.lower(petRoot.get("breed")), "%" + breed.toLowerCase() + "%"));

            return root.get("petId").in(subquery);
        };
    }


    public static Specification<AlertEntity> withPetSpecies(String species) {
        return (root, query, cb) -> {
            if (species == null || species.isBlank()) return cb.conjunction();

            Subquery<String> subquery = query.subquery(String.class);
            Root<PetEntity> petRoot = subquery.from(PetEntity.class);
            subquery.select(petRoot.get("id"));
            subquery.where(cb.equal(cb.lower(petRoot.get("species")), species.toLowerCase()));

            return root.get("petId").in(subquery);
        };
    }
}
