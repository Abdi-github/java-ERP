package ch.swiftapp.erp.notification.listener;

import ch.swiftapp.erp.auth.AuthModuleApi;
import ch.swiftapp.erp.notification.model.NotificationChannel;
import ch.swiftapp.erp.notification.service.NotificationService;
import ch.swiftapp.erp.qualitycontrol.event.NonConformanceReportCreatedEvent;
import ch.swiftapp.erp.qualitycontrol.event.QualityCheckFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Locale;
import java.util.Map;

/**
 * Listens to quality-control domain events and triggers notifications.
 *
 * <p>Quality failures and NCRs are high-priority — they are delivered
 * via {@link NotificationChannel#BOTH} (in-app + email) to ensure visibility.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class QualityControlEventListener {

    private final NotificationService notificationService;
    private final AuthModuleApi authModuleApi;

    /**
     * On quality check failure: urgent in-app + email to the QC operator.
     */
    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onQualityCheckFailed(QualityCheckFailedEvent event) {
        log.warn("[NOTIFICATION] QualityCheckFailed: check={}, productionOrder={}",
                event.checkNumber(), event.productionOrderId());

        authModuleApi.findUserByUsername(authModuleApi.getCurrentUsername()).ifPresent(user ->
            notificationService.dispatch(
                    user.id(), user.email(),
                    "QUALITY_CHECK_FAILED", NotificationChannel.BOTH,
                    "⚠ Qualitätsprüfung fehlgeschlagen – " + event.checkNumber(),
                    "Qualitätsprüfung " + event.checkNumber() + " ist fehlgeschlagen.",
                    "QUALITY_CHECK", event.qualityCheckId(),
                    "email/quality-check-failed",
                    Map.of(
                            "checkNumber",       event.checkNumber(),
                            "productionOrderId", event.productionOrderId(),
                            "userName",          user.displayName() != null ? user.displayName() : user.username()
                    ),
                    Locale.GERMAN
            )
        );
    }

    /**
     * On non-conformance report creation: in-app + email alert.
     */
    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNcrCreated(NonConformanceReportCreatedEvent event) {
        log.warn("[NOTIFICATION] NCR created: {}", event.ncrNumber());

        authModuleApi.findUserByUsername(authModuleApi.getCurrentUsername()).ifPresent(user ->
            notificationService.dispatch(
                    user.id(), user.email(),
                    "NCR_CREATED", NotificationChannel.BOTH,
                    "Neuer NCR – " + event.ncrNumber(),
                    "Nichtkonformitätsbericht " + event.ncrNumber() + " wurde erstellt.",
                    "NCR", event.ncrId(),
                    "email/ncr-created",
                    Map.of(
                            "ncrNumber",  event.ncrNumber(),
                            "ncrId",      event.ncrId(),
                            "userName",   user.displayName() != null ? user.displayName() : user.username()
                    ),
                    Locale.GERMAN
            )
        );
    }
}

