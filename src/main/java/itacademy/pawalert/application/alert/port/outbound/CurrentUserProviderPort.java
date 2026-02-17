package itacademy.pawalert.application.alert.port.outbound;

import itacademy.pawalert.domain.user.Role;

import java.util.UUID;

public interface CurrentUserProviderPort {
    UUID getCurrentUserId();
    boolean isCurrentUserAdmin();
    Role getCurrentUserRole();
}
