package ch.swiftapp.erp.accounting.repository;

import ch.swiftapp.erp.accounting.model.JournalEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link JournalEntry} entities.
 */
@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, UUID> {

    Page<JournalEntry> findAllByDeletedAtIsNull(Pageable pageable);

    Optional<JournalEntry> findByEntryNumberAndDeletedAtIsNull(String entryNumber);

    boolean existsByEntryNumberIgnoreCase(String entryNumber);

    @Query("""
            SELECT je FROM JournalEntry je
            WHERE je.deletedAt IS NULL
              AND (LOWER(je.entryNumber) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(je.description) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<JournalEntry> searchEntries(String search, Pageable pageable);
}

