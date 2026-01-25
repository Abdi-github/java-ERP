-- ==========================================================
-- V016 — Additional notification templates for inventory &
--         accounting event listeners
-- SwiftApp ERP | Swiss Watch Manufacturing & Retail
-- ==========================================================

-- Low Stock Alert — German
INSERT INTO notification_templates (code, channel, locale, subject, body_template, active) VALUES
('LOW_STOCK_ALERT', 'EMAIL', 'de',
 '⚠ Niedriger Bestand – Artikel #{itemId}',
 'Lagerbestand für Artikel #{itemId} ist unter den Mindestbestand gefallen.', TRUE),
('LOW_STOCK_ALERT', 'IN_APP', 'de',
 NULL,
 '⚠ Lagerbestand für Artikel #{itemId} ist niedrig!', TRUE),

-- Low Stock Alert — English
('LOW_STOCK_ALERT', 'EMAIL', 'en',
 '⚠ Low Stock Alert – Item #{itemId}',
 'Stock level for item #{itemId} has fallen below the minimum threshold.', TRUE),

-- Journal Entry Posted — German
('JOURNAL_ENTRY_POSTED', 'IN_APP', 'de',
 NULL,
 'Buchungssatz #{entryNumber} wurde gebucht.', TRUE),

-- Journal Entry Reversed — German
('JOURNAL_ENTRY_REVERSED', 'EMAIL', 'de',
 'Stornierung – Buchungssatz #{entryNumber}',
 'Buchungssatz #{entryNumber} wurde storniert.', TRUE),
('JOURNAL_ENTRY_REVERSED', 'IN_APP', 'de',
 NULL,
 'Buchungssatz #{entryNumber} wurde storniert!', TRUE),

-- Weekly Summary
('WEEKLY_SUMMARY', 'EMAIL', 'de',
 'SwiftApp Wochenübersicht – #{date}',
 'email/daily-digest', TRUE);

