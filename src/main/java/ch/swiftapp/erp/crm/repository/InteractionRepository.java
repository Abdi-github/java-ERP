package ch.swiftapp.erp.crm.repository;

import ch.swiftapp.erp.crm.model.Interaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, UUID> {
    Page<Interaction> findAllByContactIdOrderByInteractionDateDesc(UUID contactId, Pageable pageable);
    Page<Interaction> findAllByOrderByInteractionDateDesc(Pageable pageable);
}

