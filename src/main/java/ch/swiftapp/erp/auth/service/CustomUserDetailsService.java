package ch.swiftapp.erp.auth.service;

import ch.swiftapp.erp.auth.model.Permission;
import ch.swiftapp.erp.auth.model.User;
import ch.swiftapp.erp.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom Spring Security {@link UserDetailsService} backed by the ERP users table.
 *
 * <p>Loads both {@code ROLE_*} authorities (from the user's roles) and
 * permission-based authorities (from each role's assigned permissions).
 * This enables both {@code hasRole('ADMIN')} and {@code hasAuthority('SALES:VIEW')}
 * checks in Spring Security expressions.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameIgnoreCaseAndDeletedAtIsNull(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<GrantedAuthority> authorities = new ArrayList<>();

        // Add ROLE_* authorities from roles
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

            // Add permission-based authorities from each role
            role.getPermissions().forEach(permission ->
                    authorities.add(new SimpleGrantedAuthority(permission.getCode()))
            );
        });

        log.debug("Loaded user '{}' with {} authorities ({} roles, {} permissions)",
                user.getUsername(), authorities.size(),
                user.getRoles().size(),
                authorities.size() - user.getRoles().size());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                user.getEnabled(),
                true,              // accountNonExpired
                true,              // credentialsNonExpired
                !user.getLocked(),  // accountNonLocked
                authorities
        );
    }
}

