/**
 * Accounting module for SwiftApp ERP.
 *
 * <p>Manages the chart of accounts, journal entries, general ledger,
 * and Swiss QR-bill generation for invoicing.</p>
 *
 * <p>Other modules should depend only on {@link ch.swiftapp.erp.accounting.AccountingModuleApi}
 * and not access internal sub-packages directly.</p>
 */
@org.springframework.modulith.NamedInterface("accounting")
package ch.swiftapp.erp.accounting;

