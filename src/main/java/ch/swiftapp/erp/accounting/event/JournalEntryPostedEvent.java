package ch.swiftapp.erp.accounting.event;

import java.util.UUID;

/**
 * Domain event published when a journal entry is posted.
 *
 * @param journalEntryId the ID of the posted journal entry
 * @param entryNumber    the entry number
 */
public record JournalEntryPostedEvent(UUID journalEntryId, String entryNumber) {}

