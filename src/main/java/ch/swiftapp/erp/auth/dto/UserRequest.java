package ch.swiftapp.erp.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * Request DTO for creating or updating a {@link ch.swiftapp.erp.auth.model.User}.
 */
public record UserRequest(

        @NotBlank(message = "Username is required")
        @Size(max = 100, message = "Username must not exceed 100 characters")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
        String password,

        @Size(max = 100, message = "First name must not exceed 100 characters")
        String firstName,

        @Size(max = 100, message = "Last name must not exceed 100 characters")
        String lastName,

        Boolean enabled,

        Set<String> roleNames
) {}

