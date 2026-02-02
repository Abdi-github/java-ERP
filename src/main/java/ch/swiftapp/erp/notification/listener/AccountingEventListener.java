package ch.swiftapp.erp.notification.listener;

import ch.swiftapp.erp.accounting.event.JournalEntryPostedEvent;
import ch.swiftapp.erp.accounting.event.JournalEntryReversedEvent;
import ch.swiftapp.erp.auth.AuthModuleApi;
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
 * Listens to accounting domain events and triggers notifications.
 *
 * <ul>
 *   <li>{@link JournalEntryPostedEvent} → in-app confirmation to the poster</li>
 *   <li>{@link JournalEntryReversedEvent} → in-app + email alert to the poster</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AccountingEventListener {

    private final NotificationService notificationService;
    private final AuthModuleApi authModuleApi;

    /**
     * On journal entry posted: in-app confirmation to the poster.
     */
    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onJournalEntryPosted(JournalEntryPostedEvent event) {
        log.info("[NOTIFICATION] JournalEntryPosted: {}", event.entryNumber());

        authModuleApi.findUserByUsername(authModuleApi.getCurrentUsername()).ifPresent(user ->
            notificationService.dispatch(
                    user.id(), null,
                    "JOURNAL_ENTRY_POSTED", NotificationChannel.IN_APP,
                    null,
                    "Buchungssatz " + event.entryNumber() + " wurde gebucht.",
                    "JOURNAL_ENTRY", event.journalEntryId(),
                    null, null, Locale.GERMAN
            )
        );
    }

    /**
     * On journal entry reversed: in-app + email alert.
     *
     * <p>Reversals are critical accounting events — delivered via both channels.</p>
     */
    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onJournalEntryReversed(JournalEntryReversedEvent event) {
        log.warn("[NOTIFICATION] JournalEntryReversed: original={}", event.originalNumber());

        authModuleApi.findUserByUsername(authModuleApi.getCurrentUsername()).ifPresent(user ->
            notificationService.dispatch(
                    user.id(), user.email(),
                    "JOURNAL_ENTRY_REVERSED", NotificationChannel.BOTH,
                    "Stornierung – Buchungssatz " + event.originalNumber(),
                    "Buchungssatz " + event.originalNumber() + " wurde storniert. "
                            + "Stornobuchung: " + event.reversalEntryId(),
                    "JOURNAL_ENTRY", event.originalEntryId(),
                    null, null, Locale.GERMAN
            )
        );
    }
}

