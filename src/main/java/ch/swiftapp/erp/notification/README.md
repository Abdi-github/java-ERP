# 🔔 Notification Module

> In-app notifications, email dispatch, mail campaigns, and scheduled retry for Swiss watch ERP.

---

## Module Overview

| Property | Value |
|----------|-------|
| **Package** | `ch.swiftapp.erp.notification` |
| **Public API** | `NotificationModuleApi` |
| **Entities** | Notification, NotificationTemplate, MailCampaign |
| **Enums** | `NotificationStatus` (PENDING, SENT, FAILED, READ, DISMISSED), `NotificationChannel` (IN_APP, EMAIL), `MailCampaignStatus` |
| **Permissions** | `NOTIFICATION:VIEW`, `NOTIFICATION:CREATE`, `NOTIFICATION:EDIT`, `NOTIFICATION:DELETE` |
| **Web Routes** | `/app/notifications`, `/app/notifications/campaigns` |
| **API Routes** | `/api/v1/notifications`, `/api/v1/mail-campaigns` |

---

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                   NOTIFICATION MODULE                            │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                   Event Listeners                        │   │
│  │  @TransactionalEventListener(AFTER_COMMIT)               │   │
│  │  @Async("notificationExecutor")                          │   │
│  │                                                          │   │
│  │  Listens to events from ALL modules:                     │   │
│  │  • ProductCreatedEvent → "New product added"             │   │
│  │  • SalesOrderConfirmedEvent → "Order confirmed"          │   │
│  │  • PurchaseOrderReceivedEvent → "PO received"            │   │
│  │  • UserCreatedEvent → "Welcome notification"             │   │
│  │  • ProductionOrderCompletedEvent → "Production done"     │   │
│  └────────────────────────┬─────────────────────────────────┘   │
│                           │                                      │
│  ┌────────────────────────┴────────────────────────────┐        │
│  │              NotificationService                     │        │
│  │  dispatch(userId, channel, title, message)           │        │
│  │                                                      │        │
│  │  ┌──────────┐     ┌──────────┐                      │        │
│  │  │ IN_APP   │     │  EMAIL   │                      │        │
│  │  │ Save to  │     │ MailSvc  │                      │        │
│  │  │ DB as    │     │ → SMTP   │                      │        │
│  │  │ SENT     │     │ (Mailpit)│                      │        │
│  │  └──────────┘     └──────────┘                      │        │
│  └─────────────────────────────────────────────────────┘        │
│                                                                  │
│  ┌──────────────────────────────────────────────────────┐       │
│  │            NotificationScheduler                      │       │
│  │  @Scheduled(fixedDelay = 5 min)                       │       │
│  │  retryFailed() → re-send FAILED notifications         │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  ┌──────────────────┐          ┌──────────────────┐             │
│  │ Notification     │          │ MailCampaign     │             │
│  │ RestController   │          │ RestController   │             │
│  │ /api/v1/         │          │ /api/v1/         │             │
│  │ notifications    │          │ mail-campaigns   │             │
│  └──────────────────┘          └──────────────────┘             │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                    DATABASE                               │   │
│  │  notifications (recipient_user_id → users.id)             │   │
│  │  notification_templates (code, locale, subject, body)     │   │
│  │  notification_campaigns (status, scheduled_at)            │   │
│  └──────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────┘
```

---

## Entity Relationship Diagram

```
┌─────────────────────────┐
│  notifications          │
│─────────────────────────│
│ id (PK)                 │
│ recipient_user_id (FK)──│──► users
│ channel (IN_APP/EMAIL)  │
│ title                   │
│ message                 │
│ status (PENDING/SENT/   │
│         FAILED/READ/    │
│         DISMISSED)      │
│ read_at                 │
│ sent_at                 │
└─────────────────────────┘

┌─────────────────────────┐       ┌─────────────────────────┐
│ notification_templates  │       │ notification_campaigns  │
│─────────────────────────│       │─────────────────────────│
│ id (PK)                 │       │ id (PK)                 │
│ code (UNIQUE)           │       │ name                    │
│ locale                  │       │ description             │
│ subject                 │       │ template_code           │
│ body (TEXT)             │       │ locale                  │
│ channel                 │       │ target_segment          │
└─────────────────────────┘       │ scheduled_at            │
                                  │ status                  │
                                  │ subject_override        │
                                  └─────────────────────────┘
```

---

## Notification Flow

```
Domain Event Published (e.g., SalesOrderConfirmedEvent)
         │
         │ @TransactionalEventListener(phase = AFTER_COMMIT)
         │ @Async("notificationExecutor")
         ▼
SalesOrderEventListener.onOrderConfirmed(event)
         │
         ▼
NotificationService.dispatch(userId, IN_APP, title, message)
         │
         ├── Create Notification entity (status = PENDING)
         ├── switch(channel)
         │     ├── IN_APP: status = SENT immediately
         │     └── EMAIL:  MailService.send()
         │                  ├── ✅ status = SENT
         │                  └── ❌ status = FAILED (retry later)
         └── Save to DB
```

---

## Campaign Flow

```
Admin creates MailCampaign
         │
         ▼
POST /api/v1/mail-campaigns (name, templateCode, targetSegment)
         │
         ▼
POST /api/v1/mail-campaigns/{id}/queue
         │
         ├── Load target users by segment
         ├── Create Notification per user (channel = EMAIL)
         ├── Campaign status = QUEUED → SENDING → SENT
         └── Async email dispatch
```

---

## API Endpoints

### Notifications (`/api/v1/notifications`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/notifications` | Authenticated | List user's notifications |
| `GET` | `/api/v1/notifications/{id}` | Authenticated | Get notification by ID |
| `GET` | `/api/v1/notifications/unread-count` | Authenticated | Count of unread |
| `POST` | `/api/v1/notifications/{id}/read` | Authenticated | Mark as read |
| `POST` | `/api/v1/notifications/read-all` | Authenticated | Mark all as read |
| `POST` | `/api/v1/notifications/{id}/dismiss` | Authenticated | Dismiss notification |

### Mail Campaigns (`/api/v1/mail-campaigns`)

| Method | Path | Permission | Description |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/mail-campaigns` | `NOTIFICATION:VIEW` | List campaigns |
| `GET` | `/api/v1/mail-campaigns/{id}` | `NOTIFICATION:VIEW` | Get campaign by ID |
| `POST` | `/api/v1/mail-campaigns` | `NOTIFICATION:CREATE` | Create campaign |
| `POST` | `/api/v1/mail-campaigns/{id}/queue` | `NOTIFICATION:EDIT` | Queue for sending |
| `POST` | `/api/v1/mail-campaigns/{id}/cancel` | `NOTIFICATION:EDIT` | Cancel campaign |

---

## Scheduled Tasks

| Task | Schedule | Description |
|------|----------|-------------|
| `retryFailed()` | Every 5 minutes | Re-send notifications with status `FAILED` |

---

## Module Dependencies

```
notification ──depends on──► shared (BaseEntity)
notification ──depends on──► auth (user lookup for recipient)
notification ──listens to──► ALL modules (via Spring Application Events)
```

---

## File Inventory

```
notification/
├── NotificationModuleApi.java
├── package-info.java
├── model/  (Notification, NotificationTemplate, MailCampaign, NotificationStatus, NotificationChannel, MailCampaignStatus)
├── repository/  (NotificationRepository, NotificationTemplateRepository, MailCampaignRepository)
├── service/  (NotificationService, MailService, NotificationScheduler)
├── dto/  (NotificationResponse, MailCampaignRequest, MailCampaignResponse)
├── listener/  (SalesOrderEventListener, ProductEventListener, etc.)
├── web/  (NotificationViewController, MailCampaignViewController)
└── api/  (NotificationRestController, MailCampaignRestController)
```

