# 📦 Inventory Module

> Warehouse management, stock levels, and stock movements for Swiss watch manufacturing.

---

## Module Overview

| Property | Value |
|----------|-------|
| **Package** | `ch.swiftapp.erp.inventory` |
| **Public API** | `InventoryModuleApi` |
| **Entities** | Warehouse, StockLevel, StockMovement |
| **Enums** | `MovementType` (INBOUND, OUTBOUND, TRANSFER, ADJUSTMENT), `StockItemType` (PRODUCT, MATERIAL) |
| **Translation** | Warehouse (companion table) |
| **Permissions** | `INVENTORY:VIEW`, `INVENTORY:CREATE`, `INVENTORY:EDIT`, `INVENTORY:DELETE` |
| **Web Routes** | `/app/inventory/warehouses`, `/app/inventory/stock` |
| **API Routes** | `/api/v1/warehouses`, `/api/v1/stock` |

---

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────────┐
│                    INVENTORY MODULE                           │
│                                                              │
│  ┌─────────────────┐          ┌──────────────────┐          │
│  │ Warehouse       │          │ Stock            │          │
│  │ ViewController  │          │ ViewController   │          │
│  │ /app/inventory/ │          │ /app/inventory/  │          │
│  │ warehouses      │          │ stock            │          │
│  └───────┬─────────┘          └───────┬──────────┘          │
│          │                            │                      │
│  ┌───────┴─────────┐          ┌───────┴──────────┐          │
│  │ Warehouse       │          │ Stock            │          │
│  │ RestController  │          │ RestController   │          │
│  │ /api/v1/        │          │ /api/v1/         │          │
│  │ warehouses      │          │ stock            │          │
│  └───────┬─────────┘          └───────┬──────────┘          │
│          └────────────┬───────────────┘                      │
│                       │                                      │
│              ┌────────┴──────────┐                           │
│              │ WarehouseService  │                           │
│              │ StockService      │                           │
│              └────────┬──────────┘                           │
│                       │                                      │
│  ┌────────────────────┼──────────────────────────────────┐   │
│  │               DATABASE                                │   │
│  │  warehouses ── warehouse_translations                  │   │
│  │  stock_levels (warehouse_id, item_id, item_type)       │   │
│  │  stock_movements (source/target warehouse, item)       │   │
│  └───────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
```

---

## Entity Relationship Diagram

```
┌──────────────────┐       ┌──────────────────────┐
│  warehouses      │       │ warehouse_           │
│──────────────────│       │ translations         │
│ id (PK)          │       │──────────────────────│
│ code (UNIQUE)    │◄──────│ warehouse_id (FK)    │
│ name             │       │ locale               │
│ description      │       │ name                 │
│ address          │       │ description          │
│ active           │       └──────────────────────┘
└────────┬─────────┘
         │ 1:N
         │
┌────────┴──────────┐         ┌────────────────────────┐
│  stock_levels     │         │  stock_movements       │
│───────────────────│         │────────────────────────│
│ id (PK)           │         │ id (PK)                │
│ warehouse_id (FK) │         │ movement_type          │
│ item_id           │         │ item_id                │
│ item_type         │         │ item_type              │
│ quantity          │         │ source_warehouse_id(FK)│
│ reserved_quantity │         │ target_warehouse_id(FK)│
│                   │         │ quantity               │
│                   │         │ reason                 │
└───────────────────┘         └────────────────────────┘
```

---

## Movement Types

```
INBOUND    → Goods received (from purchase order)
             ┌──────────┐
  External──►│ Warehouse│  quantity += N
             └──────────┘

OUTBOUND   → Goods shipped (for sales order)
             ┌──────────┐
             │ Warehouse│──►External   quantity -= N
             └──────────┘

TRANSFER   → Inter-warehouse transfer
             ┌──────────┐     ┌──────────┐
             │ Source WH │──►  │ Target WH│
             └──────────┘     └──────────┘

ADJUSTMENT → Manual stock correction
             ┌──────────┐
             │ Warehouse│  quantity = new value
             └──────────┘
```

---

## Data Flow — Stock Movement

```
User creates stock movement
         │
         ▼
StockService.createMovement(StockMovementRequest)
         │
         ├── Validate movement type
         ├── Validate warehouse(s) exist and are active
         ├── switch(movementType)
         │     ├── INBOUND:  increase target stock level
         │     ├── OUTBOUND: decrease source stock level (check >= 0)
         │     ├── TRANSFER: decrease source + increase target
         │     └── ADJUSTMENT: set stock level directly
         │
         ├── Save StockMovement (audit trail)
         ├── Update StockLevel records
         └── Return StockMovementResponse
```

---

## API Endpoints

### Stock (`/api/v1/stock`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/stock/levels/{itemId}` | `INVENTORY:VIEW` | Stock levels per warehouse |
| `GET` | `/api/v1/stock/levels/warehouse/{warehouseId}` | `INVENTORY:VIEW` | All items in warehouse |
| `GET` | `/api/v1/stock/levels/{itemId}/total` | `INVENTORY:VIEW` | Total stock across all WH |
| `GET` | `/api/v1/stock/levels/{itemId}/available` | `INVENTORY:VIEW` | Available (total - reserved) |
| `GET` | `/api/v1/stock/movements` | `INVENTORY:VIEW` | Movement history (paginated) |
| `GET` | `/api/v1/stock/movements/item/{itemId}` | `INVENTORY:VIEW` | Movements for specific item |
| `POST` | `/api/v1/stock/movements` | `INVENTORY:CREATE` | Create stock movement |

### Warehouses (`/api/v1/warehouses`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/warehouses` | `INVENTORY:VIEW` | List all warehouses |
| `GET` | `/api/v1/warehouses/active` | `INVENTORY:VIEW` | Active warehouses only |
| `GET` | `/api/v1/warehouses/{id}` | `INVENTORY:VIEW` | Get by ID |
| `POST` | `/api/v1/warehouses` | `INVENTORY:CREATE` | Create warehouse |
| `PUT` | `/api/v1/warehouses/{id}` | `INVENTORY:EDIT` | Update warehouse |
| `DELETE` | `/api/v1/warehouses/{id}` | `INVENTORY:DELETE` | Delete warehouse |

---

## Domain Events

| Event | Trigger | Consumers |
|-------|---------|-----------|
| `StockMovementCreatedEvent` | Movement recorded | NotificationListener |

---

## Swiss Business Rules

- **Warehouse codes**: Short uppercase (e.g., `WH-ZURICH-01`, `WH-GENEVA-02`)
- **Quantities**: `BigDecimal` — precision for fractional materials (gold weight, etc.)
- **Dual item types**: Stock can be tracked for both PRODUCTs (finished watches) and MATERIALs (components)
- **Multi-language**: Warehouse names/descriptions translatable

---

## Module Dependencies

```
inventory ──depends on──► shared (BaseEntity)
inventory ──uses────────► masterdata (via MasterdataModuleApi — item details)
```

---

## File Inventory

```
inventory/
├── InventoryModuleApi.java
├── package-info.java
├── model/  (Warehouse, WarehouseTranslation, StockLevel, StockMovement, MovementType, StockItemType)
├── repository/  (WarehouseRepository, StockLevelRepository, StockMovementRepository)
├── service/  (WarehouseService, StockService)
├── dto/  (Warehouse + StockLevel + StockMovement Request/Response)
├── web/  (WarehouseViewController, StockViewController)
├── api/  (WarehouseRestController, StockRestController)
└── event/  (StockMovementCreatedEvent)
```
