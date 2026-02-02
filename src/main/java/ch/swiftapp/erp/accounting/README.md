# 📊 Accounting Module

> Chart of accounts, journal entries, and double-entry bookkeeping for Swiss watch ERP.

---

## Module Overview

| Property | Value |
|----------|-------|
| **Package** | `ch.swiftapp.erp.accounting` |
| **Public API** | `AccountingModuleApi` |
| **Entities** | Account, JournalEntry, JournalEntryLine |
| **Enums** | `AccountType` (ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE) |
| **Permissions** | `ACCOUNTING:VIEW`, `ACCOUNTING:CREATE`, `ACCOUNTING:EDIT`, `ACCOUNTING:DELETE` |
| **Web Routes** | `/app/accounting/accounts`, `/app/accounting/journal-entries` |
| **API Routes** | `/api/v1/accounts`, `/api/v1/journal-entries` |

---

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                    ACCOUNTING MODULE                              │
│                                                                  │
│  ┌──────────────────┐          ┌──────────────────┐             │
│  │ Account          │          │ JournalEntry     │             │
│  │ ViewController   │          │ ViewController   │             │
│  │ /app/accounting/ │          │ /app/accounting/ │             │
│  │ accounts         │          │ journal-entries  │             │
│  └───────┬──────────┘          └───────┬──────────┘             │
│          │                             │                         │
│  ┌───────┴──────────┐          ┌───────┴──────────┐             │
│  │ Account          │          │ JournalEntry     │             │
│  │ RestController   │          │ RestController   │             │
│  │ /api/v1/accounts │          │ /api/v1/journal- │             │
│  │                  │          │ entries          │             │
│  └───────┬──────────┘          └───────┬──────────┘             │
│          └──────────────┬──────────────┘                         │
│                         │                                        │
│              ┌──────────┴───────────┐                            │
│              │ AccountService       │                            │
│              │ JournalEntryService  │                            │
│              └──────────┬───────────┘                            │
│                         │                                        │
│  ┌──────────────────────┼────────────────────────────────────┐   │
│  │                  DATABASE                                 │   │
│  │  accounts (self-referencing parent_id for COA hierarchy)  │   │
│  │  journal_entries ── journal_entry_lines                    │   │
│  └───────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────┘
```

---

## Entity Relationship Diagram

```
┌──────────────────┐
│    accounts      │
│──────────────────│
│ id (PK)          │
│ account_number   │
│ name             │
│ description      │
│ account_type     │──► ASSET | LIABILITY | EQUITY | REVENUE | EXPENSE
│ parent_id (FK)───│──► accounts (self-ref for COA tree)
│ active           │
└────────┬─────────┘
         │
         │ Referenced by journal_entry_lines
         │
┌────────┴──────────────┐         ┌───────────────────────────┐
│  journal_entries      │         │ journal_entry_lines       │
│───────────────────────│         │───────────────────────────│
│ id (PK)               │ 1:N     │ id (PK)                  │
│ entry_number          │────────►│ journal_entry_id (FK)     │
│ entry_date            │         │ account_id (FK) ──────────│──► accounts
│ description           │         │ description               │
│ reference             │         │ debit                     │
│ posted (boolean)      │         │ credit                    │
│ reversed (boolean)    │         │ position                  │
│ reversal_of_id (FK)   │         └───────────────────────────┘
└───────────────────────┘
```

---

## Double-Entry Bookkeeping Rules

```
Every JournalEntry must satisfy:

  ┌──────────────────────────────────────────┐
  │  Σ(debit lines) == Σ(credit lines)       │
  │                                           │
  │  Example: Product Sale                    │
  │  ┌──────────────┬──────────┬──────────┐  │
  │  │ Account      │  Debit   │  Credit  │  │
  │  ├──────────────┼──────────┼──────────┤  │
  │  │ Cash (Asset) │ 4'500.00 │          │  │
  │  │ Revenue      │          │ 4'135.04 │  │
  │  │ VAT Payable  │          │   364.96 │  │
  │  └──────────────┴──────────┴──────────┘  │
  │  Total: 4'500.00 == 4'500.00 ✅           │
  └──────────────────────────────────────────┘
```

---

## Journal Entry Lifecycle

```
    ┌─────────┐
    │  DRAFT  │ ◄── create()
    └────┬────┘
         │ post()
         │  ├── Validate debit == credit
         │  ├── Set posted = true
         │  └── Update account balances
         ▼
    ┌─────────┐
    │ POSTED  │
    └────┬────┘
         │ reverse()
         │  ├── Create new entry (mirror debits/credits)
         │  ├── Set original.reversed = true
         │  └── Link reversalOfId
         ▼
    ┌──────────┐
    │ REVERSED │ (terminal)
    └──────────┘

    DRAFT only → delete()
```

---

## API Endpoints

### Accounts (`/api/v1/accounts`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/accounts` | `ACCOUNTING:VIEW` | List (paginated) |
| `GET` | `/api/v1/accounts/{id}` | `ACCOUNTING:VIEW` | Get by ID |
| `GET` | `/api/v1/accounts/{id}/balance` | `ACCOUNTING:VIEW` | Get account balance |
| `POST` | `/api/v1/accounts` | `ACCOUNTING:CREATE` | Create account |
| `PUT` | `/api/v1/accounts/{id}` | `ACCOUNTING:EDIT` | Update account |
| `DELETE` | `/api/v1/accounts/{id}` | `ACCOUNTING:DELETE` | Delete account |

### Journal Entries (`/api/v1/journal-entries`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/journal-entries` | `ACCOUNTING:VIEW` | List (paginated) |
| `GET` | `/api/v1/journal-entries/{id}` | `ACCOUNTING:VIEW` | Get by ID (with lines) |
| `POST` | `/api/v1/journal-entries` | `ACCOUNTING:CREATE` | Create draft entry |
| `POST` | `/api/v1/journal-entries/{id}/post` | `ACCOUNTING:EDIT` | Post entry |
| `POST` | `/api/v1/journal-entries/{id}/reverse` | `ACCOUNTING:EDIT` | Reverse posted entry |
| `DELETE` | `/api/v1/journal-entries/{id}` | `ACCOUNTING:DELETE` | Delete draft entry |

---

## Swiss Business Rules

- **Currency**: All amounts in CHF (`BigDecimal` scale 4)
- **VAT rates**: 8.1% standard, 2.6% reduced, 3.8% accommodation, 0% exempt
- **Account numbering**: Swiss SME standard (1xxx Assets, 2xxx Liabilities, etc.)
- **Audit trail**: Posted entries cannot be edited — only reversed
- **Fiscal year**: January 1 – December 31

---

## Module Dependencies

```
accounting ──depends on──► shared (BaseEntity)
accounting ──may consume──► sales events (future: auto-journal on order confirmation)
```

---

## File Inventory

```
accounting/
├── AccountingModuleApi.java
├── package-info.java
├── model/  (Account, AccountType, JournalEntry, JournalEntryLine)
├── repository/  (AccountRepository, JournalEntryRepository)
├── service/  (AccountService, JournalEntryService)
├── dto/  (Account + JournalEntry + JournalEntryLine Request/Response)
├── web/  (AccountViewController, JournalEntryViewController)
├── api/  (AccountRestController, JournalEntryRestController)
└── event/  (JournalEntryPostedEvent)
```

