-- ===========================================================
-- V005 — Purchasing: suppliers, purchase_orders,
--         purchase_order_lines
-- SwiftApp ERP — Swiss Watch Manufacturing & Retail
-- ===========================================================

-- ── Suppliers ─────────────────────────────────────────────
CREATE TABLE suppliers (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  supplier_number VARCHAR(30)    NOT NULL UNIQUE,
  company_name    VARCHAR(255),
  first_name      VARCHAR(100),
  last_name       VARCHAR(100),
  email           VARCHAR(255),
  phone           VARCHAR(50),
  street          VARCHAR(255),
  city            VARCHAR(100),
  postal_code     VARCHAR(20),
  canton          VARCHAR(50),
  country         VARCHAR(3)     NOT NULL DEFAULT 'CH',
  vat_number      VARCHAR(30),
  payment_terms   INTEGER        NOT NULL DEFAULT 30,
  contact_person  VARCHAR(255),
  website         VARCHAR(255),
  notes           TEXT,
  active          BOOLEAN        NOT NULL DEFAULT true,
  deleted_at      TIMESTAMPTZ,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_suppliers_number ON suppliers(supplier_number);
CREATE INDEX idx_suppliers_email ON suppliers(email);
CREATE INDEX idx_suppliers_name_trgm ON suppliers USING gin (
  (COALESCE(company_name, '') || ' ' || COALESCE(first_name, '') || ' ' || COALESCE(last_name, '')) gin_trgm_ops
);

-- ── Purchase Orders ───────────────────────────────────────
CREATE TABLE purchase_orders (
  id                      UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  order_number            VARCHAR(30)    NOT NULL UNIQUE,
  supplier_id             UUID           NOT NULL REFERENCES suppliers(id),
  status                  VARCHAR(30)    NOT NULL DEFAULT 'DRAFT',
  order_date              DATE           NOT NULL DEFAULT CURRENT_DATE,
  expected_delivery_date  DATE,
  actual_delivery_date    DATE,
  subtotal                NUMERIC(19, 4) NOT NULL DEFAULT 0,
  vat_amount              NUMERIC(19, 4) NOT NULL DEFAULT 0,
  total_amount            NUMERIC(19, 4) NOT NULL DEFAULT 0,
  currency                VARCHAR(3)     NOT NULL DEFAULT 'CHF',
  notes                   TEXT,
  deleted_at              TIMESTAMPTZ,
  created_at              TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at              TIMESTAMPTZ,
  created_by              VARCHAR(255),
  updated_by              VARCHAR(255),
  version                 BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_purchase_orders_number ON purchase_orders(order_number);
CREATE INDEX idx_purchase_orders_supplier ON purchase_orders(supplier_id);
CREATE INDEX idx_purchase_orders_status ON purchase_orders(status);
CREATE INDEX idx_purchase_orders_date ON purchase_orders(order_date DESC);

-- ── Purchase Order Lines ──────────────────────────────────
CREATE TABLE purchase_order_lines (
  id                  UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  purchase_order_id   UUID           NOT NULL REFERENCES purchase_orders(id) ON DELETE CASCADE,
  material_id         UUID           NOT NULL REFERENCES materials(id),
  description         VARCHAR(500),
  quantity            NUMERIC(19, 4) NOT NULL,
  unit_price          NUMERIC(19, 4) NOT NULL,
  discount_pct        NUMERIC(5, 2)  NOT NULL DEFAULT 0,
  vat_rate            VARCHAR(50)    NOT NULL DEFAULT 'STANDARD_8_1',
  line_total          NUMERIC(19, 4) NOT NULL DEFAULT 0,
  position            INTEGER        NOT NULL DEFAULT 0,
  created_at          TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at          TIMESTAMPTZ,
  created_by          VARCHAR(255),
  updated_by          VARCHAR(255),
  version             BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_purchase_order_lines_order ON purchase_order_lines(purchase_order_id);
CREATE INDEX idx_purchase_order_lines_material ON purchase_order_lines(material_id);

