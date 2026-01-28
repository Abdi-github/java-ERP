/**
 * Authentication and authorization module for SwiftApp ERP.
 *
 * <p>Manages user accounts, roles (RBAC), login/logout,
 * and JWT token issuance for the REST API.</p>
 *
 * <p>Other modules should depend only on {@link ch.swiftapp.erp.auth.AuthModuleApi}
 * and not access internal sub-packages directly.</p>
 */
@org.springframework.modulith.NamedInterface("auth")
package ch.swiftapp.erp.auth;

