# 🛒 Purchasing Module

> Supplier management, purchase orders, and procurement lifecycle for Swiss watch manufacturing.

---

## Module Overview

| Property | Value |
|----------|-------|
| **Package** | `ch.swiftapp.erp.purchasing` |
| **Public API** | `PurchasingModuleApi` |
| **Entities** | Supplier, PurchaseOrder, PurchaseOrderLine |
| **Enums** | `PurchaseOrderStatus` (DRAFT → SUBMITTED → CONFIRMED → RECEIVED → COMPLETED → CANCELLED) |
| **Permissions** | `PURCHASING:VIEW`, `PURCHASING:CREATE`, `PURCHASING:EDIT`, `PURCHASING:DELETE` |
| **Web Routes** | `/app/purchasing/orders`, `/app/purchasing/suppliers` |
| **API Routes** | `/api/v1/purchase-orders`, `/api/v1/suppliers` |

---

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────────┐
│                    PURCHASING MODULE                          │
│                                                              │
│  ┌─────────────────┐          ┌─────────────────┐           │
│  │ Supplier        │          │ PurchaseOrder   │           │
│  │ ViewController  │          │ ViewController  │           │
│  │ /app/purchasing/│          │ /app/purchasing/│           │
│  │ suppliers       │          │ orders          │           │
│  └───────┬─────────┘          └───────┬─────────┘           │
│          │                            │                      │
│  ┌───────┴─────────┐          ┌───────┴─────────┐           │
│  │ Supplier        │          │ PurchaseOrder   │           │
│  │ RestController  │          │ RestController  │           │
│  │ /api/v1/        │          │ /api/v1/        │           │
│  │ suppliers       │          │ purchase-orders │           │
│  └───────┬─────────┘          └───────┬─────────┘           │
│          └────────────┬───────────────┘                      │
│                       │                                      │
│              ┌────────┴──────────┐                           │
│              │ SupplierService   │                           │
│              │ PurchaseOrderSvc  │                           │
│              └────────┬──────────┘                           │
│                       │                                      │
│              ┌────────┴──────────┐                           │
│              │   Repositories    │                           │
│              └────────┬──────────┘                           │
│                       │                                      │
│  ┌────────────────────┼──────────────────────────────────┐   │
│  │               DATABASE                                │   │
│  │  suppliers                                            │   │
│  │  purchase_orders ── purchase_order_lines               │   │
│  └───────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
```

---

## Entity Relationship Diagram

```
┌──────────────────┐
│   suppliers      │
│──────────────────│
│ id (PK)          │
│ supplier_number  │
│ company_name     │
│ contact_person   │
│ email, phone     │
│ street, city     │
│ postal_code      │
│ country          │
│ website          │
│ notes            │
│ active           │
└────────┬─────────┘
         │ 1:N
         │
┌────────┴──────────┐         ┌───────────────────────┐
│ purchase_orders   │         │ purchase_order_lines  │
│───────────────────│         │───────────────────────│
│ id (PK)           │ 1:N     │ id (PK)              │
│ order_number      │────────►│ purchase_order_id(FK)│
│ supplier_id (FK)  │         │ material_id (FK) ────│──► materials
│ order_date        │         │ description          │
│ expected_delivery │         │ quantity             │
│ status            │         │ unit_price           │
│ notes             │         │ discount_pct         │
│ total_amount      │         │ vat_rate             │
│ vat_amount        │         │ line_total           │
└───────────────────┘         │ position             │
                              └───────────────────────┘
```

---

## Order Lifecycle — State Machine

```
    ┌───────┐
    │ DRAFT │ ◄── create()
    └───┬───┘
        │ submit()
        ▼
  ┌───────────┐
  │ SUBMITTED │
  └─────┬─────┘
        │ confirm()
        ▼
  ┌───────────┐
  │ CONFIRMED │
  └─────┬─────┘
        │ receive()
        ▼
  ┌──────────┐
  │ RECEIVED │
  └─────┬────┘
        │ complete()
        ▼
  ┌───────────┐
  │ COMPLETED │ (terminal)
  └───────────┘

  Any non-terminal → cancel(reason) → ┌───────────┐
                                       │ CANCELLED │ (terminal)
                                       └───────────┘
```

---

## Data Flow — Purchase Order Receipt

```
Warehouse staff clicks "Receive"
         │
         ▼
PurchaseOrderService.receive(orderId)
         │
         ├── Load PO (must be CONFIRMED)
         ├── Set status = RECEIVED
         ├── Save to DB
         ├── Publish PurchaseOrderReceivedEvent
         │       │
         │       └──► (future) InventoryListener → create inbound stock movements
         └── Return updated PurchaseOrderResponse
```

---

## API Endpoints

### Purchase Orders (`/api/v1/purchase-orders`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/purchase-orders` | `PURCHASING:VIEW` | List (paginated, searchable) |
| `GET` | `/api/v1/purchase-orders/{id}` | `PURCHASING:VIEW` | Get by ID (with lines) |
| `POST` | `/api/v1/purchase-orders` | `PURCHASING:CREATE` | Create draft PO |
| `PUT` | `/api/v1/purchase-orders/{id}` | `PURCHASING:EDIT` | Update draft PO |
| `DELETE` | `/api/v1/purchase-orders/{id}` | `PURCHASING:DELETE` | Delete draft PO |
| `POST` | `/api/v1/purchase-orders/{id}/submit` | `PURCHASING:EDIT` | Submit PO for approval |
| `POST` | `/api/v1/purchase-orders/{id}/confirm` | `PURCHASING:EDIT` | Confirm PO |
| `POST` | `/api/v1/purchase-orders/{id}/receive` | `PURCHASING:EDIT` | Mark as received |
| `POST` | `/api/v1/purchase-orders/{id}/complete` | `PURCHASING:EDIT` | Complete PO |
| `POST` | `/api/v1/purchase-orders/{id}/cancel` | `PURCHASING:EDIT` | Cancel PO (with reason) |

### Suppliers (`/api/v1/suppliers`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/suppliers` | `PURCHASING:VIEW` | List (paginated, searchable) |
| `GET` | `/api/v1/suppliers/{id}` | `PURCHASING:VIEW` | Get by ID |
| `POST` | `/api/v1/suppliers` | `PURCHASING:CREATE` | Create supplier |
| `PUT` | `/api/v1/suppliers/{id}` | `PURCHASING:EDIT` | Update supplier |
| `DELETE` | `/api/v1/suppliers/{id}` | `PURCHASING:DELETE` | Delete supplier |

---

## Domain Events

| Event | Trigger | Consumers |
|-------|---------|-----------|
| `PurchaseOrderReceivedEvent` | PO marked received | NotificationListener, (future) InventoryListener |

---

## Swiss Business Rules

- **Supplier number**: Auto-generated (e.g., `SUP-000012`)
- **VAT**: Per-line VAT at Swiss rates — import duties handled separately
- **Currency**: CHF (all amounts `BigDecimal` scale 4)
- **Expected delivery**: Standard lead times tracked per supplier

---

## Module Dependencies

```
purchasing ──depends on──► shared (BaseEntity, VatRate)
purchasing ──uses────────► masterdata (via MasterdataModuleApi — material lookup)
purchasing ──publishes───► PurchaseOrderReceivedEvent → notification module
```

---

## File Inventory

```
purchasing/
├── PurchasingModuleApi.java
├── package-info.java
├── model/  (Supplier, PurchaseOrder, PurchaseOrderLine, PurchaseOrderStatus)
├── repository/  (SupplierRepository, PurchaseOrderRepository)
├── service/  (SupplierService, PurchaseOrderService)
├── dto/  (Supplier + PurchaseOrder + PurchaseOrderLine Request/Response)
├── web/  (SupplierViewController, PurchaseOrderViewController)
├── api/  (SupplierRestController, PurchaseOrderRestController)
└── event/  (PurchaseOrderReceivedEvent)
```
