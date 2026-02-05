package ch.swiftapp.erp.masterdata;

import ch.swiftapp.erp.masterdata.dto.MaterialResponse;
import ch.swiftapp.erp.masterdata.dto.ProductResponse;

import java.util.Optional;
import java.util.UUID;

/**
 * Public API for the Masterdata module.
 *
 * <p>Other Spring Modulith modules should depend on this interface only —
 * never on internal model, repository, or service classes.</p>
 */
public interface MasterdataModuleApi {

    /**
     * Find a product by its ID.
     *
     * @param id the product UUID
     * @return the product response, or empty if not found
     */
    Optional<ProductResponse> findProductById(UUID id);

    /**
     * Find a product by its SKU.
     *
     * @param sku the unique stock keeping unit code
     * @return the product response, or empty if not found
     */
    Optional<ProductResponse> findProductBySku(String sku);

    /**
     * Check if a product exists and is active.
     *
     * @param id the product UUID
     * @return true if the product exists, is active, and not deleted
     */
    boolean isProductActive(UUID id);

    /**
     * Find a material by its ID.
     *
     * @param id the material UUID
     * @return the material response, or empty if not found
     */
    Optional<MaterialResponse> findMaterialById(UUID id);

    /**
     * Check if a material exists and is not deleted.
     *
     * @param id the material UUID
     * @return true if the material exists and is not deleted
     */
    boolean isMaterialActive(UUID id);
}

