package itacademy.pawalert.infrastructure.alert;

import itacademy.pawalert.application.alert.port.inbound.AlertSubscriptionUseCase;
import itacademy.pawalert.domain.alert.model.AlertCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertCreatedEventListener {

    private final AlertSubscriptionUseCase alertSubscriptionUseCase;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAlertCreated(AlertCreatedEvent event) {
        log.info("[AUTO-SUBSCRIBE] Auto-subscribing creator {} to alert {}",
                event.creatorId(), event.alertId());

        try {
            alertSubscriptionUseCase.subscribeToAlert(event.alertId(), event.creatorId());
            log.info("[AUTO-SUBSCRIBE] Successfully subscribed creator to alert");
        } catch (Exception e) {
            log.error("[AUTO-SUBSCRIBE] Failed to subscribe creator to alert: {}",
                    e.getMessage());
        }
    }
}
