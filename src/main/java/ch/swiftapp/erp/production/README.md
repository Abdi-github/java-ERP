# ⚙️ Production Module

> Production order management, work centers, and manufacturing lifecycle for Swiss watch assembly.

---

## Module Overview

| Property | Value |
|----------|-------|
| **Package** | `ch.swiftapp.erp.production` |
| **Public API** | `ProductionModuleApi` |
| **Entities** | ProductionOrder, ProductionOrderLine, WorkCenter |
| **Enums** | `ProductionOrderStatus` (DRAFT → RELEASED → IN_PROGRESS → ON_HOLD → COMPLETED → CANCELLED) |
| **Translation** | WorkCenter (companion table) |
| **Permissions** | `PRODUCTION:VIEW`, `PRODUCTION:CREATE`, `PRODUCTION:EDIT`, `PRODUCTION:DELETE` |
| **Web Routes** | `/app/production/orders`, `/app/production/work-centers` |
| **API Routes** | `/api/v1/production-orders`, `/api/v1/work-centers` |

---

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                     PRODUCTION MODULE                            │
│                                                                  │
│  ┌──────────────────┐          ┌──────────────────┐             │
│  │ ProductionOrder  │          │ WorkCenter       │             │
│  │ ViewController   │          │ ViewController   │             │
│  │ /app/production/ │          │ /app/production/ │             │
│  │ orders           │          │ work-centers     │             │
│  └───────┬──────────┘          └───────┬──────────┘             │
│          │                             │                         │
│  ┌───────┴──────────┐          ┌───────┴──────────┐             │
│  │ ProductionOrder  │          │ WorkCenter       │             │
│  │ RestController   │          │ RestController   │             │
│  │ /api/v1/         │          │ /api/v1/         │             │
│  │ production-orders│          │ work-centers     │             │
│  └───────┬──────────┘          └───────┬──────────┘             │
│          └──────────────┬──────────────┘                         │
│                         │                                        │
│              ┌──────────┴───────────┐                            │
│              │ ProductionOrderSvc   │                            │
│              │ WorkCenterService    │                            │
│              └──────────┬───────────┘                            │
│                         │                                        │
│  ┌──────────────────────┼────────────────────────────────────┐   │
│  │                  DATABASE                                 │   │
│  │  work_centers ── work_center_translations                 │   │
│  │  production_orders ── production_order_lines               │   │
│  └───────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────┘
```

---

## Entity Relationship Diagram

```
┌──────────────────┐       ┌──────────────────────┐
│  work_centers    │       │ work_center_         │
│──────────────────│       │ translations         │
│ id (PK)          │       │──────────────────────│
│ code (UNIQUE)    │◄──────│ work_center_id (FK)  │
│ name             │       │ locale               │
│ description      │       │ name                 │
│ active           │       │ description          │
└────────┬─────────┘       └──────────────────────┘
         │ 1:N
         │
┌────────┴──────────────┐         ┌───────────────────────────┐
│  production_orders    │         │ production_order_lines    │
│───────────────────────│         │───────────────────────────│
│ id (PK)               │ 1:N     │ id (PK)                  │
│ order_number          │────────►│ production_order_id (FK)  │
│ product_id (FK) ──────│──► prod │ material_id (FK) ─────────│──► materials
│ work_center_id (FK)   │         │ description               │
│ planned_quantity      │         │ planned_quantity           │
│ actual_quantity       │         │ unit_price                 │
│ planned_start_date    │         │ position                   │
│ planned_end_date      │         └───────────────────────────┘
│ actual_start/end_date │
│ status                │
│ priority              │
│ notes                 │
└───────────────────────┘
```

---

## Order Lifecycle — State Machine

```
    ┌───────┐
    │ DRAFT │ ◄── create()
    └───┬───┘
        │ release()
        ▼
  ┌──────────┐
  │ RELEASED │
  └─────┬────┘
        │ start()
        ▼
  ┌─────────────┐
  │ IN_PROGRESS │
  └──┬──────┬───┘
     │      │ hold()
     │      ▼
     │  ┌─────────┐
     │  │ ON_HOLD │
     │  └────┬────┘
     │       │ resume() → back to IN_PROGRESS
     │
     │ complete(actualQuantity)
     ▼
  ┌───────────┐
  │ COMPLETED │ (terminal)
  └───────────┘

  Any non-terminal → cancel(reason) → ┌───────────┐
                                       │ CANCELLED │ (terminal)
                                       └───────────┘
```

---

## Data Flow — Production Order Lifecycle

```
Planner creates Production Order
         │
         ├── Links to Product (from masterdata)
         ├── Assigns WorkCenter
         ├── Lines auto-populated from BOM
         ▼
release() → RELEASED
         │
         ├── Materials availability checked
         ▼
start() → IN_PROGRESS
         │
         ├── actual_start_date = now
         │
         ├── (optional) hold() / resume()
         ▼
complete(actualQuantity) → COMPLETED
         │
         ├── actual_end_date = now
         ├── actual_quantity recorded
         ├── Publish ProductionOrderCompletedEvent
         │       └──► (future) InventoryListener → add finished goods to stock
         └── Return ProductionOrderResponse
```

---

## API Endpoints

### Production Orders (`/api/v1/production-orders`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/production-orders` | `PRODUCTION:VIEW` | List (paginated, searchable) |
| `GET` | `/api/v1/production-orders/{id}` | `PRODUCTION:VIEW` | Get by ID (with lines) |
| `POST` | `/api/v1/production-orders` | `PRODUCTION:CREATE` | Create draft |
| `PUT` | `/api/v1/production-orders/{id}` | `PRODUCTION:EDIT` | Update draft |
| `DELETE` | `/api/v1/production-orders/{id}` | `PRODUCTION:DELETE` | Delete draft |
| `POST` | `/api/v1/production-orders/{id}/release` | `PRODUCTION:EDIT` | Release for production |
| `POST` | `/api/v1/production-orders/{id}/start` | `PRODUCTION:EDIT` | Start production |
| `POST` | `/api/v1/production-orders/{id}/complete` | `PRODUCTION:EDIT` | Complete (with actual qty) |
| `POST` | `/api/v1/production-orders/{id}/hold` | `PRODUCTION:EDIT` | Put on hold |
| `POST` | `/api/v1/production-orders/{id}/resume` | `PRODUCTION:EDIT` | Resume from hold |
| `POST` | `/api/v1/production-orders/{id}/cancel` | `PRODUCTION:EDIT` | Cancel (with reason) |

### Work Centers (`/api/v1/work-centers`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/work-centers` | `PRODUCTION:VIEW` | List (paginated, searchable) |
| `GET` | `/api/v1/work-centers/{id}` | `PRODUCTION:VIEW` | Get by ID |
| `POST` | `/api/v1/work-centers` | `PRODUCTION:CREATE` | Create work center |
| `PUT` | `/api/v1/work-centers/{id}` | `PRODUCTION:EDIT` | Update work center |
| `DELETE` | `/api/v1/work-centers/{id}` | `PRODUCTION:DELETE` | Delete work center |

---

## Domain Events

| Event | Trigger | Consumers |
|-------|---------|-----------|
| `ProductionOrderCompletedEvent` | Order completed | NotificationListener |

---

## Swiss Business Rules

- **Work center codes**: Uppercase identifiers (e.g., `WC-ASSEMBLY-01`)
- **Priority**: Integer 0 (highest) to N — used for scheduling
- **Actual vs Planned**: Variance tracking (quantity, dates)
- **Multi-language**: Work center names translatable to de, fr, it, en

---

## Module Dependencies

```
production ──depends on──► shared (BaseEntity)
production ──uses────────► masterdata (via MasterdataModuleApi — product, BOM, materials)
production ──publishes───► ProductionOrderCompletedEvent → notification module
```

---

## File Inventory

```
production/
├── ProductionModuleApi.java
├── ProductionOrderSummary.java       ← Summary projection
├── package-info.java
├── model/  (ProductionOrder, ProductionOrderLine, ProductionOrderStatus, WorkCenter, WorkCenterTranslation)
├── repository/  (ProductionOrderRepository, WorkCenterRepository)
├── service/  (ProductionOrderService, WorkCenterService)
├── dto/  (ProductionOrder + WorkCenter + Line Request/Response)
├── web/  (ProductionOrderViewController, WorkCenterViewController)
├── api/  (ProductionOrderRestController, WorkCenterRestController)
└── event/  (ProductionOrderCompletedEvent)
```
