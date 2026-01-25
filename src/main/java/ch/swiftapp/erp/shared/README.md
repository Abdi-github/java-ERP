# 🔧 Shared Module

> Cross-cutting base classes, configuration, value objects, and shared services used by all ERP modules.

---

## Module Overview

| Property | Value |
|----------|-------|
| **Package** | `ch.swiftapp.erp.shared` |
| **Type** | Infrastructure / Cross-cutting |
| **Provides** | BaseEntity, VatRate, TranslationResolver, SecurityConfig, GlobalExceptionHandler, DashboardController |
| **Used By** | Every other module in the system |

---

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                       SHARED MODULE                              │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                    config/                                │   │
│  │  SecurityConfig      ← Dual filter chains (Web + API)    │   │
│  │  AsyncConfig         ← @Async executor for notifications │   │
│  │  WebMvcConfig        ← Locale resolver, interceptors     │   │
│  │  AuditConfig         ← JPA auditing (createdBy, etc.)    │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                    model/                                 │   │
│  │  BaseEntity          ← id, createdAt, updatedAt,         │   │
│  │                        createdBy, updatedBy, version      │   │
│  │  VatRate (enum)      ← STANDARD_8_1, REDUCED_2_6,        │   │
│  │                        ACCOMMODATION_3_8, EXEMPT_0        │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                    service/                               │   │
│  │  TranslationResolver ← Resolves locale-aware names       │   │
│  │  GlobalExceptionHandler ← RFC 7807 Problem Details       │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                    web/                                   │   │
│  │  DashboardController ← /app/dashboard (aggregates data)  │   │
│  │  ErrorController     ← Custom error pages                │   │
│  └──────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────┘
```

---

## BaseEntity Class Hierarchy

```
┌──────────────────────────────┐
│         BaseEntity           │
│──────────────────────────────│
│ @Id UUID id                  │ ← Auto-generated
│ @CreatedDate Instant         │
│   createdAt                  │
│ @LastModifiedDate Instant    │
│   updatedAt                  │
│ @CreatedBy String createdBy  │ ← From SecurityContext
│ @LastModifiedBy String       │
│   updatedBy                  │
│ @Version Long version        │ ← Optimistic locking
└──────────────┬───────────────┘
               │ extends
    ┌──────────┼──────────────────┐
    │          │                  │
Product   SalesOrder    Employee  ...  (all entities)
```

---

## VatRate Enum

```
┌─────────────────────┬──────────┬────────────────────┐
│ Enum Value          │  Rate    │ Description        │
├─────────────────────┼──────────┼────────────────────┤
│ STANDARD_8_1        │  8.1%    │ Standard Swiss VAT │
│ REDUCED_2_6         │  2.6%    │ Food, books, etc.  │
│ ACCOMMODATION_3_8   │  3.8%    │ Hotel/lodging      │
│ EXEMPT_0            │  0.0%    │ Exempt goods       │
└─────────────────────┴──────────┴────────────────────┘

Each has a .rate() method returning BigDecimal.
```

---

## Security Configuration — Dual Filter Chains

```
┌─────────────────────────────────────────────────────────┐
│               SecurityConfig                            │
│                                                         │
│  Filter Chain 1: Web (order = 1)                        │
│  ┌───────────────────────────────────────────────┐     │
│  │ Pattern:   /app/**, /auth/**                   │     │
│  │ Auth:      Session-based (form login)          │     │
│  │ CSRF:      Enabled                             │     │
│  │ Login:     /auth/login                         │     │
│  │ Logout:    /auth/logout                        │     │
│  │ Remember:  HTTP-only cookie                    │     │
│  └───────────────────────────────────────────────┘     │
│                                                         │
│  Filter Chain 2: API (order = 2)                        │
│  ┌───────────────────────────────────────────────┐     │
│  │ Pattern:   /api/v1/**                          │     │
│  │ Auth:      JWT (Bearer token)                  │     │
│  │ CSRF:      Disabled                            │     │
│  │ Session:   STATELESS                           │     │
│  │ Filter:    JwtAuthenticationFilter             │     │
│  └───────────────────────────────────────────────┘     │
│                                                         │
│  Public paths (no auth required):                       │
│  /auth/login, /api/v1/auth/login, /static/**,           │
│  /error, /actuator/health                               │
└─────────────────────────────────────────────────────────┘
```

---

## Error Handling — RFC 7807

```
GlobalExceptionHandler handles:

  ┌─────────────────────────┬──────┬───────────────────────────┐
  │ Exception               │ Code │ Problem Detail            │
  ├─────────────────────────┼──────┼───────────────────────────┤
  │ EntityNotFoundException │ 404  │ type: /errors/not-found   │
  │ ValidationException     │ 400  │ type: /errors/validation  │
  │ DuplicateException      │ 409  │ type: /errors/conflict    │
  │ AccessDeniedException   │ 403  │ type: /errors/forbidden   │
  │ Generic Exception       │ 500  │ type: /errors/internal    │
  └─────────────────────────┴──────┴───────────────────────────┘

Response format:
  {
    "type": "about:blank",
    "title": "Not Found",
    "status": 404,
    "detail": "Product with id 123 not found",
    "instance": "/api/v1/products/123"
  }
```

---

## Dashboard Aggregation

```
DashboardController (/app/dashboard)
         │
         ├── SalesModuleApi.getOrderSummary()     → pending/confirmed counts
         ├── SalesModuleApi.getMonthlyRevenue()    → revenue chart data
         ├── ProductionModuleApi.getSummary()       → active production orders
         ├── InventoryModuleApi.getLowStockCount()  → low stock alerts
         ├── PurchasingModuleApi.getPendingPOs()    → awaiting delivery
         └── NotificationModuleApi.getUnread()      → unread count
                  │
                  ▼
         model.addAttribute(...)
         return "app/dashboard"
```

---

## Internationalization

```
messages.properties (default = de)
messages_de.properties (German)
messages_fr.properties (French)
messages_it.properties (Italian)
messages_en.properties (English)

TranslationResolver resolves entity translations:
  product.getName(locale) → checks translation table → fallback to default name
```

---

## Module Dependencies

```
shared ──depends on──► (none — foundation module)
ALL other modules ──depend on──► shared
```

---

## File Inventory

```
shared/
├── package-info.java
├── config/
│   ├── SecurityConfig.java         ← Dual filter chains
│   ├── AsyncConfig.java            ← Thread pool for notifications
│   ├── WebMvcConfig.java           ← Locale, interceptors
│   └── AuditConfig.java            ← JPA auditing
├── model/
│   ├── BaseEntity.java             ← Abstract superclass for all entities
│   └── VatRate.java                ← Swiss VAT rate enum
├── service/
│   ├── TranslationResolver.java    ← Locale-aware name resolution
│   └── GlobalExceptionHandler.java ← RFC 7807 error responses
└── web/
    ├── DashboardController.java    ← /app/dashboard
    └── ErrorController.java        ← Custom error pages
```
