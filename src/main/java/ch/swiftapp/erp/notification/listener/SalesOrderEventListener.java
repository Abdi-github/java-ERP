package ch.swiftapp.erp.notification.listener;

import ch.swiftapp.erp.auth.AuthModuleApi;
import ch.swiftapp.erp.notification.model.NotificationChannel;
import ch.swiftapp.erp.notification.service.NotificationService;
import ch.swiftapp.erp.sales.event.SalesOrderCancelledEvent;
import ch.swiftapp.erp.sales.event.SalesOrderConfirmedEvent;
import ch.swiftapp.erp.sales.event.SalesOrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Locale;
import java.util.Map;

/**
 * Listens to sales domain events and triggers appropriate notifications.
 *
 * <p>Uses {@code @TransactionalEventListener(phase = AFTER_COMMIT)} to ensure
 * notifications are only dispatched after the business transaction commits successfully —
 * preventing phantom emails on rollback.</p>
 *
 * <p>Runs {@code @Async("notificationExecutor")} to avoid blocking the
 * caller's thread and to keep notification failures from propagating back
 * to the originating service.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SalesOrderEventListener {

    private final NotificationService notificationService;
    private final AuthModuleApi authModuleApi;

    /**
     * On sales order creation: send in-app alert to the creator.
     */
    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(SalesOrderCreatedEvent event) {
        log.info("[NOTIFICATION] SalesOrderCreated event received for order {}", event.orderNumber());

        // Resolve the current user's email from the auth module
        authModuleApi.findUserByUsername(authModuleApi.getCurrentUsername()).ifPresent(user -> {
            notificationService.dispatch(
                    user.id(),
                    user.email(),
                    "SALES_ORDER_CREATED",
                    NotificationChannel.IN_APP,
                    null,
                    "Bestellung " + event.orderNumber() + " wurde erfasst.",
                    "SALES_ORDER", event.orderId(),
                    null, null, Locale.GERMAN
            );
        });
    }

    /**
     * On sales order confirmation: send confirmation email + in-app to the operator.
     */
    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderConfirmed(SalesOrderConfirmedEvent event) {
        log.info("[NOTIFICATION] SalesOrderConfirmed event received for order {}", event.orderNumber());

        authModuleApi.findUserByUsername(authModuleApi.getCurrentUsername()).ifPresent(user -> {
            notificationService.dispatch(
                    user.id(),
                    user.email(),
                    "SALES_ORDER_CONFIRMED",
                    NotificationChannel.BOTH,
                    "Bestellbestätigung " + event.orderNumber(),
                    "Ihre Bestellung " + event.orderNumber() + " wurde bestätigt.",
                    "SALES_ORDER", event.orderId(),
                    "email/sales-order-confirmed",
                    Map.of(
                            "orderNumber", event.orderNumber(),
                            "orderId",     event.orderId(),
                            "userName",    user.displayName() != null ? user.displayName() : user.username()
                    ),
                    Locale.GERMAN
            );
        });
    }

    /**
     * On sales order cancellation: send cancellation notice to the operator.
     */
    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCancelled(SalesOrderCancelledEvent event) {
        log.info("[NOTIFICATION] SalesOrderCancelled event received for order {}", event.orderNumber());

        authModuleApi.findUserByUsername(authModuleApi.getCurrentUsername()).ifPresent(user -> {
            notificationService.dispatch(
                    user.id(),
                    user.email(),
                    "SALES_ORDER_CANCELLED",
                    NotificationChannel.BOTH,
                    "Bestellung " + event.orderNumber() + " storniert",
                    "Bestellung " + event.orderNumber() + " wurde storniert.",
                    "SALES_ORDER", event.orderId(),
                    "email/sales-order-cancelled",
                    Map.of(
                            "orderNumber", event.orderNumber(),
                            "reason",      event.reason() != null ? event.reason() : "—",
                            "userName",    user.displayName() != null ? user.displayName() : user.username()
                    ),
                    Locale.GERMAN
            );
        });
    }
}

