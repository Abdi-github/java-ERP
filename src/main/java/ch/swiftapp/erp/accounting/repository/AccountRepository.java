package ch.swiftapp.erp.accounting.repository;

import ch.swiftapp.erp.accounting.model.Account;
import ch.swiftapp.erp.accounting.model.AccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link Account} entities.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByAccountNumberAndDeletedAtIsNull(String accountNumber);

    Page<Account> findAllByDeletedAtIsNull(Pageable pageable);

    List<Account> findAllByDeletedAtIsNullAndActiveTrue();

    List<Account> findAllByAccountTypeAndDeletedAtIsNull(AccountType accountType);

    boolean existsByAccountNumberIgnoreCase(String accountNumber);

    @Query("""
            SELECT a FROM Account a
            WHERE a.deletedAt IS NULL
              AND (LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(a.accountNumber) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Account> searchAccounts(String search, Pageable pageable);
}

