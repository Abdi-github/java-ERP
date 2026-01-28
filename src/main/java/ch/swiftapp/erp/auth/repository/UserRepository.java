package ch.swiftapp.erp.auth.repository;

import ch.swiftapp.erp.auth.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link User} entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsernameIgnoreCaseAndDeletedAtIsNull(String username);

    Optional<User> findByEmailIgnoreCaseAndDeletedAtIsNull(String email);

    Page<User> findAllByDeletedAtIsNull(Pageable pageable);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    @Query("""
            SELECT u FROM User u
            WHERE u.deletedAt IS NULL
              AND (LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<User> searchUsers(String search, Pageable pageable);

    /** Find all enabled, non-deleted, non-locked users. */
    List<User> findAllByEnabledTrueAndLockedFalseAndDeletedAtIsNull();

    /** Find all enabled, non-deleted, non-locked users that have a specific role. */
    @Query("""
            SELECT u FROM User u JOIN u.roles r
            WHERE u.deletedAt IS NULL
              AND u.enabled = true
              AND u.locked = false
              AND UPPER(r.name) = UPPER(:roleName)
            """)
    List<User> findAllByRoleName(@Param("roleName") String roleName);
}

