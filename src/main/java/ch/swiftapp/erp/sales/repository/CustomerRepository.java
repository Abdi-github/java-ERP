package ch.swiftapp.erp.sales.repository;

import ch.swiftapp.erp.sales.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link Customer} entities.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByCustomerNumberAndDeletedAtIsNull(String customerNumber);

    Page<Customer> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Customer> findAllByDeletedAtIsNullAndActiveTrue(Pageable pageable);

    @Query("""
            SELECT c FROM Customer c
            WHERE c.deletedAt IS NULL
              AND (LOWER(COALESCE(c.companyName, '')) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(COALESCE(c.firstName, '')) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(COALESCE(c.lastName, '')) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(c.customerNumber) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(COALESCE(c.email, '')) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Customer> search(String search, Pageable pageable);

    boolean existsByCustomerNumberIgnoreCase(String customerNumber);

    boolean existsByEmailIgnoreCaseAndDeletedAtIsNull(String email);
}

