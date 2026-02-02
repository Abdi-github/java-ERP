package ch.swiftapp.erp.notification.repository;

import ch.swiftapp.erp.notification.model.Notification;
import ch.swiftapp.erp.notification.model.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link Notification} entities.
 */
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    /** All notifications for a user, newest first. */
    Page<Notification> findAllByRecipientUserIdOrderByCreatedAtDesc(UUID recipientUserId, Pageable pageable);

    /** Unread in-app notifications for a user. */
    List<Notification> findAllByRecipientUserIdAndStatusIn(UUID recipientUserId, List<NotificationStatus> statuses);

    /** Count unread in-app notifications. */
    long countByRecipientUserIdAndStatusIn(UUID recipientUserId, List<NotificationStatus> statuses);

    /** Failed notifications eligible for retry (retry_count < maxRetries). */
    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' AND n.retryCount < :maxRetries")
    List<Notification> findRetryable(@Param("maxRetries") int maxRetries);

    /** Mark all PENDING/SENT notifications as READ for a user. */
    @Modifying
    @Query("UPDATE Notification n SET n.status = 'READ', n.readAt = CURRENT_TIMESTAMP " +
           "WHERE n.recipientUserId = :userId AND n.status IN ('PENDING', 'SENT')")
    int markAllReadByUserId(@Param("userId") UUID userId);
}

