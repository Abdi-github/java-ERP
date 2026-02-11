package ch.swiftapp.erp;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Verifies Spring Modulith module boundaries and inter-module dependencies.
 *
 * <p>This test ensures that modules only communicate through their
 * public API classes and don't access each other's internal packages.</p>
 */
class ModularityTests {

    @Test
    void verifyModuleStructure() {
        ApplicationModules.of(ErpApplication.class).verify();
    }
}

