package ch.swiftapp.erp.auth.repository;

import ch.swiftapp.erp.auth.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Repository for {@link Permission} entities.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    Optional<Permission> findByCode(String code);

    List<Permission> findAllByModule(String module);

    List<Permission> findAllByOrderByModuleAscCodeAsc();

    List<Permission> findAllByIdIn(Set<UUID> ids);

    boolean existsByCode(String code);
}

