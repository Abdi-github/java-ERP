package ch.swiftapp.erp.accounting.service;

import ch.swiftapp.erp.accounting.AccountingModuleApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Facade implementing {@link AccountingModuleApi} for cross-module access.
 */
@Service
@RequiredArgsConstructor
public class AccountingModuleApiFacade implements AccountingModuleApi {

    private final AccountService accountService;
    private final JournalEntryService journalEntryService;

    @Override
    public Optional<BigDecimal> getAccountBalance(UUID accountId) {
        return accountService.getBalance(accountId);
    }

    @Override
    public void postJournalEntry(UUID journalEntryId) {
        journalEntryService.post(journalEntryId);
    }
}

