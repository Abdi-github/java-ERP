package ch.swiftapp.erp.sales.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * A customer who purchases watches or services.
 *
 * <p>Customers may be individuals (B2C) or companies (B2B).
 * Swiss-specific fields include canton and VAT number (CHE-xxx.xxx.xxx MWST).</p>
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "customers")
public class Customer extends BaseEntity {

    @Column(name = "customer_number", nullable = false, unique = true, length = 30)
    private String customerNumber;

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

    @Builder.Default
    @Column(name = "credit_limit", nullable = false, precision = 19, scale = 4)
    private BigDecimal creditLimit = BigDecimal.ZERO;

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

