package ch.swiftapp.erp.notification.listener;

import ch.swiftapp.erp.auth.AuthModuleApi;
import ch.swiftapp.erp.notification.model.NotificationChannel;
import ch.swiftapp.erp.notification.service.NotificationService;
import ch.swiftapp.erp.purchasing.event.PurchaseOrderCancelledEvent;
import ch.swiftapp.erp.purchasing.event.PurchaseOrderConfirmedEvent;
import ch.swiftapp.erp.purchasing.event.PurchaseOrderCreatedEvent;
import ch.swiftapp.erp.purchasing.event.PurchaseOrderReceivedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Locale;
import java.util.Map;

/**
 * Listens to purchasing domain events and triggers notifications.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderEventListener {

    private final NotificationService notificationService;
    private final AuthModuleApi authModuleApi;

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPurchaseOrderCreated(PurchaseOrderCreatedEvent event) {
        log.info("[NOTIFICATION] PurchaseOrderCreated: {}", event.orderNumber());
        authModuleApi.findUserByUsername(authModuleApi.getCurrentUsername()).ifPresent(user ->
            notificationService.dispatch(
                    user.id(), null,
                    "PURCHASE_ORDER_CREATED", NotificationChannel.IN_APP,
                    null,
                    "Lieferantenbestellung " + event.orderNumber() + " erfasst.",
                    "PURCHASE_ORDER", event.orderId(),
                    null, null, Locale.GERMAN
            )
        );
    }

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPurchaseOrderConfirmed(PurchaseOrderConfirmedEvent event) {
        log.info("[NOTIFICATION] PurchaseOrderConfirmed: {}", event.orderNumber());
        authModuleApi.findUserByUsername(authModuleApi.getCurrentUsername()).ifPresent(user ->
            notificationService.dispatch(
                    user.id(), user.email(),
                    "PURCHASE_ORDER_CONFIRMED", NotificationChannel.BOTH,
                    "Lieferantenbestellung " + event.orderNumber() + " bestätigt",
                    "Lieferantenbestellung " + event.orderNumber() + " wurde bestätigt. Betrag: CHF " + event.totalAmount(),
                    "PURCHASE_ORDER", event.orderId(),
                    "email/purchase-order-confirmed",
                    Map.of(
                            "orderNumber",  event.orderNumber(),
                            "totalAmount",  event.totalAmount(),
                            "userName",     user.displayName() != null ? user.displayName() : user.username()
                    ),
                    Locale.GERMAN
            )
        );
    }

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPurchaseOrderReceived(PurchaseOrderReceivedEvent event) {
        log.info("[NOTIFICATION] PurchaseOrderReceived: {}", event.orderNumber());
        authModuleApi.findUserByUsername(authModuleApi.getCurrentUsername()).ifPresent(user ->
            notificationService.dispatch(
                    user.id(), null,
                    "PURCHASE_ORDER_RECEIVED", NotificationChannel.IN_APP,
                    null,
                    "Wareneingang für Bestellung " + event.orderNumber() + " verbucht.",
                    "PURCHASE_ORDER", event.orderId(),
                    null, null, Locale.GERMAN
            )
        );
    }

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPurchaseOrderCancelled(PurchaseOrderCancelledEvent event) {
        log.info("[NOTIFICATION] PurchaseOrderCancelled: {}", event.orderNumber());
        authModuleApi.findUserByUsername(authModuleApi.getCurrentUsername()).ifPresent(user ->
            notificationService.dispatch(
                    user.id(), null,
                    "PURCHASE_ORDER_CANCELLED", NotificationChannel.IN_APP,
                    null,
                    "Lieferantenbestellung " + event.orderNumber() + " wurde storniert."
                    + (event.reason() != null ? " Grund: " + event.reason() : ""),
                    "PURCHASE_ORDER", event.orderId(),
                    null, null, Locale.GERMAN
            )
        );
    }
}


