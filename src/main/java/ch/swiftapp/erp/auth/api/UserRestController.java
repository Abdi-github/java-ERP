package ch.swiftapp.erp.auth.api;

import ch.swiftapp.erp.auth.dto.*;
import ch.swiftapp.erp.auth.service.JwtTokenProvider;
import ch.swiftapp.erp.auth.service.UserService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for user management and authentication — JSON API at {@code /api/v1/auth} and {@code /api/v1/users}.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Auth & Users", description = "Authentication (JWT login) and user management endpoints")
@PreAuthorize("hasAuthority('ADMIN:USERS_VIEW')")
public class UserRestController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    // ── Authentication ────────────────────────────────────────

    @Operation(summary = "Authenticate user", description = "Returns a JWT token for API authentication. No prior authentication required.")
    @PreAuthorize("permitAll()")
    @PostMapping("/auth/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        String token = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(JwtResponse.of(token, request.username()));
    }

    // ── User CRUD ─────────────────────────────────────────────

    @GetMapping("/users")
    public Page<UserResponse> list(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return userService.search(search, pageable);
        }
        return userService.findAll(pageable);
    }

    @GetMapping("/users/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        return userService.findById(id);
    }

    @PreAuthorize("hasAuthority('ADMIN:USERS_MANAGE')")
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        return userService.create(request);
    }

    @PreAuthorize("hasAuthority('ADMIN:USERS_MANAGE')")
    @PutMapping("/users/{id}")
    public UserResponse update(@PathVariable UUID id,
                               @Valid @RequestBody UserRequest request) {
        return userService.update(id, request);
    }

    @PreAuthorize("hasAuthority('ADMIN:USERS_MANAGE')")
    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }
}

