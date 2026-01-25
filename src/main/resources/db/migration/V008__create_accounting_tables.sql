-- ===========================================================
-- V008 — Accounting: accounts (chart of accounts),
--         journal_entries, journal_entry_lines
-- SwiftApp ERP — Swiss Watch Manufacturing & Retail
-- ===========================================================

-- ── Accounts (Chart of Accounts) ──────────────────────────
CREATE TABLE accounts (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  account_number  VARCHAR(20)    NOT NULL UNIQUE,
  name            VARCHAR(255)   NOT NULL,
  description     TEXT,
  account_type    VARCHAR(30)    NOT NULL,  -- ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE
  parent_id       UUID           REFERENCES accounts(id),
  active          BOOLEAN        NOT NULL DEFAULT true,
  deleted_at      TIMESTAMPTZ,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_accounts_number ON accounts(account_number);
CREATE INDEX idx_accounts_type ON accounts(account_type);
CREATE INDEX idx_accounts_parent ON accounts(parent_id);
CREATE INDEX idx_accounts_name_trgm ON accounts USING gin (name gin_trgm_ops);

-- ── Journal Entries ───────────────────────────────────────
CREATE TABLE journal_entries (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  entry_number    VARCHAR(30)    NOT NULL UNIQUE,
  entry_date      DATE           NOT NULL DEFAULT CURRENT_DATE,
  description     TEXT           NOT NULL,
  posted          BOOLEAN        NOT NULL DEFAULT false,
  reversed        BOOLEAN        NOT NULL DEFAULT false,
  reference       VARCHAR(255),
  deleted_at      TIMESTAMPTZ,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_journal_entries_number ON journal_entries(entry_number);
CREATE INDEX idx_journal_entries_date ON journal_entries(entry_date DESC);
CREATE INDEX idx_journal_entries_posted ON journal_entries(posted);

-- ── Journal Entry Lines ───────────────────────────────────
CREATE TABLE journal_entry_lines (
  id                UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  journal_entry_id  UUID           NOT NULL REFERENCES journal_entries(id) ON DELETE CASCADE,
  account_id        UUID           NOT NULL REFERENCES accounts(id),
  description       VARCHAR(500),
  debit             NUMERIC(19, 4) NOT NULL DEFAULT 0,
  credit            NUMERIC(19, 4) NOT NULL DEFAULT 0,
  position          INTEGER        NOT NULL DEFAULT 0,
  created_at        TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at        TIMESTAMPTZ,
  created_by        VARCHAR(255),
  updated_by        VARCHAR(255),
  version           BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_journal_lines_entry ON journal_entry_lines(journal_entry_id);
CREATE INDEX idx_journal_lines_account ON journal_entry_lines(account_id);

