package ch.swiftapp.erp.purchasing.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * A supplier who provides raw materials or components for watch manufacturing.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "suppliers")
public class Supplier extends BaseEntity {

    @Column(name = "supplier_number", nullable = false, unique = true, length = 30)
    private String supplierNumber;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "street")
    private String street;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "canton", length = 50)
    private String canton;

    @Builder.Default
    @Column(name = "country", nullable = false, length = 3)
    private String country = "CH";

    @Column(name = "vat_number", length = 30)
    private String vatNumber;

    @Builder.Default
    @Column(name = "payment_terms", nullable = false)
    private Integer paymentTerms = 30;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "website")
    private String website;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    /**
     * Returns display name: company name or "FirstName LastName".
     */
    public String getDisplayName() {
        if (companyName != null && !companyName.isBlank()) {
            return companyName;
        }
        return ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
    }
}

