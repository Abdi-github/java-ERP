package ch.swiftapp.erp.notification.web;

import ch.swiftapp.erp.auth.AuthModuleApi;
import ch.swiftapp.erp.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * Thymeleaf view controller for the notification centre at {@code /app/notifications}.
 */
@Controller
@RequestMapping("/app/notifications")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('NOTIFICATIONS:VIEW')")
public class NotificationViewController {

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
    public String list(@PageableDefault(size = 20) Pageable pageable, Model model) {
        UUID userId = resolveCurrentUserId();
        if (userId != null) {
            model.addAttribute("notifications", notificationService.findForUser(userId, pageable));
            model.addAttribute("unreadCount", notificationService.countUnread(userId));
        } else {
            model.addAttribute("notifications", org.springframework.data.domain.Page.empty(pageable));
            model.addAttribute("unreadCount", 0L);
        }
        return "app/notifications/list";
    }

    @PreAuthorize("hasAuthority('NOTIFICATIONS:CREATE')")
    @PostMapping("/{id}/read")
    public String markRead(@PathVariable UUID id) {
        notificationService.markRead(id);
        return "redirect:/app/notifications";
    }

    @PreAuthorize("hasAuthority('NOTIFICATIONS:CREATE')")
    @PostMapping("/read-all")
    public String markAllRead(RedirectAttributes redirectAttributes) {
        UUID userId = resolveCurrentUserId();
        if (userId != null) {
            notificationService.markAllRead(userId);
        }
        redirectAttributes.addFlashAttribute("successMessage", "Alle Benachrichtigungen als gelesen markiert.");
        return "redirect:/app/notifications";
    }

    @PreAuthorize("hasAuthority('NOTIFICATIONS:CREATE')")
    @PostMapping("/{id}/dismiss")
    public String dismiss(@PathVariable UUID id) {
        notificationService.dismiss(id);
        return "redirect:/app/notifications";
    }
}


