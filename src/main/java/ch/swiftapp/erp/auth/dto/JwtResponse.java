package ch.swiftapp.erp.auth.dto;

/**
 * Response DTO containing a JWT token after successful authentication.
 */
public record JwtResponse(
        String token,
        String type,
        String username
) {
    public JwtResponse(String token, String username) {
        this("Bearer", token, username);
    }

    /**
     * Creates a new JWT response.
     */
    public static JwtResponse of(String token, String username) {
        return new JwtResponse(token, "Bearer", username);
    }
}

