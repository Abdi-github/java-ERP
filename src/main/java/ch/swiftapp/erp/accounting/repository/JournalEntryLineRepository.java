package ch.swiftapp.erp.accounting.repository;

import ch.swiftapp.erp.accounting.model.JournalEntryLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Repository for {@link JournalEntryLine} entities.
 */
@Repository
public interface JournalEntryLineRepository extends JpaRepository<JournalEntryLine, UUID> {

    @Query("""
            SELECT COALESCE(SUM(jel.debit), 0) - COALESCE(SUM(jel.credit), 0)
            FROM JournalEntryLine jel
            WHERE jel.account.id = :accountId
              AND jel.journalEntry.posted = true
              AND jel.journalEntry.deletedAt IS NULL
            """)
    BigDecimal calculateAccountBalance(UUID accountId);
}

