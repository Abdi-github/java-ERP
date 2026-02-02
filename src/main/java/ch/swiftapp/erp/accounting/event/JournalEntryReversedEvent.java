package ch.swiftapp.erp.accounting.event;

import java.util.UUID;

/**
 * Domain event published when a journal entry is reversed.
 *
 * @param originalEntryId  the ID of the original (reversed) entry
 * @param originalNumber   the original entry number
 * @param reversalEntryId  the ID of the newly created reversal entry
 */
public record JournalEntryReversedEvent(UUID originalEntryId, String originalNumber, UUID reversalEntryId) {}

