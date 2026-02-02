package ch.swiftapp.erp.notification.api;

import ch.swiftapp.erp.auth.AuthModuleApi;
import ch.swiftapp.erp.notification.dto.NotificationResponse;
import ch.swiftapp.erp.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * REST controller for user notifications at {@code /api/v1/notifications}.
 *
 * <p>Provides endpoints for the notification centre: listing, reading,
 * dismissing, and checking unread counts for the authenticated user.</p>
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "User notification centre — list, read, dismiss")
@PreAuthorize("hasAuthority('NOTIFICATIONS:VIEW')")
public class NotificationRestController {

    private final NotificationService notificationService;
    private final AuthModuleApi authModuleApi;

    private UUID resolveCurrentUserId() {
        String username = authModuleApi.getCurrentUsername();
        if ("system".equals(username) || "anonymousUser".equals(username)) {
            return null;
        }
        return authModuleApi.findUserByUsername(username)
                .map(u -> u.id())
                .orElse(null);
    }

    @GetMapping
    public Page<NotificationResponse> list(@PageableDefault(size = 20) Pageable pageable) {
        UUID userId = resolveCurrentUserId();
        if (userId == null) return Page.empty(pageable);
        return notificationService.findForUser(userId, pageable);
    }

    @GetMapping("/{id}")
    public NotificationResponse getById(@PathVariable UUID id) {
        return notificationService.findById(id);
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount() {
        UUID userId = resolveCurrentUserId();
        if (userId == null) return Map.of("count", 0L);
        return Map.of("count", notificationService.countUnread(userId));
    }

    @PreAuthorize("hasAuthority('NOTIFICATIONS:CREATE')")
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable UUID id) {
        notificationService.markRead(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('NOTIFICATIONS:CREATE')")
    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllRead() {
        UUID userId = resolveCurrentUserId();
        if (userId != null) notificationService.markAllRead(userId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('NOTIFICATIONS:CREATE')")
    @PostMapping("/{id}/dismiss")
    public ResponseEntity<Void> dismiss(@PathVariable UUID id) {
        notificationService.dismiss(id);
        return ResponseEntity.ok().build();
    }
}

