# 👥 HR Module

> Employee management, department structure, and workforce tracking for Swiss watch ERP.

---

## Module Overview

| Property | Value |
|----------|-------|
| **Package** | `ch.swiftapp.erp.hr` |
| **Public API** | `HrModuleApi` |
| **Entities** | Employee, Department |
| **Translation** | Department (companion table) |
| **Permissions** | `HR:VIEW`, `HR:CREATE`, `HR:EDIT`, `HR:DELETE` |
| **Web Routes** | `/app/hr/employees`, `/app/hr/departments` |
| **API Routes** | `/api/v1/employees`, `/api/v1/departments` |

---

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────────┐
│                         HR MODULE                            │
│                                                              │
│  ┌─────────────────┐          ┌─────────────────┐           │
│  │ Employee        │          │ Department      │           │
│  │ ViewController  │          │ ViewController  │           │
│  │ /app/hr/        │          │ /app/hr/        │           │
│  │ employees       │          │ departments     │           │
│  └───────┬─────────┘          └───────┬─────────┘           │
│          │                            │                      │
│  ┌───────┴─────────┐          ┌───────┴─────────┐           │
│  │ Employee        │          │ Department      │           │
│  │ RestController  │          │ RestController  │           │
│  │ /api/v1/        │          │ /api/v1/        │           │
│  │ employees       │          │ departments     │           │
│  └───────┬─────────┘          └───────┬─────────┘           │
│          └────────────┬───────────────┘                      │
│                       │                                      │
│              ┌────────┴──────────┐                           │
│              │ EmployeeService   │                           │
│              │ DepartmentService │                           │
│              └────────┬──────────┘                           │
│                       │                                      │
│  ┌────────────────────┼──────────────────────────────────┐   │
│  │               DATABASE                                │   │
│  │  employees (department_id FK)                          │   │
│  │  departments ── department_translations                │   │
│  └───────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
```

---

## Entity Relationship Diagram

```
┌──────────────────┐       ┌──────────────────────┐
│  departments     │       │ department_          │
│──────────────────│       │ translations         │
│ id (PK)          │       │──────────────────────│
│ name             │◄──────│ department_id (FK)   │
│ description      │       │ locale               │
│ manager_id (FK)──│──┐    │ name                 │
│ active           │  │    │ description          │
└────────┬─────────┘  │    └──────────────────────┘
         │ 1:N        │
         │            │
┌────────┴──────────┐ │
│  employees        │ │
│───────────────────│ │
│ id (PK)           │◄┘  (manager is an employee)
│ employee_number   │
│ first_name        │
│ last_name         │
│ email             │
│ phone             │
│ hire_date         │
│ termination_date  │
│ department_id(FK) │
│ position          │
│ salary            │  ← BigDecimal (CHF)
│ active            │
└───────────────────┘
```

---

## Employee Lifecycle

```
    ┌─────────┐
    │  HIRED  │ ◄── create(hireDate)
    └────┬────┘
         │
         │ (active employment)
         │
    ┌────┴────┐
    │ ACTIVE  │ ── update position, salary, department
    └────┬────┘
         │ terminate(terminationDate)
         ▼
  ┌────────────┐
  │ TERMINATED │  (terminationDate set, active = false)
  └────────────┘
```

---

## API Endpoints

### Employees (`/api/v1/employees`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/employees` | `HR:VIEW` | List (paginated, searchable) |
| `GET` | `/api/v1/employees/{id}` | `HR:VIEW` | Get by ID |
| `POST` | `/api/v1/employees` | `HR:CREATE` | Create employee |
| `PUT` | `/api/v1/employees/{id}` | `HR:EDIT` | Update employee |
| `POST` | `/api/v1/employees/{id}/terminate` | `HR:EDIT` | Terminate employee |
| `DELETE` | `/api/v1/employees/{id}` | `HR:DELETE` | Delete employee |

### Departments (`/api/v1/departments`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/departments` | `HR:VIEW` | List (paginated) |
| `GET` | `/api/v1/departments/{id}` | `HR:VIEW` | Get by ID |
| `POST` | `/api/v1/departments` | `HR:CREATE` | Create department |
| `PUT` | `/api/v1/departments/{id}` | `HR:EDIT` | Update department |
| `DELETE` | `/api/v1/departments/{id}` | `HR:DELETE` | Delete department |

---

## Domain Events

| Event | Trigger | Consumers |
|-------|---------|-----------|
| `EmployeeCreatedEvent` | New employee hired | NotificationListener |
| `EmployeeTerminatedEvent` | Employee terminated | NotificationListener |

---

## Swiss Business Rules

- **Employee number**: Sequential (e.g., `EMP-000001`)
- **Salary**: `BigDecimal` in CHF — confidential, restricted by HR permissions
- **Hire/termination dates**: Swiss labor law compliance
- **Department translations**: de, fr, it, en for multilingual Swiss workforce

---

## Module Dependencies

```
hr ──depends on──► shared (BaseEntity)
hr ──publishes───► EmployeeCreatedEvent → notification module
```

---

## File Inventory

```
hr/
├── HrModuleApi.java
├── package-info.java
├── model/  (Employee, Department, DepartmentTranslation)
├── repository/  (EmployeeRepository, DepartmentRepository)
├── service/  (EmployeeService, DepartmentService)
├── dto/  (Employee + Department Request/Response)
├── web/  (EmployeeViewController, DepartmentViewController)
├── api/  (EmployeeRestController, DepartmentRestController)
└── event/  (EmployeeCreatedEvent, EmployeeTerminatedEvent)
```

