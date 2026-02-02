package ch.swiftapp.erp.notification.service;

import ch.swiftapp.erp.notification.NotificationModuleApi;
import ch.swiftapp.erp.notification.dto.NotificationResponse;
import ch.swiftapp.erp.notification.model.Notification;
import ch.swiftapp.erp.notification.model.NotificationChannel;
import ch.swiftapp.erp.notification.model.NotificationStatus;
import ch.swiftapp.erp.notification.repository.NotificationRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Core notification orchestration service.
 *
 * <p>Handles the full lifecycle of a {@link Notification}:</p>
 * <ol>
 *   <li>Persist a PENDING record (audit trail)</li>
 *   <li>Attempt delivery (in-app store + email dispatch)</li>
 *   <li>Update status to SENT or FAILED</li>
 * </ol>
 *
 * <p>Also implements {@link NotificationModuleApi} to expose unread counts
 * and mark-as-read functionality to other modules.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationService implements NotificationModuleApi {

    private static final int MAX_RETRIES = 3;
    private static final List<NotificationStatus> UNREAD_STATUSES =
            List.of(NotificationStatus.PENDING, NotificationStatus.SENT);

    private final NotificationRepository notificationRepository;
    private final MailService mailService;

    // ── NotificationModuleApi ───────────────────────────────

    @Override
    public long countUnread(UUID userId) {
        return notificationRepository.countByRecipientUserIdAndStatusIn(userId, UNREAD_STATUSES);
    }

    @Override
    @Transactional
    public void markAllRead(UUID userId) {
        
        int count = notificationRepository.markAllReadByUserId(userId);
        
        log.debug("[NOTIFICATION] Marked {} notifications as READ for user {}", count, userId);
    }

    @Override
    @Transactional
    public void sendAdHoc(UUID recipientUserId, String recipientEmail,
                          String templateCode, String subject, String body,
                          String referenceType, UUID referenceId) {
        dispatch(recipientUserId, recipientEmail, templateCode,
                NotificationChannel.BOTH, subject, body, referenceType, referenceId,
                null, null, Locale.GERMAN);
    }

    // ── Queries ─────────────────────────────────────────────

    /** Paginated notification list for a user, newest first. */
    public Page<NotificationResponse> findForUser(UUID userId, Pageable pageable) {
        return notificationRepository
                .findAllByRecipientUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toResponse);
    }

    /** Find a single notification by ID. */
    public NotificationResponse findById(UUID id) {
        return toResponse(notificationRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Notification not found: " + id)));
    }

    /** Mark a single notification as READ. */
    @Transactional
    public void markRead(UUID notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Notification not found: " + notificationId));
        
        if (n.getStatus() == NotificationStatus.PENDING || n.getStatus() == NotificationStatus.SENT) {
            
            n.setStatus(NotificationStatus.READ);
            n.setReadAt(Instant.now());
            
            notificationRepository.save(n);
        }
    }

    /** Dismiss a single notification. */
    @Transactional
    public void dismiss(UUID notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Notification not found: " + notificationId));
        n.setStatus(NotificationStatus.DISMISSED);
        notificationRepository.save(n);
    }

    // ── Dispatch ────────────────────────────────────────────

    /**
     * Create, persist, and dispatch a notification.
     *
     * <p>This is the main entry point called by event listeners.
     * The notification is saved as PENDING first, then delivery is attempted.
     * Status is updated to SENT or FAILED in the same transaction.</p>
     *
     * @param recipientUserId  target user UUID (cross-module ref — not a FK)
     * @param recipientEmail   target email address (may be null for IN_APP-only)
     * @param templateCode     template identifier for audit
     * @param channel          delivery channel
     * @param subject          email subject (ignored for IN_APP)
     * @param body             rendered body text (HTML for email, plain for in-app)
     * @param referenceType    business entity type (e.g. {@code "SALES_ORDER"})
     * @param referenceId      business entity UUID for deep-linking
     * @param emailTemplate    Thymeleaf template name (null → use {@code body} as raw HTML)
     * @param templateVars     Thymeleaf variables (used only when {@code emailTemplate != null})
     * @param locale           rendering locale
     */
    @Transactional
    public void dispatch(UUID recipientUserId, String recipientEmail,
                         String templateCode, NotificationChannel channel,
                         String subject, String body,
                         String referenceType, UUID referenceId,
                         String emailTemplate, Map<String, Object> templateVars,
                         Locale locale) {

        Notification notification = Notification.builder()
                .recipientUserId(recipientUserId)
                .recipientEmail(recipientEmail)
                .templateCode(templateCode)
                .channel(channel)
                .status(NotificationStatus.PENDING)
                .subject(subject)
                .body(body)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .build();

        notificationRepository.save(notification);
        log.debug("[NOTIFICATION] Created {} notification '{}' for user {}",
                channel, templateCode, recipientUserId);

        // ── In-app: status = SENT immediately (it's already persisted) ──
        boolean inApp = channel == NotificationChannel.IN_APP || channel == NotificationChannel.BOTH;
        boolean email = channel == NotificationChannel.EMAIL || channel == NotificationChannel.BOTH;

        if (inApp) {
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(Instant.now());
        }

        // ── Email dispatch ───────────────────────────────────
        if (email && recipientEmail != null && !recipientEmail.isBlank()) {
            try {
                if (emailTemplate != null && templateVars != null) {
                    mailService.sendHtml(recipientEmail, subject, emailTemplate, templateVars, locale);
                } else if (body != null) {
                    mailService.sendRaw(recipientEmail, subject, body);
                }
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(Instant.now());
            } catch (MessagingException e) {
                log.error("[NOTIFICATION] Email delivery failed for '{}' to '{}': {}",
                        templateCode, recipientEmail, e.getMessage());
                notification.setStatus(NotificationStatus.FAILED);
                notification.setErrorMessage(e.getMessage());
            }
        }

        notificationRepository.save(notification);
    }

    // ── Retry (called by scheduler) ─────────────────────────

    /** Retry all FAILED notifications that haven't exceeded max retries. */
    @Transactional
    public void retryFailed() {
        List<Notification> retryable = notificationRepository.findRetryable(MAX_RETRIES);
        if (retryable.isEmpty()) return;

        log.info("[NOTIFICATION] Retrying {} failed notifications", retryable.size());
        for (Notification n : retryable) {
            n.setRetryCount(n.getRetryCount() + 1);
            boolean email = n.getChannel() == NotificationChannel.EMAIL
                    || n.getChannel() == NotificationChannel.BOTH;
            if (email && n.getRecipientEmail() != null) {
                try {
                    mailService.sendRaw(n.getRecipientEmail(), n.getSubject(), n.getBody());
                    n.setStatus(NotificationStatus.SENT);
                    n.setSentAt(Instant.now());
                    n.setErrorMessage(null);
                } catch (MessagingException e) {
                    log.warn("[NOTIFICATION] Retry {}/{} failed for notification {}: {}",
                            n.getRetryCount(), MAX_RETRIES, n.getId(), e.getMessage());
                    n.setErrorMessage(e.getMessage());
                }
            } else {
                // IN_APP-only that failed: just re-mark as SENT
                n.setStatus(NotificationStatus.SENT);
                n.setSentAt(Instant.now());
            }
        }
        notificationRepository.saveAll(retryable);
    }

    // ── Mapping ─────────────────────────────────────────────

    public NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(), n.getRecipientUserId(), n.getRecipientEmail(),
                n.getTemplateCode(), n.getChannel(), n.getStatus(),
                n.getSubject(), n.getBody(), n.getReferenceType(), n.getReferenceId(),
                n.getRetryCount(), n.getSentAt(), n.getReadAt(), n.getCreatedAt()
        );
    }
}

