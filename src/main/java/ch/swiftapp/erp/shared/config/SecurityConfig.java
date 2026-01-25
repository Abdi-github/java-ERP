package ch.swiftapp.erp.shared.config;

import ch.swiftapp.erp.auth.service.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration with two filter chains:
 * <ol>
 *     <li>API chain ({@code /api/**}) — stateless, JWT-based</li>
 *     <li>Web chain ({@code /app/**}) — session-based, form login</li>
 * </ol>
 *
 * <p>All application routes are secured. Static resources and auth endpoints are public.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * API security — stateless, JWT Bearer token.
     * Auth endpoints are public; Swagger/OpenAPI docs require ADMIN role;
     * all other API endpoints require a valid JWT.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                // OpenAPI JSON spec — ADMIN only
                .requestMatchers("/api/v1/api-docs/**", "/api/v1/api-docs").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Web security — session-based form login for Thymeleaf views.
     *
     * <ul>
     *   <li>Static assets, auth pages, actuator health → public</li>
     *   <li>Swagger UI & OpenAPI docs → ADMIN role only</li>
     *   <li>{@code /app/admin/**} → ADMIN role only</li>
     *   <li>All other {@code /app/**} → any authenticated user</li>
     * </ul>
     */
    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/**")
            .authorizeHttpRequests(auth -> auth
                // Public resources
                .requestMatchers(
                    "/auth/**",
                    "/css/**", "/js/**", "/images/**", "/vendor/**", "/fonts/**",
                    "/webjars/**",
                    "/actuator/health",
                    "/error",
                    "/"
                ).permitAll()
                // Swagger UI & OpenAPI spec — ADMIN only
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/v3/api-docs"
                ).hasRole("ADMIN")
                // All app routes — any authenticated user (fine-grained via @PreAuthorize)
                .requestMatchers("/app/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/app/dashboard", true)
                .failureUrl("/auth/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403")
            );
        return http.build();
    }
}
