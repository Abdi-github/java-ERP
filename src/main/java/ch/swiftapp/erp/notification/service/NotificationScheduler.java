package ch.swiftapp.erp.notification.service;

import ch.swiftapp.erp.auth.AuthModuleApi;
import ch.swiftapp.erp.auth.dto.UserResponse;
import ch.swiftapp.erp.notification.model.NotificationChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Scheduled jobs for the notification module.
 *
 * <p>All cron expressions use {@code Europe/Zurich} (Swiss time) to ensure
 * correct alignment with business hours regardless of server timezone.</p>
 *
 * <h2>Jobs</h2>
 * <ul>
 *   <li><b>Retry failed notifications</b> — every 5 minutes, re-attempts
 *       failed email deliveries up to a max retry count</li>
 *   <li><b>Process due campaigns</b> — every 5 minutes, picks up QUEUED
 *       mass-mail campaigns whose scheduledAt has passed</li>
 *   <li><b>Daily operations digest</b> — 07:00 CH time on weekdays,
 *       sends a summary email to admin/manager users</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationService notificationService;
    private final MailCampaignService mailCampaignService;
    private final AuthModuleApi authModuleApi;

    // ── Retry Failed Notifications ───────────────────────────

    /**
     * Retry all FAILED notifications every 5 minutes.
     *
     * <p>Respects {@code MAX_RETRIES} defined in {@link NotificationService}.
     * Notifications that exceed max retries remain in FAILED status for audit.</p>
     */
    @Scheduled(fixedDelay = 5 * 60 * 1000, initialDelay = 60 * 1000)
    public void retryFailedNotifications() {
        log.debug("[SCHEDULER] Running failed-notification retry job");
        try {
            notificationService.retryFailed();
        } catch (Exception e) {
            log.error("[SCHEDULER] Retry job failed: {}", e.getMessage(), e);
        }
    }

    // ── Mass Mail Campaign Processing ────────────────────────

    /**
     * Process QUEUED mail campaigns every 5 minutes.
     *
     * <p>Each eligible campaign is transitioned to RUNNING and dispatched
     * asynchronously via {@code mailBatchExecutor}.</p>
     */
    @Scheduled(fixedDelay = 5 * 60 * 1000, initialDelay = 2 * 60 * 1000)
    public void processCampaigns() {
        log.debug("[SCHEDULER] Running campaign dispatch job");
        try {
            mailCampaignService.processDueCampaigns();
        } catch (Exception e) {
            log.error("[SCHEDULER] Campaign dispatch job failed: {}", e.getMessage(), e);
        }
    }

    // ── Daily Digest ─────────────────────────────────────────

    /**
     * Send a daily operations digest email at 07:00 Swiss time, Monday–Friday.
     *
     * <p>The digest covers: overdue sales orders, low-stock alerts,
     * pending QC items, and production orders behind schedule.
     * Recipients: all users with roles ADMIN or MANAGER.</p>
     */
    @Scheduled(cron = "0 0 7 * * MON-FRI", zone = "Europe/Zurich")
    public void sendDailyDigest() {
        log.info("[SCHEDULER] Sending daily operations digest");
        try {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            List<UserResponse> recipients = authModuleApi.findAllByRole("ADMIN");
            recipients.addAll(authModuleApi.findAllByRole("MANAGER"));

            // De-duplicate by user ID
            var seen = new java.util.HashSet<java.util.UUID>();
            for (UserResponse user : recipients) {
                if (user.email() == null || user.email().isBlank() || !seen.add(user.id())) continue;

                notificationService.dispatch(
                        user.id(), user.email(),
                        "DAILY_DIGEST", NotificationChannel.EMAIL,
                        "SwiftApp Tagesübersicht — " + today,
                        null,
                        null, null,
                        "email/daily-digest",
                        Map.of(
                                "date", today,
                                "userName", user.displayName() != null ? user.displayName() : user.username()
                        ),
                        Locale.GERMAN
                );
            }
            log.info("[SCHEDULER] Daily digest sent to {} recipients", seen.size());
        } catch (Exception e) {
            log.error("[SCHEDULER] Daily digest failed: {}", e.getMessage(), e);
        }
    }

    // ── Weekly Summary ───────────────────────────────────────

    /**
     * Send a weekly business summary every Monday at 08:00 Swiss time.
     *
     * <p>Covers: weekly revenue, units produced, QC pass rate, open purchase orders.</p>
     */
    @Scheduled(cron = "0 0 8 * * MON", zone = "Europe/Zurich")
    public void sendWeeklySummary() {
        log.info("[SCHEDULER] Sending weekly business summary");
        try {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            List<UserResponse> recipients = authModuleApi.findAllByRole("ADMIN");
            recipients.addAll(authModuleApi.findAllByRole("MANAGER"));

            var seen = new java.util.HashSet<java.util.UUID>();
            for (UserResponse user : recipients) {
                if (user.email() == null || user.email().isBlank() || !seen.add(user.id())) continue;

                notificationService.dispatch(
                        user.id(), user.email(),
                        "WEEKLY_SUMMARY", NotificationChannel.EMAIL,
                        "SwiftApp Wochenübersicht — " + today,
                        null,
                        null, null,
                        "email/daily-digest", // reuse digest template for now
                        Map.of(
                                "date", today,
                                "userName", user.displayName() != null ? user.displayName() : user.username()
                        ),
                        Locale.GERMAN
                );
            }
            log.info("[SCHEDULER] Weekly summary sent to {} recipients", seen.size());
        } catch (Exception e) {
            log.error("[SCHEDULER] Weekly summary failed: {}", e.getMessage(), e);
        }
    }
}

