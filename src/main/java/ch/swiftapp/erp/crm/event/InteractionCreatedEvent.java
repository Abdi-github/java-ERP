package ch.swiftapp.erp.crm.event;

import java.util.UUID;

public record InteractionCreatedEvent(UUID interactionId, UUID contactId, String subject) {}

