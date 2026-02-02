package ch.swiftapp.erp.notification.listener;

import ch.swiftapp.erp.auth.event.UserCreatedEvent;
import ch.swiftapp.erp.notification.model.NotificationChannel;
import ch.swiftapp.erp.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Locale;
import java.util.Map;

/**
 * Listens to auth domain events and triggers notifications.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthEventListener {

    private final NotificationService notificationService;

    /**
     * On new user account creation: send a welcome email to the new user.
     */
    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserCreated(UserCreatedEvent event) {
        log.info("[NOTIFICATION] UserCreated: username={}", event.username());

        if (event.email() == null || event.email().isBlank()) return;

        notificationService.dispatch(
                event.userId(),
                event.email(),
                "USER_ACCOUNT_CREATED",
                NotificationChannel.EMAIL,
                "Willkommen bei SwiftApp ERP – Ihr Konto wurde erstellt",
                null,
                "USER", event.userId(),
                "email/user-account-created",
                Map.of(
                        "username", event.username(),
                        "email",    event.email()
                ),
                Locale.GERMAN
        );
    }
}

