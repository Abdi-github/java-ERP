package ch.swiftapp.erp.shared.web;

import ch.swiftapp.erp.shared.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * View controller for the main ERP dashboard.
 *
 * <p>Aggregates KPIs and activity summaries from all operational modules
 * and exposes them to the Thymeleaf template.</p>
 */
@Controller
@RequestMapping("/app")
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('DASHBOARD:VIEW')")
public class DashboardViewController {

    private final DashboardService dashboardService;

    /** Bootstrap badge CSS classes for each sales order status. */
    private static final Map<String, String> SALES_STATUS_BADGE = Map.of(
            "DRAFT",      "bg-secondary",
            "CONFIRMED",  "bg-primary",
            "PROCESSING", "bg-info text-dark",
            "SHIPPED",    "bg-warning text-dark",
            "DELIVERED",  "bg-success",
            "INVOICED",   "bg-success",
            "COMPLETED",  "bg-success",
            "CANCELLED",  "bg-danger"
    );

    /** Bootstrap badge CSS classes for each production order status. */
    private static final Map<String, String> PROD_STATUS_BADGE = Map.of(
            "PLANNED",     "bg-secondary",
            "RELEASED",    "bg-primary",
            "IN_PROGRESS", "bg-info text-dark",
            "ON_HOLD",     "bg-warning text-dark",
            "COMPLETED",   "bg-success",
            "CANCELLED",   "bg-danger"
    );

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        log.debug("Rendering ERP dashboard");

        model.addAttribute("stats",                  dashboardService.getStats());
        model.addAttribute("recentSalesOrders",      dashboardService.getRecentSalesOrders());
        model.addAttribute("recentProductionOrders", dashboardService.getRecentProductionOrders());
        model.addAttribute("monthlyRevenue",         dashboardService.getLast6MonthsRevenue());
        model.addAttribute("salesStatusBreakdown",   dashboardService.getSalesStatusBreakdown());
        model.addAttribute("salesStatusBadge",       SALES_STATUS_BADGE);
        model.addAttribute("prodStatusBadge",        PROD_STATUS_BADGE);

        return "app/dashboard";
    }
}


