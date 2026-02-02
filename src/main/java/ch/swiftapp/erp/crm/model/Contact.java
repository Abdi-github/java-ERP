package ch.swiftapp.erp.crm.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data @Entity @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "contacts")
public class Contact extends BaseEntity {
    @Column(name = "first_name", nullable = false, length = 100) private String firstName;
    @Column(name = "last_name", nullable = false, length = 100) private String lastName;
    @Column(name = "email") private String email;
    @Column(name = "phone", length = 50) private String phone;
    @Column(name = "company") private String company;
    @Column(name = "position") private String position;
    @Column(name = "customer_id") private UUID customerId;
    @Column(name = "notes", columnDefinition = "TEXT") private String notes;
    @Builder.Default @Column(name = "active", nullable = false) private Boolean active = true;
    @Column(name = "deleted_at") private Instant deletedAt;

    public String getFullName() { return firstName + " " + lastName; }
}

