package ch.swiftapp.erp.notification.listener;

import ch.swiftapp.erp.auth.AuthModuleApi;
import ch.swiftapp.erp.inventory.event.LowStockAlertEvent;
import ch.swiftapp.erp.notification.model.NotificationChannel;
import ch.swiftapp.erp.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Locale;

/**
 * Listens to inventory domain events and triggers notifications.
 *
 * <p>Low-stock alerts are high-priority — they are delivered via
 * {@link NotificationChannel#BOTH} (in-app + email) to ensure visibility
 * for warehouse and management users.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {

    private final NotificationService notificationService;
    private final AuthModuleApi authModuleApi;

    /**
     * On low-stock alert: urgent in-app + email to the current user.
     */
    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onLowStockAlert(LowStockAlertEvent event) {
        log.warn("[NOTIFICATION] LowStockAlert: item={}, warehouse={}, qty={}, threshold={}",
                event.itemId(), event.warehouseId(), event.currentQuantity(), event.minimumThreshold());

        authModuleApi.findUserByUsername(authModuleApi.getCurrentUsername()).ifPresent(user ->
            notificationService.dispatch(
                    user.id(), user.email(),
                    "LOW_STOCK_ALERT", NotificationChannel.BOTH,
                    "⚠ Niedriger Bestand – Artikel " + event.itemId(),
                    "Lagerbestand für Artikel " + event.itemType() + " (ID: " + event.itemId()
                            + ") ist auf " + event.currentQuantity()
                            + " gefallen (Minimum: " + event.minimumThreshold() + ").",
                    "STOCK_ITEM", event.itemId(),
                    null, null, Locale.GERMAN
            )
        );
    }
}

