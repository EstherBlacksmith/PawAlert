package itacademy.pawalert.domain.alert.specification;

import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;
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

    //Filter by date of creation
    public static Specification<Alert> createdAfter(LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) return cb.conjunction();
            return cb.greaterThanOrEqualTo(root.get("createdAt"), date);
        };
    }
}
