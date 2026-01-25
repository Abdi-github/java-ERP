/**
 * Shared kernel module for SwiftApp ERP.
 *
 * <p>Contains base entities, common configuration, value objects,
 * and utility classes shared across all ERP modules.</p>
 *
 * <p>This is an <b>open module</b> — all other modules may depend on
 * classes in this package and its sub-packages.</p>
 */
@org.springframework.modulith.ApplicationModule(
        type = org.springframework.modulith.ApplicationModule.Type.OPEN
)
package ch.swiftapp.erp.shared;

