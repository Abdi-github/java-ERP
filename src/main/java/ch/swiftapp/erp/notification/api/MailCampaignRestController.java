package ch.swiftapp.erp.notification.api;

import ch.swiftapp.erp.notification.dto.MailCampaignRequest;
import ch.swiftapp.erp.notification.dto.MailCampaignResponse;
import ch.swiftapp.erp.notification.service.MailCampaignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for mail campaign management at {@code /api/v1/mail-campaigns}.
 *
 * <p>Provides CRUD and lifecycle endpoints for mass-mail campaigns.
 * Intended for admin-level users.</p>
 */
@RestController
@RequestMapping("/api/v1/mail-campaigns")
@RequiredArgsConstructor
@Tag(name = "Mail Campaigns", description = "Mass email campaign management and sending")
@PreAuthorize("hasAuthority('NOTIFICATIONS:MANAGE')")
public class MailCampaignRestController {

    private final MailCampaignService mailCampaignService;

    @PreAuthorize("hasAuthority('NOTIFICATIONS:VIEW')")
    @GetMapping
    public Page<MailCampaignResponse> list(@PageableDefault(size = 20) Pageable pageable) {
        return mailCampaignService.findAll(pageable);
    }

    @PreAuthorize("hasAuthority('NOTIFICATIONS:VIEW')")
    @GetMapping("/{id}")
    public MailCampaignResponse getById(@PathVariable UUID id) {
        return mailCampaignService.findById(id);
    }

    @PreAuthorize("hasAuthority('NOTIFICATIONS:CREATE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MailCampaignResponse create(@Valid @RequestBody MailCampaignRequest request) {
        return mailCampaignService.create(request);
    }

    @PreAuthorize("hasAuthority('NOTIFICATIONS:CREATE')")
    @PostMapping("/{id}/queue")
    public MailCampaignResponse queue(@PathVariable UUID id) {
        return mailCampaignService.queue(id);
    }

    @PreAuthorize("hasAuthority('NOTIFICATIONS:CREATE')")
    @PostMapping("/{id}/cancel")
    public MailCampaignResponse cancel(@PathVariable UUID id) {
        return mailCampaignService.cancel(id);
    }
}

