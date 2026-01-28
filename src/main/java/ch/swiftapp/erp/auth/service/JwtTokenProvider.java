package ch.swiftapp.erp.auth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * JWT token provider for stateless API authentication.
 *
 * <p>Generates and validates JWT tokens signed with HMAC-SHA256.
 * Tokens include the user's authorities (roles + permissions) as claims.</p>
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret:SwiftAppErpDefaultSecretKeyThatShouldBeChanged2026}") String secret,
            @Value("${app.jwt.expiration-ms:86400000}") long expirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Generate a JWT token for the authenticated user, embedding authorities.
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return generateToken(userDetails.getUsername(), authorities);
    }

    /**
     * Generate a JWT token for a given username (without authorities).
     */
    public String generateToken(String username) {
        return generateToken(username, List.of());
    }

    /**
     * Generate a JWT token for a given username with explicit authorities.
     */
    public String generateToken(String username, List<String> authorities) {
        var now = new Date();
        var expiry = new Date(now.getTime() + expirationMs);

        var builder = Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry);

        if (authorities != null && !authorities.isEmpty()) {
            builder.claim("authorities", authorities);
        }

        return builder.signWith(signingKey).compact();
    }

    /**
     * Extract the username from a JWT token.
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Extract authorities from a JWT token.
     *
     * @return list of authority strings, or empty list if not present
     */
    @SuppressWarnings("unchecked")
    public List<String> getAuthoritiesFromToken(String token) {
        var claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        var authorities = claims.get("authorities", List.class);
        return authorities != null ? authorities : List.of();
    }

    /**
     * Validate a JWT token.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}

