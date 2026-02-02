/**
 * Purchasing module — Purchase orders, supplier management, goods receipt.
 */
@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {"shared", "masterdata", "inventory"}
)
package ch.swiftapp.erp.purchasing;

