package ch.swiftapp.erp.shared.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * Base superclass for all companion translation entities in SwiftApp ERP.
 *
 * <p>Each translatable entity (e.g. {@code Product}, {@code Category}) has a
 * corresponding companion table (e.g. {@code product_translations}) whose rows
 * extend this class.  One row is stored per supported locale ({@code de},
 * {@code fr}, {@code it}, {@code en}), containing the locale-specific values
 * of human-readable fields such as {@code name} and {@code description}.</p>
 *
 * <p>Resolution order applied by {@link ch.swiftapp.erp.shared.service.TranslationResolver}:</p>
 * <ol>
 *   <li>Current request locale (e.g. {@code fr})</li>
 *   <li>Fallback to {@code de} (company primary language)</li>
 *   <li>Any available translation</li>
 *   <li>Original field value on the parent entity</li>
 * </ol>
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * BCP-47 language tag (language subtag only): {@code de}, {@code fr}, {@code it}, {@code en}.
     */
    @Column(name = "locale", nullable = false, length = 10)
    private String locale;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    @Version
    @Column(name = "version")
    private Long version;
}

