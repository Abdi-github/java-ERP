package ch.swiftapp.erp.shared.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for error pages (403 Forbidden, etc.).
 */
@Controller
@RequestMapping("/error")
public class ErrorViewController {

    /**
     * Access Denied (403) page.
     * Shown when an authenticated user tries to access a resource they don't have permission for.
     */
    @GetMapping("/403")
    public String accessDenied() {
        return "error/403";
    }
}

