package ch.swiftapp.erp.notification.repository;

import ch.swiftapp.erp.notification.model.MailCampaign;
import ch.swiftapp.erp.notification.model.MailCampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link MailCampaign} entities.
 */
public interface MailCampaignRepository extends JpaRepository<MailCampaign, UUID> {

    Page<MailCampaign> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<MailCampaign> findAllByStatus(MailCampaignStatus status, Pageable pageable);

    /**
     * Find campaigns ready to be dispatched:
     * status = QUEUED and (scheduledAt is null OR scheduledAt <= now).
     */
    @Query("SELECT c FROM MailCampaign c WHERE c.status = 'QUEUED' " +
           "AND (c.scheduledAt IS NULL OR c.scheduledAt <= :now)")
    List<MailCampaign> findDueCampaigns(Instant now);
}

