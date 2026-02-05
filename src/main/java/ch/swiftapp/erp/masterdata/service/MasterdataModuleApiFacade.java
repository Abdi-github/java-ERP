package ch.swiftapp.erp.masterdata.service;

import ch.swiftapp.erp.masterdata.MasterdataModuleApi;
import ch.swiftapp.erp.masterdata.dto.MaterialResponse;
import ch.swiftapp.erp.masterdata.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the public Masterdata module API.
 *
 * <p>This is the only class other modules should interact with —
 * it delegates to internal services.</p>
 */
@Component
@RequiredArgsConstructor
public class MasterdataModuleApiFacade implements MasterdataModuleApi {

    private final ProductService productService;
    private final MaterialService materialService;

    @Override
    public Optional<ProductResponse> findProductById(UUID id) {
        return productService.findByIdOptional(id);
    }

    @Override
    public Optional<ProductResponse> findProductBySku(String sku) {
        return productService.findBySku(sku);
    }

    @Override
    public boolean isProductActive(UUID id) {
        return productService.isProductActive(id);
    }

    @Override
    public Optional<MaterialResponse> findMaterialById(UUID id) {
        return materialService.findByIdOptional(id);
    }

    @Override
    public boolean isMaterialActive(UUID id) {
        try {
            materialService.findById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

