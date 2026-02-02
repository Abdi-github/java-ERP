# 🤝 CRM Module

> Customer relationship management — contacts, interactions, and engagement tracking.

---

## Module Overview

| Property | Value |
|----------|-------|
| **Package** | `ch.swiftapp.erp.crm` |
| **Public API** | `CrmModuleApi` |
| **Entities** | Contact, Interaction |
| **Enums** | `InteractionType` (CALL, EMAIL, MEETING, NOTE, OTHER) |
| **Permissions** | `CRM:VIEW`, `CRM:CREATE`, `CRM:EDIT`, `CRM:DELETE` |
| **Web Routes** | `/app/crm/contacts` |
| **API Routes** | `/api/v1/contacts` |

---

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────────┐
│                        CRM MODULE                            │
│                                                              │
│  ┌─────────────────┐                                        │
│  │ Contact         │                                        │
│  │ ViewController  │                                        │
│  │ /app/crm/       │                                        │
│  │ contacts        │                                        │
│  └───────┬─────────┘                                        │
│          │                                                   │
│  ┌───────┴─────────┐                                        │
│  │ Contact         │                                        │
│  │ RestController  │                                        │
│  │ /api/v1/        │                                        │
│  │ contacts        │                                        │
│  │  + /{id}/       │                                        │
│  │   interactions  │                                        │
│  └───────┬─────────┘                                        │
│          │                                                   │
│  ┌───────┴──────────┐                                       │
│  │ ContactService   │                                       │
│  │ InteractionSvc   │                                       │
│  └───────┬──────────┘                                       │
│          │                                                   │
│  ┌───────┴──────────────────────────────────────────────┐   │
│  │               DATABASE                                │   │
│  │  crm_contacts                                         │   │
│  │  crm_interactions (contact_id FK)                     │   │
│  └───────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
```

---

## Entity Relationship Diagram

```
┌──────────────────┐
│  crm_contacts    │
│──────────────────│
│ id (PK)          │
│ first_name       │
│ last_name        │
│ email            │
│ phone            │
│ company          │
│ position         │
│ customer_id (FK) │──► customers (optional link to Sales)
│ notes            │
│ active           │
└────────┬─────────┘
         │ 1:N
         │
┌────────┴──────────────┐
│  crm_interactions     │
│───────────────────────│
│ id (PK)               │
│ contact_id (FK)       │
│ interaction_type      │──► CALL | EMAIL | MEETING | NOTE | OTHER
│ subject               │
│ description           │
│ interaction_date      │
│ follow_up_date        │
└───────────────────────┘
```

---

## Interaction Timeline

```
Contact: Jean-Pierre Müller
         │
         ├── 2026-01-15  📞 CALL    "Initial inquiry about Alpine collection"
         ├── 2026-01-20  📧 EMAIL   "Sent product catalog PDF"
         ├── 2026-02-01  🤝 MEETING "Showroom visit — interested in 3 models"
         ├── 2026-02-10  📝 NOTE    "Customer requested custom engraving"
         └── 2026-02-15  📧 EMAIL   "Sent quotation for 3 pieces"
                                    follow_up: 2026-02-22
```

---

## API Endpoints

### Contacts (`/api/v1/contacts`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/contacts` | `CRM:VIEW` | List (paginated, searchable) |
| `GET` | `/api/v1/contacts/{id}` | `CRM:VIEW` | Get by ID |
| `POST` | `/api/v1/contacts` | `CRM:CREATE` | Create contact |
| `PUT` | `/api/v1/contacts/{id}` | `CRM:EDIT` | Update contact |
| `DELETE` | `/api/v1/contacts/{id}` | `CRM:DELETE` | Delete contact |

### Interactions (nested under contacts)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/contacts/{id}/interactions` | `CRM:VIEW` | List interactions for contact |
| `POST` | `/api/v1/contacts/{id}/interactions` | `CRM:CREATE` | Add interaction to contact |

---

## Domain Events

| Event | Trigger | Consumers |
|-------|---------|-----------|
| `ContactCreatedEvent` | New contact added | NotificationListener |

---

## Swiss Business Rules

- **Multilingual contacts**: Swiss clients may prefer de, fr, or it communication
- **Customer link**: Contacts can optionally link to a Sales customer record
- **Follow-up tracking**: Interaction follow-up dates for sales pipeline

---

## Module Dependencies

```
crm ──depends on──► shared (BaseEntity)
crm ──may link to──► sales (customer_id references customers table)
```

---

## File Inventory

```
crm/
├── CrmModuleApi.java
├── package-info.java
├── model/  (Contact, Interaction, InteractionType)
├── repository/  (ContactRepository, InteractionRepository)
├── service/  (ContactService, InteractionService)
├── dto/  (Contact + Interaction Request/Response)
├── web/  (ContactViewController)
├── api/  (ContactRestController)
└── event/  (ContactCreatedEvent)
```

