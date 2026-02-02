package ch.swiftapp.erp.crm;

import ch.swiftapp.erp.crm.dto.ContactResponse;
import java.util.Optional;
import java.util.UUID;

/**
 * Public API for the CRM module.
 */
public interface CrmModuleApi {
    Optional<ContactResponse> findContactById(UUID id);
}

