package ch.swiftapp.erp.crm.repository;

import ch.swiftapp.erp.crm.model.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {
    Page<Contact> findAllByDeletedAtIsNull(Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE c.deletedAt IS NULL AND (LOWER(c.firstName) LIKE LOWER(CONCAT('%',:s,'%')) OR LOWER(c.lastName) LIKE LOWER(CONCAT('%',:s,'%')) OR LOWER(c.company) LIKE LOWER(CONCAT('%',:s,'%')))")
    Page<Contact> searchContacts(String s, Pageable pageable);
}

