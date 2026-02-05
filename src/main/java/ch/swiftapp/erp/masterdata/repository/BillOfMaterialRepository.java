package ch.swiftapp.erp.masterdata.repository;

import ch.swiftapp.erp.masterdata.model.BillOfMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link BillOfMaterial} entities (BOM lines).
 */
@Repository
public interface BillOfMaterialRepository extends JpaRepository<BillOfMaterial, UUID> {

    List<BillOfMaterial> findAllByProductIdOrderByPositionAsc(UUID productId);

    @Query("SELECT b FROM BillOfMaterial b JOIN FETCH b.material JOIN FETCH b.unitOfMeasure WHERE b.product.id = :productId ORDER BY b.position")
    List<BillOfMaterial> findAllByProductIdWithDetails(UUID productId);

    void deleteAllByProductId(UUID productId);

    boolean existsByProductIdAndMaterialId(UUID productId, UUID materialId);
}

