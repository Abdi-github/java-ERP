package ch.swiftapp.erp.auth.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * A granular permission that can be assigned to {@link Role}s.
 *
 * <p>Permissions follow a {@code MODULE:ACTION} naming convention
 * (e.g., {@code SALES:VIEW}, {@code INVENTORY:CREATE}).
 * They are stored in the {@code permissions} table and linked to roles
 * via the {@code role_permissions} join table.</p>
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "permissions")
public class Permission extends BaseEntity {

    /** Unique permission code, e.g. {@code SALES:VIEW}, {@code ADMIN:ROLES_MANAGE}. */
    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    /** Human-readable description of what this permission grants. */
    @Column(name = "description")
    private String description;

    /** The ERP module this permission belongs to (e.g. SALES, INVENTORY, ADMIN). */
    @Column(name = "module", nullable = false, length = 50)
    private String module;
}

