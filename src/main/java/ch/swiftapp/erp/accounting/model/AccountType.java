package ch.swiftapp.erp.accounting.model;

/**
 * Types of accounts in the chart of accounts.
 *
 * <p>Follows standard double-entry bookkeeping classification
 * used in the Swiss Kontenrahmen (chart of accounts).</p>
 */
public enum AccountType {
    /** Assets (Aktiven) — e.g., cash, inventory, receivables */
    ASSET,
    /** Liabilities (Passiven) — e.g., payables, loans */
    LIABILITY,
    /** Equity (Eigenkapital) — e.g., share capital, retained earnings */
    EQUITY,
    /** Revenue (Ertrag) — e.g., sales, interest income */
    REVENUE,
    /** Expense (Aufwand) — e.g., COGS, salaries, rent */
    EXPENSE
}

