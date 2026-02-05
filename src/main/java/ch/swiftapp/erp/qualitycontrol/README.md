# 🔍 Quality Control Module

> Quality checks, inspection plans, and non-conformance reports (NCRs) for Swiss watch manufacturing.

---

## Module Overview

| Property | Value |
|----------|-------|
| **Package** | `ch.swiftapp.erp.qualitycontrol` |
| **Public API** | `QualityControlModuleApi` |
| **Entities** | InspectionPlan, QualityCheck, NonConformanceReport |
| **Enums** | `CheckResult` (PASS, FAIL, CONDITIONAL), `NcrSeverity` (LOW, MEDIUM, HIGH, CRITICAL), `NcrStatus` (OPEN, CLOSED) |
| **Permissions** | `QUALITY_CONTROL:VIEW`, `QUALITY_CONTROL:CREATE`, `QUALITY_CONTROL:EDIT`, `QUALITY_CONTROL:DELETE` |
| **Web Routes** | `/app/quality-control/inspections`, `/app/quality-control/checks`, `/app/quality-control/ncrs` |
| **API Routes** | `/api/v1/inspection-plans`, `/api/v1/quality-checks`, `/api/v1/ncrs` |

---

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                   QUALITY CONTROL MODULE                         │
│                                                                  │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │ InspectionPlan  │  │ QualityCheck    │  │ NCR             │ │
│  │ ViewController  │  │ ViewController  │  │ ViewController  │ │
│  └───────┬─────────┘  └───────┬─────────┘  └───────┬─────────┘ │
│          │                    │                     │            │
│  ┌───────┴─────────┐  ┌──────┴──────────┐  ┌───────┴─────────┐ │
│  │ InspectionPlan  │  │ QualityCheck    │  │ NCR             │ │
│  │ RestController  │  │ RestController  │  │ RestController  │ │
│  │ /api/v1/        │  │ /api/v1/        │  │ /api/v1/ncrs   │ │
│  │ inspection-plans│  │ quality-checks  │  │                 │ │
│  └───────┬─────────┘  └───────┬─────────┘  └───────┬─────────┘ │
│          └─────────────────────┼─────────────────────┘           │
│                                │                                  │
│              ┌─────────────────┴──────────────────┐              │
│              │ InspectionPlanService               │              │
│              │ QualityCheckService                  │              │
│              │ NcrService                           │              │
│              └─────────────────┬──────────────────┘              │
│                                │                                  │
│  ┌─────────────────────────────┼──────────────────────────────┐  │
│  │                        DATABASE                            │  │
│  │  qc_inspection_plans (product_id / material_id FK)         │  │
│  │  qc_quality_checks (inspection_plan_id, production_order_id)│  │
│  │  qc_non_conformance_reports (quality_check_id FK)          │  │
│  └────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────┘
```

---

## Entity Relationship Diagram

```
┌──────────────────────┐
│ qc_inspection_plans  │
│──────────────────────│
│ id (PK)              │
│ plan_number (UNIQUE) │
│ name                 │
│ description          │
│ product_id (FK) ─────│──► products (optional)
│ material_id (FK) ────│──► materials (optional)
│ active               │
└────────┬─────────────┘
         │ 1:N
         │
┌────────┴──────────────┐
│  qc_quality_checks    │
│───────────────────────│
│ id (PK)               │
│ inspection_plan_id(FK)│
│ production_order_id   │──► production_orders (optional)
│ checked_by            │
│ check_date            │
│ result ───────────────│──► PASS | FAIL | CONDITIONAL
│ notes                 │
└────────┬──────────────┘
         │ 1:N (only on FAIL/CONDITIONAL)
         │
┌────────┴─────────────────────┐
│ qc_non_conformance_reports   │
│──────────────────────────────│
│ id (PK)                      │
│ quality_check_id (FK)        │
│ severity ────────────────────│──► LOW | MEDIUM | HIGH | CRITICAL
│ description                  │
│ corrective_action            │
│ status ──────────────────────│──► OPEN | CLOSED
└──────────────────────────────┘
```

---

## Quality Check Workflow

```
Define Inspection Plan (per product/material)
         │
         ▼
Perform Quality Check (on production batch)
         │
         ├── result = PASS    → ✅ Record and close
         ├── result = CONDITIONAL → ⚠️ Record + optional NCR
         └── result = FAIL    → ❌ Create NCR (mandatory)
                                    │
                                    ▼
                              NCR Lifecycle
                                    │
                              ┌─────┴─────┐
                              │   OPEN    │
                              └─────┬─────┘
                                    │ corrective action applied
                                    │ close()
                                    ▼
                              ┌───────────┐
                              │  CLOSED   │
                              └───────────┘
```

---

## Severity Matrix

```
┌──────────┬────────────────────────────────────────────────────┐
│ Severity │ Description                                        │
├──────────┼────────────────────────────────────────────────────┤
│ LOW      │ Minor cosmetic issue — no functional impact        │
│ MEDIUM   │ Moderate defect — needs rework before shipping     │
│ HIGH     │ Significant defect — affects watch performance     │
│ CRITICAL │ Safety/regulatory risk — immediate quarantine      │
└──────────┴────────────────────────────────────────────────────┘
```

---

## API Endpoints

### Inspection Plans (`/api/v1/inspection-plans`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/inspection-plans` | `QUALITY_CONTROL:VIEW` | List (paginated, searchable) |
| `GET` | `/api/v1/inspection-plans/{id}` | `QUALITY_CONTROL:VIEW` | Get by ID |
| `POST` | `/api/v1/inspection-plans` | `QUALITY_CONTROL:CREATE` | Create plan |
| `PUT` | `/api/v1/inspection-plans/{id}` | `QUALITY_CONTROL:EDIT` | Update plan |
| `DELETE` | `/api/v1/inspection-plans/{id}` | `QUALITY_CONTROL:DELETE` | Delete plan |

### Quality Checks (`/api/v1/quality-checks`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/quality-checks` | `QUALITY_CONTROL:VIEW` | List (paginated) |
| `GET` | `/api/v1/quality-checks/{id}` | `QUALITY_CONTROL:VIEW` | Get by ID |
| `POST` | `/api/v1/quality-checks` | `QUALITY_CONTROL:CREATE` | Record quality check |

### NCRs (`/api/v1/ncrs`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/ncrs` | `QUALITY_CONTROL:VIEW` | List (paginated, filter by status) |
| `GET` | `/api/v1/ncrs/{id}` | `QUALITY_CONTROL:VIEW` | Get by ID |
| `POST` | `/api/v1/ncrs` | `QUALITY_CONTROL:CREATE` | Create NCR |
| `POST` | `/api/v1/ncrs/{id}/close` | `QUALITY_CONTROL:EDIT` | Close NCR |

---

## Swiss Business Rules

- **Swiss Made certification**: Quality checks enforce "Swiss Made" label requirements
- **Traceability**: Every check links to inspection plan + production order
- **Mandatory NCR on FAIL**: Failed checks must have an NCR for audit compliance
- **CRITICAL severity**: Triggers immediate quarantine notification

---

## Module Dependencies

```
qualitycontrol ──depends on──► shared (BaseEntity)
qualitycontrol ──uses────────► masterdata (product/material references)
qualitycontrol ──uses────────► production (production order references)
qualitycontrol ──publishes───► QualityCheckFailedEvent → notification module
```

---

## File Inventory

```
qualitycontrol/
├── QualityControlModuleApi.java
├── package-info.java
├── model/  (InspectionPlan, QualityCheck, NonConformanceReport, CheckResult, NcrSeverity, NcrStatus)
├── repository/  (InspectionPlanRepository, QualityCheckRepository, NcrRepository)
├── service/  (InspectionPlanService, QualityCheckService, NcrService)
├── dto/  (InspectionPlan + QualityCheck + NCR Request/Response)
├── web/  (InspectionPlanViewController, QualityCheckViewController, NcrViewController)
├── api/  (InspectionPlanRestController, QualityCheckRestController, NcrRestController)
└── event/  (QualityCheckFailedEvent)
```

