package ch.swiftapp.erp.notification.web;

import ch.swiftapp.erp.auth.AuthModuleApi;
import ch.swiftapp.erp.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Global model attribute provider for notification data.
 *
 * <p>Injects the unread notification count into every Thymeleaf view
 * so the notification bell badge can render on initial page load.</p>
 *
 * <p>Only active for {@code /app/**} views (Thymeleaf SSR controllers).</p>
 */
@ControllerAdvice(basePackages = {
        "ch.swiftapp.erp.notification.web",
        "ch.swiftapp.erp.sales.web",
        "ch.swiftapp.erp.purchasing.web",
        "ch.swiftapp.erp.production.web",
        "ch.swiftapp.erp.inventory.web",
        "ch.swiftapp.erp.accounting.web",
        "ch.swiftapp.erp.hr.web",
        "ch.swiftapp.erp.crm.web",
        "ch.swiftapp.erp.qualitycontrol.web",
        "ch.swiftapp.erp.masterdata.web",
        "ch.swiftapp.erp.auth.web"
})
@RequiredArgsConstructor
@Slf4j
public class NotificationModelAdvice {

    private final NotificationService notificationService;
    private final AuthModuleApi authModuleApi;

    /**
     * Injects the unread notification count into every Thymeleaf model.
     *
     * @return unread count for the current user, or 0 if not authenticated
     */
    @ModelAttribute("unreadNotificationCount")
    public long unreadNotificationCount() {
        try {
            String username = authModuleApi.getCurrentUsername();
            if ("system".equals(username) || "anonymousUser".equals(username)) {
                return 0L;
            }
            return authModuleApi.findUserByUsername(username)
                    .map(user -> notificationService.countUnread(user.id()))
                    .orElse(0L);
        } catch (Exception e) {
            log.debug("Could not resolve unread notification count: {}", e.getMessage());
            return 0L;
        }
    }
}

