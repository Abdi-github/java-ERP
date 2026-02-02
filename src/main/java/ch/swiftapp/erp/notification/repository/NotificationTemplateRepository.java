package ch.swiftapp.erp.notification.repository;

import ch.swiftapp.erp.notification.model.NotificationChannel;
import ch.swiftapp.erp.notification.model.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link NotificationTemplate} entities.
 */
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, UUID> {

    /** Find a template by code + channel + locale (exact match). */
    Optional<NotificationTemplate> findByCodeAndChannelAndLocaleAndActiveTrue(
            String code, NotificationChannel channel, String locale);

    /** Fallback: find any active template for code + channel, regardless of locale. */
    Optional<NotificationTemplate> findFirstByCodeAndChannelAndActiveTrue(
            String code, NotificationChannel channel);
}

