package ch.swiftapp.erp.masterdata.repository;

import ch.swiftapp.erp.masterdata.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link Category} entities.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Page<Category> findAllByDeletedAtIsNull(Pageable pageable);

    List<Category> findAllByDeletedAtIsNullAndParentCategoryIsNull();

    List<Category> findAllByDeletedAtIsNull();

    @Query("SELECT c FROM Category c WHERE c.deletedAt IS NULL AND LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Category> searchByName(String search, Pageable pageable);
}

