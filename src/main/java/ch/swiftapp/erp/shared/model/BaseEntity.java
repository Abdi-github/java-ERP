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
 * Base entity providing common audit fields for all JPA entities in the system.
 * <p>
 * Every entity must extend this class to inherit:
 * <ul>
 *     <li>{@code id} — UUID primary key (auto-generated)</li>
 *     <li>{@code createdAt} / {@code updatedAt} — timestamps</li>
 *     <li>{@code createdBy} / {@code updatedBy} — audit user tracking</li>
 *     <li>{@code version} — optimistic locking</li>
 * </ul>
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

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

