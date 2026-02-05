package ch.swiftapp.erp.masterdata.dto;

import ch.swiftapp.erp.shared.model.VatRate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for {@link ch.swiftapp.erp.masterdata.model.Product}.
 * {@code name} and {@code description} are locale-resolved for the current request.
 * {@code nameTranslations} / {@code descriptionTranslations} expose the full translation
 * map (locale → value) for edit forms.
 */
public record ProductResponse(
        UUID id,
        String sku,
        String name,
        String description,
        UUID categoryId,
        String categoryName,
        BigDecimal unitPrice,
        BigDecimal listPrice,
        VatRate vatRate,
        Boolean active,
        Instant createdAt,
        Instant updatedAt,
        Map<String, String> nameTranslations,
        Map<String, String> descriptionTranslations
) {}

