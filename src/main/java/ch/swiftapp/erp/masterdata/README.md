# 🏭 Master Data Module

> Core product catalog, materials, categories, units of measure, and bill of materials (BOM) for Swiss watch manufacturing and retail.

---

## Module Overview

| Property | Value |
|----------|-------|
| **Package** | `ch.swiftapp.erp.masterdata` |
| **Public API** | `MasterdataModuleApi` |
| **Entities** | Product, Material, Category, UnitOfMeasure, BillOfMaterial |
| **Translation** | Product, Material, Category, UnitOfMeasure (companion tables) |
| **Permissions** | `MASTERDATA:VIEW`, `MASTERDATA:CREATE`, `MASTERDATA:EDIT`, `MASTERDATA:DELETE` |
| **Web Routes** | `/app/masterdata/products`, `/app/masterdata/materials`, `/app/masterdata/categories` |
| **API Routes** | `/api/v1/products`, `/api/v1/materials`, `/api/v1/categories`, `/api/v1/units-of-measure` |

---

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                    MASTERDATA MODULE                             │
│                                                                  │
│  ┌──────────────┐   ┌──────────────┐   ┌───────────────────┐   │
│  │ Product      │   │ Material     │   │ Category          │   │
│  │ ViewController│   │ ViewController│   │ ViewController    │   │
│  │ /app/master- │   │ /app/master- │   │ /app/master-      │   │
│  │ data/products│   │ data/materials│   │ data/categories   │   │
│  └──────┬───────┘   └──────┬───────┘   └────────┬──────────┘   │
│         │                  │                     │              │
│  ┌──────┴───────┐   ┌──────┴───────┐   ┌────────┴──────────┐   │
│  │ Product      │   │ Material     │   │ Category          │   │
│  │ RestController│   │ RestController│   │ RestController    │   │
│  │ /api/v1/     │   │ /api/v1/     │   │ /api/v1/          │   │
│  │ products     │   │ materials    │   │ categories        │   │
│  └──────┬───────┘   └──────┬───────┘   └────────┬──────────┘   │
│         │                  │                     │              │
│         └──────────────────┼─────────────────────┘              │
│                            │                                     │
│                    ┌───────┴────────┐                            │
│                    │   Services     │                            │
│                    │ ProductService │                            │
│                    │ MaterialService│                            │
│                    │ CategoryService│                            │
│                    │ BOMService     │                            │
│                    └───────┬────────┘                            │
│                            │                                     │
│                    ┌───────┴────────┐                            │
│                    │  Repositories  │                            │
│                    └───────┬────────┘                            │
│                            │                                     │
│  ┌─────────────────────────┼─────────────────────────────────┐  │
│  │                    DATABASE                               │  │
│  │  products ── product_translations                         │  │
│  │  materials ── material_translations                       │  │
│  │  categories ── category_translations                      │  │
│  │  units_of_measure ── unit_of_measure_translations         │  │
│  │  bill_of_materials (product_id → material_id)             │  │
│  └───────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────┘
```

---

## Entity Relationship Diagram

```
┌──────────────┐       ┌──────────────────┐
│  categories  │◄──┐   │ category_        │
│──────────────│   │   │ translations     │
│ id (PK)      │   │   │──────────────────│
│ name         │   │   │ category_id (FK) │
│ parent_id(FK)│───┘   │ locale           │
│ deleted_at   │◄──────│ name             │
└──────┬───────┘       └──────────────────┘
       │ 1:N
┌──────┴───────┐       ┌──────────────────┐       ┌──────────────────┐
│  products    │       │ product_         │       │ bill_of_         │
│──────────────│       │ translations     │       │ materials        │
│ id (PK)      │       │──────────────────│       │──────────────────│
│ sku (UNIQUE) │       │ product_id (FK)  │       │ product_id (FK)  │──┐
│ name         │       │ locale           │       │ material_id (FK) │──┤
│ category_id  │       │ name             │       │ quantity         │  │
│ unit_price   │       │ description      │       │ position         │  │
│ list_price   │       └──────────────────┘       └──────────────────┘  │
│ vat_rate     │◄───────────────────────────────────────────────────────┘
│ active       │
└──────────────┘   ┌──────────────┐   ┌──────────────────┐
                   │  materials   │   │ material_        │
                   │──────────────│   │ translations     │
                   │ id (PK)      │◄──│──────────────────│
                   │ sku (UNIQUE) │   │ material_id (FK) │
                   │ name         │   │ locale           │
                   │ unit_price   │   └──────────────────┘
                   │ minimum_stock│
                   └──────────────┘
```

---

## Data Flow — Product Creation

```
User fills Product Form
         │
         ▼
ProductViewController.create()
         │
         ├── @Valid ProductRequest ── validates SKU, name, price, vatRate
         ▼
ProductService.create()
         │
         ├── Check SKU uniqueness (existsBySkuIgnoreCase)
         ├── Map DTO → Entity (mapRequestToEntity)
         ├── Resolve Category by ID
         ├── Apply translations (de, fr, it, en)
         ├── Save to DB (productRepository.save)
         ├── Publish ProductCreatedEvent
         │       └──► NotificationListener → IN_APP notification
         └── Return ProductResponse → Redirect to detail page
```

---

## API Endpoints

### Products (`/api/v1/products`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/products` | `MASTERDATA:VIEW` | List (paginated, searchable) |
| `GET` | `/api/v1/products/{id}` | `MASTERDATA:VIEW` | Get by ID |
| `POST` | `/api/v1/products` | `MASTERDATA:CREATE` | Create |
| `PUT` | `/api/v1/products/{id}` | `MASTERDATA:EDIT` | Update |
| `DELETE` | `/api/v1/products/{id}` | `MASTERDATA:DELETE` | Soft-delete |
| `GET` | `/api/v1/products/{id}/bom` | `MASTERDATA:VIEW` | Get BOM lines |
| `POST` | `/api/v1/products/{id}/bom` | `MASTERDATA:CREATE` | Add BOM line |
| `PUT` | `/api/v1/products/{id}/bom/{lineId}` | `MASTERDATA:EDIT` | Update BOM line |
| `DELETE` | `/api/v1/products/{id}/bom/{lineId}` | `MASTERDATA:DELETE` | Delete BOM line |

### Materials (`/api/v1/materials`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/materials` | `MASTERDATA:VIEW` | List (paginated, searchable) |
| `GET` | `/api/v1/materials/{id}` | `MASTERDATA:VIEW` | Get by ID |
| `POST` | `/api/v1/materials` | `MASTERDATA:CREATE` | Create |
| `PUT` | `/api/v1/materials/{id}` | `MASTERDATA:EDIT` | Update |
| `DELETE` | `/api/v1/materials/{id}` | `MASTERDATA:DELETE` | Soft-delete |

### Categories (`/api/v1/categories`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/categories` | `MASTERDATA:VIEW` | List (paginated) |
| `GET` | `/api/v1/categories/roots` | `MASTERDATA:VIEW` | Root categories |
| `GET` | `/api/v1/categories/flat` | `MASTERDATA:VIEW` | Flat (dropdowns) |
| `GET` | `/api/v1/categories/{id}` | `MASTERDATA:VIEW` | Get by ID |
| `POST` | `/api/v1/categories` | `MASTERDATA:CREATE` | Create |
| `PUT` | `/api/v1/categories/{id}` | `MASTERDATA:EDIT` | Update |
| `DELETE` | `/api/v1/categories/{id}` | `MASTERDATA:DELETE` | Soft-delete |

### Units of Measure (`/api/v1/units-of-measure`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/units-of-measure` | `MASTERDATA:VIEW` | List all |
| `GET` | `/api/v1/units-of-measure/{id}` | `MASTERDATA:VIEW` | Get by ID |
| `POST` | `/api/v1/units-of-measure` | `MASTERDATA:CREATE` | Create |
| `PUT` | `/api/v1/units-of-measure/{id}` | `MASTERDATA:EDIT` | Update |
| `DELETE` | `/api/v1/units-of-measure/{id}` | `MASTERDATA:DELETE` | Soft-delete |

---

## Domain Events

| Event | Trigger | Payload |
|-------|---------|---------|
| `ProductCreatedEvent` | New product saved | `productId`, `sku`, `name` |
| `ProductUpdatedEvent` | Product updated | `productId`, `sku`, `name` |
| `ProductDeletedEvent` | Product soft-deleted | `productId`, `sku` |

---

## Swiss Business Rules

- **SKU format**: Uppercase + numbers (e.g., `WCH-ALPINE-001`)
- **Prices**: `BigDecimal` scale 4 (e.g., `4'500.0000 CHF`)
- **VAT rates**: `STANDARD_8_1`, `REDUCED_2_6`, `ACCOMMODATION_3_8`, `EXEMPT_0`
- **Multi-language**: Names/descriptions translatable to de, fr, it, en
- **Soft delete**: `deletedAt` set, row never physically removed

---

## Module Dependencies

```
masterdata ──depends on──► shared (BaseEntity, VatRate, TranslationResolver)
sales      ──uses──────────► masterdata (via MasterdataModuleApi)
production ──uses──────────► masterdata (via MasterdataModuleApi)
inventory  ──uses──────────► masterdata (via MasterdataModuleApi)
```

---

## File Inventory

```
masterdata/
├── MasterdataModuleApi.java          ← Public API (5 methods)
├── MasterdataModuleApiFacade.java    ← API implementation
├── package-info.java                 ← @NamedInterface
├── model/  (Product, Material, Category, UnitOfMeasure, BillOfMaterial + translations)
├── repository/  (5 Spring Data repositories)
├── service/  (ProductService, MaterialService, CategoryService, UnitOfMeasureService, BOMService)
├── dto/  (5 Request/Response record pairs)
├── web/  (ProductViewController, MaterialViewController, CategoryViewController)
├── api/  (ProductRestController, MaterialRestController, CategoryRestController, UnitOfMeasureRestController)
└── event/  (ProductCreatedEvent, ProductUpdatedEvent, ProductDeletedEvent)
```
