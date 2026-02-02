package ch.swiftapp.erp.notification.listener;

import ch.swiftapp.erp.auth.AuthModuleApi;
import ch.swiftapp.erp.notification.model.NotificationChannel;
import ch.swiftapp.erp.notification.service.NotificationService;
import ch.swiftapp.erp.production.event.ProductionOrderCompletedEvent;
import ch.swiftapp.erp.production.event.ProductionOrderCreatedEvent;
import ch.swiftapp.erp.production.event.ProductionOrderReleasedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Locale;
import java.util.Map;

/**
 * Listens to production domain events and triggers notifications.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductionOrderEventListener {

    private final NotificationService notificationService;
    private final AuthModuleApi authModuleApi;

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductionOrderCreated(ProductionOrderCreatedEvent event) {
        log.info("[NOTIFICATION] ProductionOrderCreated: {}", event.orderNumber());
        authModuleApi.findUserByUsername(authModuleApi.getCurrentUsername()).ifPresent(user ->
            notificationService.dispatch(
                    user.id(), null,
                    "PRODUCTION_ORDER_CREATED", NotificationChannel.IN_APP,
                    null,
                    "Produktionsauftrag " + event.orderNumber() + " wurde erstellt.",
                    "PRODUCTION_ORDER", event.orderId(),
                    null, null, Locale.GERMAN
            )
        );
    }

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductionOrderReleased(ProductionOrderReleasedEvent event) {
        log.info("[NOTIFICATION] ProductionOrderReleased: {}", event.orderNumber());
        authModuleApi.findUserByUsername(authModuleApi.getCurrentUsername()).ifPresent(user ->
            notificationService.dispatch(
                    user.id(), null,
                    "PRODUCTION_ORDER_RELEASED", NotificationChannel.IN_APP,
                    null,
                    "Produktionsauftrag " + event.orderNumber() + " wurde freigegeben.",
                    "PRODUCTION_ORDER", event.orderId(),
                    null, null, Locale.GERMAN
            )
        );
    }

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductionOrderCompleted(ProductionOrderCompletedEvent event) {
        log.info("[NOTIFICATION] ProductionOrderCompleted: {}", event.orderNumber());
        authModuleApi.findUserByUsername(authModuleApi.getCurrentUsername()).ifPresent(user ->
            notificationService.dispatch(
                    user.id(), user.email(),
                    "PRODUCTION_ORDER_COMPLETED", NotificationChannel.BOTH,
                    "Produktionsauftrag " + event.orderNumber() + " abgeschlossen",
                    "Produktionsauftrag " + event.orderNumber() + " wurde abgeschlossen. Menge: " + event.completedQuantity(),
                    "PRODUCTION_ORDER", event.orderId(),
                    "email/production-order-completed",
                    Map.of(
                            "orderNumber",       event.orderNumber(),
                            "completedQuantity", event.completedQuantity(),
                            "userName",          user.displayName() != null ? user.displayName() : user.username()
                    ),
                    Locale.GERMAN
            )
        );
    }
}

