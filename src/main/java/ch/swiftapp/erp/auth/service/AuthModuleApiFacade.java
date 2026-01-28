package ch.swiftapp.erp.auth.service;

import ch.swiftapp.erp.auth.AuthModuleApi;
import ch.swiftapp.erp.auth.dto.UserResponse;
import ch.swiftapp.erp.auth.model.Permission;
import ch.swiftapp.erp.auth.model.Role;
import ch.swiftapp.erp.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Facade implementing {@link AuthModuleApi} for cross-module access.
 */
@Service
@RequiredArgsConstructor
public class AuthModuleApiFacade implements AuthModuleApi {

    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public Optional<UserResponse> findUserById(UUID id) {
        return userService.findByIdOptional(id);
    }

    @Override
    public Optional<UserResponse> findUserByUsername(String username) {
        return userService.findByUsername(username);
    }

    @Override
    public String getCurrentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "system";
    }

    @Override
    public List<UserResponse> findAllEnabledUsers() {
        return userService.findAllEnabled();
    }

    @Override
    public List<UserResponse> findAllByRole(String roleName) {
        return userService.findAllByRole(roleName);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getPermissionsForUser(UUID userId) {
        return userRepository.findById(userId)
                .filter(u -> u.getDeletedAt() == null)
                .map(user -> user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .map(Permission::getCode)
                        .collect(Collectors.toSet()))
                .orElse(Set.of());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(String username, String permissionCode) {
        return userRepository.findByUsernameIgnoreCaseAndDeletedAtIsNull(username)
                .map(user -> user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .anyMatch(p -> p.getCode().equals(permissionCode)))
                .orElse(false);
    }
}

