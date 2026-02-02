package ch.swiftapp.erp.accounting;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Public API for the Accounting module.
 *
 * <p>Other Spring Modulith modules should depend on this interface only —
 * never on internal model, repository, or service classes.</p>
 */
public interface AccountingModuleApi {

    /**
     * Get the balance of an account (debits minus credits).
     *
     * @param accountId the account UUID
     * @return the balance, or empty if account not found
     */
    Optional<BigDecimal> getAccountBalance(UUID accountId);

    /**
     * Post a journal entry, making it immutable.
     *
     * @param journalEntryId the journal entry UUID
     */
    void postJournalEntry(UUID journalEntryId);
}

