package ch.swiftapp.erp.notification.listener;

import ch.swiftapp.erp.auth.AuthModuleApi;
import ch.swiftapp.erp.hr.event.EmployeeCreatedEvent;
import ch.swiftapp.erp.hr.event.EmployeeTerminatedEvent;
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
 * Listens to HR domain events and triggers notifications.
 *
 * <ul>
 *   <li>{@link EmployeeCreatedEvent} → welcome in-app notification to the HR operator</li>
 *   <li>{@link EmployeeTerminatedEvent} → alert in-app to ADMIN/HR users</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HrEventListener {

    private final NotificationService notificationService;
    private final AuthModuleApi authModuleApi;

    /**
     * On new employee creation: in-app confirmation to the HR operator.
     */
    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("[NOTIFICATION] EmployeeCreated: {}", event.fullName());

        authModuleApi.findUserByUsername(authModuleApi.getCurrentUsername()).ifPresent(user -> {
            notificationService.dispatch(
                    user.id(),
                    user.email(),
                    "EMPLOYEE_WELCOME",
                    NotificationChannel.BOTH,
                    "Mitarbeiterprofil erstellt – " + event.fullName(),
                    "Mitarbeiterprofil für " + event.fullName() + " (" + event.employeeNumber() + ") wurde erstellt.",
                    "EMPLOYEE", event.employeeId(),
                    "email/employee-welcome",
                    Map.of(
                            "fullName",        event.fullName(),
                            "employeeNumber",  event.employeeNumber(),
                            "recipientName",   user.displayName() != null ? user.displayName() : user.username()
                    ),
                    Locale.GERMAN
            );
        });
    }

    /**
     * On employee termination: in-app alert to the HR operator.
     */
    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEmployeeTerminated(EmployeeTerminatedEvent event) {
        log.info("[NOTIFICATION] EmployeeTerminated: {}", event.employeeNumber());

        authModuleApi.findUserByUsername(authModuleApi.getCurrentUsername()).ifPresent(user -> {
            notificationService.dispatch(
                    user.id(),
                    null,
                    "EMPLOYEE_TERMINATED",
                    NotificationChannel.IN_APP,
                    null,
                    "Mitarbeiter " + event.employeeNumber() + " wurde ausgetreten.",
                    "EMPLOYEE", event.employeeId(),
                    null, null, Locale.GERMAN
            );
        });
    }
}

