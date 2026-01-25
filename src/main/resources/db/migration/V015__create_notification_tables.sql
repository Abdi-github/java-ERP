-- ==========================================================
-- V015 — Notification & Mailing System
-- SwiftApp ERP | Swiss Watch Manufacturing & Retail
-- ==========================================================

-- ── Notification templates ────────────────────────────────
-- DB-stored templates keyed by code + locale, rendered by
-- Thymeleaf on the fly for personalization
CREATE TABLE notification_templates (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code            VARCHAR(100)  NOT NULL,   -- e.g. 'SALES_ORDER_CONFIRMED'
    channel         VARCHAR(20)   NOT NULL,   -- EMAIL | IN_APP
    locale          VARCHAR(10)   NOT NULL,   -- de | fr | it | en
    subject         VARCHAR(500),
    body_template   TEXT          NOT NULL,   -- Thymeleaf fragment path or inline
    active          BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT uq_notification_template UNIQUE (code, channel, locale)
);

-- ── In-app & email notification log ──────────────────────
-- Every notification sent is persisted here for audit (nDSG)
-- and for the in-app notification centre
CREATE TABLE notifications (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recipient_user_id   UUID          NOT NULL,  -- references auth.users — stored as UUID only (cross-module)
    recipient_email     VARCHAR(255),
    template_code       VARCHAR(100),
    channel             VARCHAR(20)   NOT NULL,  -- EMAIL | IN_APP | BOTH
    status              VARCHAR(20)   NOT NULL DEFAULT 'PENDING',  -- PENDING | SENT | FAILED | READ | DISMISSED
    subject             VARCHAR(500),
    body                TEXT,
    reference_type      VARCHAR(100),  -- e.g. 'SALES_ORDER', 'PRODUCTION_ORDER'
    reference_id        UUID,          -- the related business entity
    error_message       TEXT,
    retry_count         INT           NOT NULL DEFAULT 0,
    sent_at             TIMESTAMPTZ,
    read_at             TIMESTAMPTZ,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ   NOT NULL DEFAULT now(),
    version             BIGINT        NOT NULL DEFAULT 0
);

CREATE INDEX idx_notifications_recipient    ON notifications (recipient_user_id);
CREATE INDEX idx_notifications_status       ON notifications (status);
CREATE INDEX idx_notifications_channel      ON notifications (channel);
CREATE INDEX idx_notifications_reference    ON notifications (reference_type, reference_id);
CREATE INDEX idx_notifications_created      ON notifications (created_at DESC);

-- ── Mail campaigns (mass mail) ────────────────────────────
CREATE TABLE mail_campaigns (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                VARCHAR(255)  NOT NULL,
    description         TEXT,
    template_code       VARCHAR(100)  NOT NULL,
    locale              VARCHAR(10)   NOT NULL DEFAULT 'de',
    target_segment      VARCHAR(100),          -- ALL_USERS | ROLE_SALES | ALL_CUSTOMERS | etc.
    status              VARCHAR(20)   NOT NULL DEFAULT 'DRAFT',  -- DRAFT | QUEUED | RUNNING | COMPLETED | FAILED | CANCELLED
    total_recipients    INT           NOT NULL DEFAULT 0,
    sent_count          INT           NOT NULL DEFAULT 0,
    failed_count        INT           NOT NULL DEFAULT 0,
    scheduled_at        TIMESTAMPTZ,
    started_at          TIMESTAMPTZ,
    completed_at        TIMESTAMPTZ,
    subject_override    VARCHAR(500),          -- optional manual subject override
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    version             BIGINT        NOT NULL DEFAULT 0
);

CREATE INDEX idx_mail_campaigns_status      ON mail_campaigns (status);
CREATE INDEX idx_mail_campaigns_scheduled   ON mail_campaigns (scheduled_at);

-- ── Seed: default notification templates ─────────────────

-- Sales Order Confirmed — German
INSERT INTO notification_templates (code, channel, locale, subject, body_template, active) VALUES
('SALES_ORDER_CONFIRMED', 'EMAIL', 'de',
 'Bestellbestätigung #{orderNumber}',
 'email/sales-order-confirmed', TRUE),
('SALES_ORDER_CONFIRMED', 'IN_APP', 'de',
 NULL,
 'Bestellung #{orderNumber} wurde bestätigt.', TRUE),

-- Sales Order Confirmed — French
('SALES_ORDER_CONFIRMED', 'EMAIL', 'fr',
 'Confirmation de commande #{orderNumber}',
 'email/sales-order-confirmed', TRUE),

-- Sales Order Confirmed — English
('SALES_ORDER_CONFIRMED', 'EMAIL', 'en',
 'Order Confirmation #{orderNumber}',
 'email/sales-order-confirmed', TRUE),

-- Sales Order Cancelled — German
('SALES_ORDER_CANCELLED', 'EMAIL', 'de',
 'Bestellung #{orderNumber} storniert',
 'email/sales-order-cancelled', TRUE),
('SALES_ORDER_CANCELLED', 'IN_APP', 'de',
 NULL,
 'Bestellung #{orderNumber} wurde storniert.', TRUE),

-- Purchase Order Confirmed
('PURCHASE_ORDER_CONFIRMED', 'EMAIL', 'de',
 'Bestellbestätigung Lieferant #{orderNumber}',
 'email/purchase-order-confirmed', TRUE),
('PURCHASE_ORDER_CONFIRMED', 'IN_APP', 'de',
 NULL,
 'Lieferantenbestellung #{orderNumber} bestätigt.', TRUE),

-- Production Order Released
('PRODUCTION_ORDER_RELEASED', 'IN_APP', 'de',
 NULL,
 'Produktionsauftrag #{orderNumber} freigegeben.', TRUE),

-- Production Order Completed
('PRODUCTION_ORDER_COMPLETED', 'EMAIL', 'de',
 'Produktionsauftrag #{orderNumber} abgeschlossen',
 'email/production-order-completed', TRUE),
('PRODUCTION_ORDER_COMPLETED', 'IN_APP', 'de',
 NULL,
 'Produktionsauftrag #{orderNumber} wurde abgeschlossen.', TRUE),

-- Quality Check Failed
('QUALITY_CHECK_FAILED', 'EMAIL', 'de',
 'Qualitätsprüfung fehlgeschlagen – #{checkNumber}',
 'email/quality-check-failed', TRUE),
('QUALITY_CHECK_FAILED', 'IN_APP', 'de',
 NULL,
 'Qualitätsprüfung #{checkNumber} fehlgeschlagen!', TRUE),

-- NCR Created
('NCR_CREATED', 'EMAIL', 'de',
 'Neuer Nichtkonformitätsbericht #{ncrNumber}',
 'email/ncr-created', TRUE),
('NCR_CREATED', 'IN_APP', 'de',
 NULL,
 'Nichtkonformitätsbericht #{ncrNumber} erstellt.', TRUE),

-- Employee Welcome
('EMPLOYEE_WELCOME', 'EMAIL', 'de',
 'Willkommen bei SwiftApp – #{fullName}',
 'email/employee-welcome', TRUE),
('EMPLOYEE_WELCOME', 'IN_APP', 'de',
 NULL,
 'Willkommen, #{fullName}! Ihr Mitarbeiterprofil wurde erstellt.', TRUE),

-- User Account Created
('USER_ACCOUNT_CREATED', 'EMAIL', 'de',
 'Ihr SwiftApp ERP Konto wurde erstellt',
 'email/user-account-created', TRUE),

-- Daily Digest
('DAILY_DIGEST', 'EMAIL', 'de',
 'SwiftApp Tagesübersicht – #{date}',
 'email/daily-digest', TRUE);

