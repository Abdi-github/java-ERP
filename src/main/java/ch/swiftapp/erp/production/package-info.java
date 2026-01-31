/**
 * Production module — Production orders, work centers, manufacturing scheduling.
 */
@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {"shared", "masterdata", "inventory"}
)
package ch.swiftapp.erp.production;

