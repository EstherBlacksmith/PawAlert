package itacademy.pawalert.application.alert.port.outbound;

import java.util.UUID;

public interface CurrentUserProviderPort {
    UUID getCurrentUserId();
    boolean isCurrentUserAdmin();
}
