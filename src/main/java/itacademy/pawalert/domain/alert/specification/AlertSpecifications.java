package itacademy.pawalert.domain.alert.specification;

import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.pet.model.Pet;
import itacademy.pawalert.infrastructure.persistence.alert.AlertEntity;
import itacademy.pawalert.infrastructure.persistence.alert.AlertEventEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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

    // Filter by state
    public static Specification<AlertEntity> withStatus(StatusNames status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction(); // Si null, no filtra
            return cb.equal(root.get("status"), status);
        };
    }

    // Filter by title, case-sensitive
    public static Specification<AlertEntity> titleContains(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) return cb.conjunction();
            return cb.like(
                    cb.lower(root.get("title")),
                    "%" + title.toLowerCase() + "%"
            );
        };
    }

    // Filter by user creator
    public static Specification<AlertEntity> createdBy(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) return cb.conjunction();
            return cb.equal(root.get("user").get("id"), userId);
        };
    }

    //Filter by date of creation after
    public static Specification<AlertEntity> createdAfter(LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) return cb.conjunction();
            return cb.greaterThanOrEqualTo(root.get("createdAt"), date);
        };
    }

    //Filter by date of creation before
    public static Specification<AlertEntity> createdBefore(LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) return cb.conjunction();
            return cb.lessThanOrEqualTo(root.get("createdAt"), date);
        };
    }

    //Filter by Pet name
    public static Specification<AlertEntity> petNameContains(String petName) {
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
    public static Specification<AlertEntity> withPetId(Long petId) {
        return (root, query, cb) -> {
            if (petId == null) return cb.conjunction();
            return cb.equal(root.get("pet").get("id"), petId);
        };
    }

    //Filter by Breed
    public static Specification<AlertEntity> petBreedContains(String breed) {
        return (root, query, cb) -> {
            if (breed == null || breed.isBlank()) return cb.conjunction();
            Join<Alert, Pet> petJoin = root.join("pet", JoinType.INNER);
            return cb.like(cb.lower(petJoin.get("breed")), "%" + breed.toLowerCase() + "%");
        };
    }

    //Filter by specie
    public static Specification<AlertEntity> withPetSpecies(String species) {
        return (root, query, cb) -> {
            if (species == null || species.isBlank()) return cb.conjunction();
            Join<Alert, Pet> petJoin = root.join("pet", JoinType.INNER);
            return cb.equal(cb.lower(petJoin.get("species")), species.toLowerCase());
        };
    }

    // Filter by last update date
    public static Specification<AlertEntity> lastUpdatedAfter(LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) return cb.conjunction();

            // Subquery to get max changedAt from alert_events
            Subquery<LocalDateTime> subquery = query.subquery(LocalDateTime.class);
            Root<AlertEventEntity> eventRoot = subquery.from(AlertEventEntity.class);
            subquery.select(cb.greatest(eventRoot.<LocalDateTime>get("changedAt")));
            subquery.where(cb.equal(eventRoot.get("alert").get("id"), root.get("id")));

            return cb.greaterThanOrEqualTo(subquery, date);
        };
    }

    public static Specification<AlertEntity> lastUpdatedBefore(LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) return cb.conjunction();

            Subquery<LocalDateTime> subquery = query.subquery(LocalDateTime.class);
            Root<AlertEventEntity> eventRoot = subquery.from(AlertEventEntity.class);
            subquery.select(cb.greatest(eventRoot.<LocalDateTime>get("changedAt")));
            subquery.where(cb.equal(eventRoot.get("alert").get("id"), root.get("id")));

            return cb.lessThanOrEqualTo(subquery, date);
        };
    }

    // Filter by geographic radius using Haversine formula approximation
    public static Specification<AlertEntity> withinRadius(Double latitude, Double longitude, Double radiusKm) {
        return (root, query, cb) -> {
            if (latitude == null || longitude == null || radiusKm == null) {
                return cb.conjunction();
            }

            Subquery<UUID> latestEventSubquery = query.subquery(UUID.class);
            Root<AlertEventEntity> eventRoot = latestEventSubquery.from(AlertEventEntity.class);

            // 1 degree ~ 111km at equator
            double latDelta = radiusKm / 111.0;
            double lonDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(latitude)));


            return cb.conjunction();
        };
    }
}
