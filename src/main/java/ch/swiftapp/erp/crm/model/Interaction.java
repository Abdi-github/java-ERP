package ch.swiftapp.erp.crm.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Data @Entity @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "contact")
@ToString(exclude = "contact")
@Table(name = "interactions")
public class Interaction extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "contact_id", nullable = false) private Contact contact;
    @Enumerated(EnumType.STRING) @Column(name = "interaction_type", nullable = false, length = 30) private InteractionType interactionType;
    @Column(name = "subject", nullable = false) private String subject;
    @Column(name = "description", columnDefinition = "TEXT") private String description;
    @Builder.Default @Column(name = "interaction_date", nullable = false) private Instant interactionDate = Instant.now();
    @Column(name = "follow_up_date") private LocalDate followUpDate;
}

