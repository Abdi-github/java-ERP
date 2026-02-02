package ch.swiftapp.erp.accounting.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A journal entry in the general ledger (Buchungssatz).
 *
 * <p>Each entry must balance: total debits == total credits.
 * Once posted, a journal entry becomes immutable and can only be reversed.</p>
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "lines")
@ToString(exclude = "lines")
@Table(name = "journal_entries")
public class JournalEntry extends BaseEntity {

    @Column(name = "entry_number", nullable = false, unique = true, length = 30)
    private String entryNumber;

    @Builder.Default
    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate = LocalDate.now();

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(name = "posted", nullable = false)
    private Boolean posted = false;

    @Builder.Default
    @Column(name = "reversed", nullable = false)
    private Boolean reversed = false;

    @Column(name = "reference")
    private String reference;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Builder.Default
    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<JournalEntryLine> lines = new ArrayList<>();

    /**
     * Add a line to this journal entry.
     */
    public void addLine(JournalEntryLine line) {
        lines.add(line);
        line.setJournalEntry(this);
    }
}

