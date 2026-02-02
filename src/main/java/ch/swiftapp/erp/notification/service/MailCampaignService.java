package ch.swiftapp.erp.notification.service;

import ch.swiftapp.erp.auth.AuthModuleApi;
import ch.swiftapp.erp.auth.dto.UserResponse;
import ch.swiftapp.erp.notification.dto.MailCampaignRequest;
import ch.swiftapp.erp.notification.dto.MailCampaignResponse;
import ch.swiftapp.erp.notification.model.MailCampaign;
import ch.swiftapp.erp.notification.model.MailCampaignStatus;
import ch.swiftapp.erp.notification.repository.MailCampaignRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing and executing {@link MailCampaign} mass-mail campaigns.
 *
 * <h2>Campaign Lifecycle</h2>
 * <pre>
 * DRAFT → (admin queues) → QUEUED → (scheduler picks up) → RUNNING → COMPLETED | FAILED
 *                                                          ↑
 *                                   (only if scheduledAt ≤ now)
 * </pre>
 *
 * <h2>Batch Processing</h2>
 * Recipients are loaded in pages of {@value #BATCH_SIZE} to avoid OOM.
 * Each batch is sent asynchronously via {@code mailBatchExecutor}.
 * A short sleep between batches naturally rate-limits outgoing SMTP traffic.
 *
 * <h2>Restartability</h2>
 * If the JVM crashes mid-campaign, the status remains {@link MailCampaignStatus#RUNNING}.
 * On restart, {@link NotificationScheduler} can detect stale RUNNING campaigns and
 * re-queue them, or an admin can manually reset them to QUEUED.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MailCampaignService {

    private static final int BATCH_SIZE = 50;
    /** Milliseconds between batches — adjust to stay within SMTP provider rate limits. */
    private static final long BATCH_DELAY_MS = 2_000L;

    private final MailCampaignRepository campaignRepository;
    private final MailService mailService;
    private final AuthModuleApi authModuleApi;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from:noreply@swiftapp.ch}")
    private String fromAddress;

    // ── Queries ──────────────────────────────────────────────

    public Page<MailCampaignResponse> findAll(Pageable pageable) {
        return campaignRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::toResponse);
    }

    public MailCampaignResponse findById(UUID id) {
        return toResponse(findEntityById(id));
    }

    // ── Commands ─────────────────────────────────────────────

    @Transactional
    public MailCampaignResponse create(MailCampaignRequest request) {
        MailCampaign campaign = MailCampaign.builder()
                .name(request.name())
                .description(request.description())
                .templateCode(request.templateCode())
                .locale(request.locale() != null ? request.locale() : "de")
                .targetSegment(request.targetSegment())
                .scheduledAt(request.scheduledAt())
                .subjectOverride(request.subjectOverride())
                .status(MailCampaignStatus.DRAFT)
                .build();
        return toResponse(campaignRepository.save(campaign));
    }

    /** Approve a DRAFT campaign — moves it to QUEUED for the scheduler. */
    @Transactional
    public MailCampaignResponse queue(UUID id) {
        MailCampaign campaign = findEntityById(id);
        if (campaign.getStatus() != MailCampaignStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT campaigns can be queued.");
        }
        campaign.setStatus(MailCampaignStatus.QUEUED);
        log.info("[CAMPAIGN] Campaign '{}' ({}) queued for dispatch", campaign.getName(), id);
        return toResponse(campaignRepository.save(campaign));
    }

    /** Cancel a DRAFT or QUEUED campaign. */
    @Transactional
    public MailCampaignResponse cancel(UUID id) {
        MailCampaign campaign = findEntityById(id);
        if (campaign.getStatus() == MailCampaignStatus.RUNNING ||
                campaign.getStatus() == MailCampaignStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a " + campaign.getStatus() + " campaign.");
        }
        campaign.setStatus(MailCampaignStatus.CANCELLED);
        return toResponse(campaignRepository.save(campaign));
    }

    // ── Batch Execution (called by scheduler) ────────────────

    /**
     * Execute all QUEUED campaigns whose scheduled time has arrived.
     * Called by {@link NotificationScheduler} every 5 minutes.
     */
    @Transactional
    public void processDueCampaigns() {
        List<MailCampaign> due = campaignRepository.findDueCampaigns(Instant.now());
        if (due.isEmpty()) return;
        log.info("[CAMPAIGN] Found {} campaign(s) ready for dispatch", due.size());
        for (MailCampaign campaign : due) {
            campaign.setStatus(MailCampaignStatus.RUNNING);
            campaign.setStartedAt(Instant.now());
            campaignRepository.save(campaign);
            sendCampaignAsync(campaign.getId());
        }
    }

    /**
     * Execute a single campaign asynchronously in the mail batch executor.
     *
     * <p>Loads users in batches of {@value #BATCH_SIZE} and sends each email
     * with a {@value #BATCH_DELAY_MS}ms delay between batches.</p>
     */
    @Async("mailBatchExecutor")
    @Transactional
    public void sendCampaignAsync(UUID campaignId) {
        MailCampaign campaign = findEntityById(campaignId);
        log.info("[CAMPAIGN] Starting dispatch for '{}' ({})", campaign.getName(), campaignId);

        try {
            List<UserResponse> recipients = resolveRecipients(campaign.getTargetSegment());
            campaign.setTotalRecipients(recipients.size());
            campaignRepository.save(campaign);

            Locale locale = Locale.forLanguageTag(campaign.getLocale());
            int sent = 0, failed = 0;

            for (int i = 0; i < recipients.size(); i += BATCH_SIZE) {
                List<UserResponse> batch = recipients.subList(i,
                        Math.min(i + BATCH_SIZE, recipients.size()));

                for (UserResponse user : batch) {
                    if (user.email() == null || user.email().isBlank()) {
                        failed++;
                        continue;
                    }
                    try {
                        String subject = buildSubject(campaign, user, locale);
                        String html = renderCampaignTemplate(campaign, user, locale);
                        mailService.sendRaw(user.email(), subject, html);
                        sent++;
                    } catch (MessagingException e) {
                        log.warn("[CAMPAIGN] Failed to send to {}: {}", user.email(), e.getMessage());
                        failed++;
                    }
                }

                // Update progress after each batch
                campaign.setSentCount(sent);
                campaign.setFailedCount(failed);
                campaignRepository.save(campaign);

                // Rate-limit pause between batches
                if (i + BATCH_SIZE < recipients.size()) {
                    try { Thread.sleep(BATCH_DELAY_MS); }
                    catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
                }
            }

            campaign.setStatus(MailCampaignStatus.COMPLETED);
            campaign.setCompletedAt(Instant.now());
            log.info("[CAMPAIGN] '{}' completed. Sent={}, Failed={}", campaign.getName(), sent, failed);

        } catch (Exception e) {
            log.error("[CAMPAIGN] '{}' failed with: {}", campaign.getName(), e.getMessage(), e);
            campaign.setStatus(MailCampaignStatus.FAILED);
        }

        campaignRepository.save(campaign);
    }

    // ── Private helpers ──────────────────────────────────────

    /**
     * Resolve the list of recipient users for a target segment.
     *
     * <p>Currently supports {@code ALL_USERS} (all enabled users in the auth module).
     * Extend with additional segments as needed (e.g. by role, by CRM customer list).</p>
     */
    private List<UserResponse> resolveRecipients(String targetSegment) {
        log.debug("[CAMPAIGN] Resolving recipients for segment '{}'", targetSegment);
        if (targetSegment == null || targetSegment.isBlank() || "ALL_USERS".equals(targetSegment)) {
            return authModuleApi.findAllEnabledUsers();
        }
        if (targetSegment.startsWith("ROLE_")) {
            String role = targetSegment.substring(5); // e.g. ROLE_SALES → SALES
            return authModuleApi.findAllByRole(role);
        }
        // Default: all enabled users
        return authModuleApi.findAllEnabledUsers();
    }

    private String buildSubject(MailCampaign campaign, UserResponse user, Locale locale) {
        if (campaign.getSubjectOverride() != null && !campaign.getSubjectOverride().isBlank()) {
            return campaign.getSubjectOverride();
        }
        return "SwiftApp ERP — " + campaign.getName();
    }

    private String renderCampaignTemplate(MailCampaign campaign, UserResponse user, Locale locale) {
        Context ctx = new Context(locale);
        ctx.setVariable("user", user);
        ctx.setVariable("campaign", campaign);
        return templateEngine.process("email/campaign", ctx);
    }

    private MailCampaign findEntityById(UUID id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MailCampaign not found: " + id));
    }

    public MailCampaignResponse toResponse(MailCampaign c) {
        return new MailCampaignResponse(
                c.getId(), c.getName(), c.getDescription(), c.getTemplateCode(), c.getLocale(),
                c.getTargetSegment(), c.getStatus(), c.getTotalRecipients(), c.getSentCount(),
                c.getFailedCount(), c.getScheduledAt(), c.getStartedAt(), c.getCompletedAt(),
                c.getSubjectOverride(), c.getCreatedBy(), c.getCreatedAt(), c.getUpdatedAt()
        );
    }
}

