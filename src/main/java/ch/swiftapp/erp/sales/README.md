# 💰 Sales Module

> Sales order management, customer records, order lifecycle, and revenue tracking for Swiss watch retail.

---

## Module Overview

| Property | Value |
|----------|-------|
| **Package** | `ch.swiftapp.erp.sales` |
| **Public API** | `SalesModuleApi` |
| **Entities** | Customer, SalesOrder, SalesOrderLine |
| **Enums** | `SalesOrderStatus` (DRAFT → CONFIRMED → IN_PRODUCTION → SHIPPED → DELIVERED → CANCELLED) |
| **Permissions** | `SALES:VIEW`, `SALES:CREATE`, `SALES:EDIT`, `SALES:DELETE` |
| **Web Routes** | `/app/sales/orders`, `/app/sales/customers` |
| **API Routes** | `/api/v1/sales-orders`, `/api/v1/customers` |

---

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────────┐
│                      SALES MODULE                            │
│                                                              │
│  ┌─────────────────┐          ┌─────────────────┐           │
│  │ Customer        │          │ SalesOrder      │           │
│  │ ViewController  │          │ ViewController  │           │
│  │ /app/sales/     │          │ /app/sales/     │           │
│  │ customers       │          │ orders          │           │
│  └───────┬─────────┘          └───────┬─────────┘           │
│          │                            │                      │
│  ┌───────┴─────────┐          ┌───────┴─────────┐           │
│  │ Customer        │          │ SalesOrder      │           │
│  │ RestController  │          │ RestController  │           │
│  │ /api/v1/        │          │ /api/v1/        │           │
│  │ customers       │          │ sales-orders    │           │
│  └───────┬─────────┘          └───────┬─────────┘           │
│          │                            │                      │
│          └────────────┬───────────────┘                      │
│                       │                                      │
│              ┌────────┴─────────┐                            │
│              │ CustomerService  │                            │
│              │ SalesOrderService│                            │
│              └────────┬─────────┘                            │
│                       │                                      │
│              ┌────────┴─────────┐                            │
│              │  Repositories    │                            │
│              └────────┬─────────┘                            │
│                       │                                      │
│  ┌────────────────────┼──────────────────────────────────┐   │
│  │               DATABASE                                │   │
│  │  customers                                            │   │
│  │  sales_orders ── sales_order_lines (product_id FK)    │   │
│  └───────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
```

---

## Entity Relationship Diagram

```
┌──────────────────┐
│   customers      │
│──────────────────│
│ id (PK)          │
│ customer_number  │
│ company_name     │
│ first_name       │
│ last_name        │
│ email            │
│ phone            │
│ street, city     │
│ postal_code      │
│ canton, country  │
│ vat_number       │
│ payment_terms    │
│ credit_limit     │
│ active           │
└────────┬─────────┘
         │ 1:N
         │
┌────────┴─────────┐         ┌─────────────────────┐
│  sales_orders    │         │ sales_order_lines   │
│──────────────────│         │─────────────────────│
│ id (PK)          │ 1:N     │ id (PK)             │
│ order_number     │────────►│ sales_order_id (FK) │
│ customer_id (FK) │         │ product_id (FK) ────│──► products
│ order_date       │         │ description         │
│ delivery_date    │         │ quantity             │
│ status           │         │ unit_price           │
│ notes            │         │ discount_pct         │
│ shipping_*       │         │ vat_rate             │
│ total_amount     │         │ line_total           │
│ vat_amount       │         │ position             │
└──────────────────┘         └─────────────────────┘
```

---

## Order Lifecycle — State Machine

```
    ┌───────┐
    │ DRAFT │ ◄── create()
    └───┬───┘
        │ confirm()
        ▼
  ┌───────────┐
  │ CONFIRMED │
  └─────┬─────┘
        │ advance()
        ▼
┌──────────────┐
│IN_PRODUCTION │
└──────┬───────┘
       │ advance()
       ▼
  ┌─────────┐
  │ SHIPPED │
  └────┬────┘
       │ advance()
       ▼
  ┌───────────┐
  │ DELIVERED │ (terminal)
  └───────────┘

  Any non-terminal → cancel() → ┌───────────┐
                                │ CANCELLED │ (terminal)
                                └───────────┘
```

---

## Data Flow — Order Confirmation

```
Manager clicks "Confirm" button
         │
         ▼
SalesOrderViewController.confirm(orderId)
         │
         ▼
SalesOrderService.confirm(orderId)
         │
         ├── Load order (must exist, must be DRAFT)
         ├── Validate all lines have valid products
         ├── Calculate totals (subtotal, VAT, grand total)
         ├── Set status = CONFIRMED
         ├── Save to DB
         ├── Publish SalesOrderConfirmedEvent
         │       │
         │       ├──► NotificationListener → notify sales team
         │       └──► (future) InventoryListener → reserve stock
         └── Return updated SalesOrderResponse
```

---

## API Endpoints

### Sales Orders (`/api/v1/sales-orders`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/sales-orders` | `SALES:VIEW` | List (paginated, searchable) |
| `GET` | `/api/v1/sales-orders/{id}` | `SALES:VIEW` | Get by ID (with lines) |
| `POST` | `/api/v1/sales-orders` | `SALES:CREATE` | Create draft order |
| `PUT` | `/api/v1/sales-orders/{id}` | `SALES:EDIT` | Update draft order |
| `DELETE` | `/api/v1/sales-orders/{id}` | `SALES:DELETE` | Delete draft order |
| `POST` | `/api/v1/sales-orders/{id}/confirm` | `SALES:EDIT` | Confirm order |
| `POST` | `/api/v1/sales-orders/{id}/advance` | `SALES:EDIT` | Advance to next status |
| `POST` | `/api/v1/sales-orders/{id}/cancel` | `SALES:EDIT` | Cancel order |

### Customers (`/api/v1/customers`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/customers` | `SALES:VIEW` | List (paginated, searchable) |
| `GET` | `/api/v1/customers/{id}` | `SALES:VIEW` | Get by ID |
| `POST` | `/api/v1/customers` | `SALES:CREATE` | Create customer |
| `PUT` | `/api/v1/customers/{id}` | `SALES:EDIT` | Update customer |
| `DELETE` | `/api/v1/customers/{id}` | `SALES:DELETE` | Delete customer |

---

## Domain Events

| Event | Trigger | Consumers |
|-------|---------|-----------|
| `SalesOrderConfirmedEvent` | Order confirmed | NotificationListener, (future) InventoryListener |
| `SalesOrderCancelledEvent` | Order cancelled | NotificationListener |

---

## Swiss Business Rules

- **Customer number**: Auto-generated sequential format (e.g., `CU-000042`)
- **VAT calculation**: Per-line VAT at Swiss rates (8.1%, 2.6%, 3.8%, 0%)
- **Credit limit**: Enforced in CHF — orders cannot exceed customer credit
- **Payment terms**: Default 30 days, configurable per customer
- **Shipping address**: Swiss canton field for regional logistics

---

## Module Dependencies

```
sales ──depends on──► shared (BaseEntity, VatRate)
sales ──uses────────► masterdata (via MasterdataModuleApi — product lookup)
sales ──publishes───► SalesOrderConfirmedEvent → notification module
```

---

## File Inventory

```
sales/
├── SalesModuleApi.java               ← Public API
├── SalesOrderSummary.java            ← Summary projection
├── MonthlyRevenueSummary.java        ← Revenue aggregation
├── package-info.java
├── model/  (Customer, SalesOrder, SalesOrderLine, SalesOrderStatus)
├── repository/  (CustomerRepository, SalesOrderRepository)
├── service/  (CustomerService, SalesOrderService)
├── dto/  (Customer + SalesOrder + SalesOrderLine Request/Response)
├── web/  (CustomerViewController, SalesOrderViewController)
├── api/  (CustomerRestController, SalesOrderRestController)
└── event/  (SalesOrderConfirmedEvent, SalesOrderCancelledEvent)
```
