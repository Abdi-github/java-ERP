package ch.swiftapp.erp.auth.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * An authorization role (e.g., ADMIN, SALES, WAREHOUSE).
 *
 * <p>Roles are seeded via Flyway migration V007 and assigned to users
 * through the {@code user_roles} join table. Each role holds a set of
 * {@link Permission}s linked via the {@code role_permissions} join table.</p>
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "permissions")
@ToString(exclude = "permissions")
@Table(name = "roles")
public class Role extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();
}

