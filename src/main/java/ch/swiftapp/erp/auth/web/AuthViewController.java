package ch.swiftapp.erp.auth.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Thymeleaf view controller for login and registration pages.
 */
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthViewController {

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }
}

