package ch.swiftapp.erp.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3 / Swagger UI configuration for SwiftApp ERP.
 *
 * <p>Generates API documentation for all REST endpoints
 * under {@code /api/v1/**}. The Swagger UI is restricted to ADMIN users
 * via security rules in {@link SecurityConfig}.</p>
 *
 * <p>Access the documentation at:</p>
 * <ul>
 *     <li>Swagger UI: {@code /swagger-ui.html}</li>
 *     <li>OpenAPI JSON: {@code /api/v1/api-docs}</li>
 * </ul>
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    /**
     * Global OpenAPI metadata — title, version, contact, security scheme.
     */
    @Bean
    public OpenAPI swiftAppOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SwiftApp ERP API")
                        .description("""
                                RESTful API for **SwiftApp ERP** — a Swiss luxury watch manufacturing \
                                & retail enterprise resource planning system.

                                ## Authentication
                                All endpoints (except `/api/v1/auth/login`) require a valid **JWT Bearer token**.
                                1. Call `POST /api/v1/auth/login` with your credentials
                                2. Copy the `token` from the response
                                3. Click **Authorize** 🔒 and paste: `Bearer <your-token>`

                                ## Modules
                                The API is organized by ERP module:
                                - **Auth** — Authentication & user management
                                - **Master Data** — Products, materials, categories, units of measure, BOM
                                - **Sales** — Customers & sales orders
                                - **Purchasing** — Suppliers & purchase orders
                                - **Production** — Production orders & work centers
                                - **Inventory** — Warehouses, stock levels & movements
                                - **Accounting** — Chart of accounts & journal entries
                                - **HR** — Employees & departments
                                - **CRM** — Contacts & interactions
                                - **Notifications** — User notifications & mail campaigns
                                - **Quality Control** — Non-conformance reports (NCRs)

                                ## Conventions
                                - All IDs are **UUID**
                                - Monetary values use **BigDecimal** (scale 4) in **CHF**
                                - Pagination via `page`, `size`, `sort` query parameters
                                - Errors follow **RFC 7807** Problem Details format
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SwiftApp ERP Team")
                                .email("dev@swiftapp.ch")
                                .url("https://swiftapp.ch"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://swiftapp.ch/license")))
                .externalDocs(new ExternalDocumentation()
                        .description("SwiftApp ERP Project Documentation")
                        .url("https://swiftapp.ch/docs"))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development"),
                        new Server().url("http://localhost:80").description("Docker Compose"),
                        new Server().url("https://erp.swiftapp.ch").description("Production")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter your JWT token obtained from `POST /api/v1/auth/login`")));
    }

    // ── Grouped APIs (one per ERP module) ──────────────────────────

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("01-auth")
                .displayName("Auth & Users")
                .packagesToScan("ch.swiftapp.erp.auth.api")
                .build();
    }

    @Bean
    public GroupedOpenApi masterdataApi() {
        return GroupedOpenApi.builder()
                .group("02-masterdata")
                .displayName("Master Data")
                .packagesToScan("ch.swiftapp.erp.masterdata.api")
                .build();
    }

    @Bean
    public GroupedOpenApi salesApi() {
        return GroupedOpenApi.builder()
                .group("03-sales")
                .displayName("Sales")
                .packagesToScan("ch.swiftapp.erp.sales.api")
                .build();
    }

    @Bean
    public GroupedOpenApi purchasingApi() {
        return GroupedOpenApi.builder()
                .group("04-purchasing")
                .displayName("Purchasing")
                .packagesToScan("ch.swiftapp.erp.purchasing.api")
                .build();
    }

    @Bean
    public GroupedOpenApi productionApi() {
        return GroupedOpenApi.builder()
                .group("05-production")
                .displayName("Production")
                .packagesToScan("ch.swiftapp.erp.production.api")
                .build();
    }

    @Bean
    public GroupedOpenApi inventoryApi() {
        return GroupedOpenApi.builder()
                .group("06-inventory")
                .displayName("Inventory")
                .packagesToScan("ch.swiftapp.erp.inventory.api")
                .build();
    }

    @Bean
    public GroupedOpenApi accountingApi() {
        return GroupedOpenApi.builder()
                .group("07-accounting")
                .displayName("Accounting")
                .packagesToScan("ch.swiftapp.erp.accounting.api")
                .build();
    }

    @Bean
    public GroupedOpenApi hrApi() {
        return GroupedOpenApi.builder()
                .group("08-hr")
                .displayName("Human Resources")
                .packagesToScan("ch.swiftapp.erp.hr.api")
                .build();
    }

    @Bean
    public GroupedOpenApi crmApi() {
        return GroupedOpenApi.builder()
                .group("09-crm")
                .displayName("CRM")
                .packagesToScan("ch.swiftapp.erp.crm.api")
                .build();
    }

    @Bean
    public GroupedOpenApi notificationsApi() {
        return GroupedOpenApi.builder()
                .group("10-notifications")
                .displayName("Notifications")
                .packagesToScan("ch.swiftapp.erp.notification.api")
                .build();
    }

    @Bean
    public GroupedOpenApi qualityControlApi() {
        return GroupedOpenApi.builder()
                .group("11-quality-control")
                .displayName("Quality Control")
                .packagesToScan("ch.swiftapp.erp.qualitycontrol.api")
                .build();
    }

    /**
     * Catch-all group that shows every {@code /api/v1/**} endpoint.
     */
    @Bean
    public GroupedOpenApi allApisGroup() {
        return GroupedOpenApi.builder()
                .group("00-all")
                .displayName("All APIs")
                .pathsToMatch("/api/v1/**")
                .build();
    }
}

