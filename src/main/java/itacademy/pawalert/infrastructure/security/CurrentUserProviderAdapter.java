package itacademy.pawalert.infrastructure.security;

import itacademy.pawalert.application.alert.port.outbound.CurrentUserProviderPort;
import itacademy.pawalert.domain.user.Role;
import itacademy.pawalert.domain.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class CurrentUserProviderAdapter implements CurrentUserProviderPort {
    
    @Override
    public UUID getCurrentUserId() {
        log.debug("[CURRENT_USER] Getting current user ID");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            log.warn("[CURRENT_USER] Authentication is null");
            return null;
        }
        
        if (!authentication.isAuthenticated()) {
            log.warn("[CURRENT_USER] Authentication is not authenticated");
            return null;
        }
        
        log.debug("[CURRENT_USER] Authentication name: {}", authentication.getName());
        log.debug("[CURRENT_USER] Authentication principal type: {}", authentication.getPrincipal().getClass().getName());
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsAdapter) {
            User user = ((UserDetailsAdapter) principal).getUser();
            if (user != null) {
                log.info("[CURRENT_USER] Found user ID: {}", user.getId());
                return user.getId();
            } else {
                log.warn("[CURRENT_USER] User is null in UserDetailsAdapter");
            }
        } else {
            log.warn("[CURRENT_USER] Principal is not UserDetailsAdapter, it is: {}", principal.getClass().getName());
        }
        return null;
    }
    
    @Override
    public boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    @Override
    public Role getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsAdapter userDetailsAdapter) {
            User user = userDetailsAdapter.getUser();
            if (user != null) {
                return user.getRole();
            }
        }
        return null;
    }
}
