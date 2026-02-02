package ch.swiftapp.erp.crm.service;

import ch.swiftapp.erp.crm.CrmModuleApi;
import ch.swiftapp.erp.crm.dto.ContactResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class CrmModuleApiFacade implements CrmModuleApi {
    private final ContactService contactService;
    @Override public Optional<ContactResponse> findContactById(UUID id) { return contactService.findByIdOptional(id); }
}

