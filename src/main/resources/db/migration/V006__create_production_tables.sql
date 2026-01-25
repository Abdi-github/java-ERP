-- ===========================================================
-- V006 — Production: work_centers, production_orders,
--         production_order_lines
-- SwiftApp ERP — Swiss Watch Manufacturing & Retail
-- ===========================================================

-- ── Work Centers (manufacturing stations / areas) ─────────
CREATE TABLE work_centers (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  code            VARCHAR(20)    NOT NULL UNIQUE,
  name            VARCHAR(255)   NOT NULL,
  description     TEXT,
  capacity_per_day NUMERIC(19, 4) NOT NULL DEFAULT 1,
  cost_per_hour   NUMERIC(19, 4) NOT NULL DEFAULT 0,
  active          BOOLEAN        NOT NULL DEFAULT true,
  deleted_at      TIMESTAMPTZ,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_work_centers_code ON work_centers(code);
CREATE INDEX idx_work_centers_active ON work_centers(active) WHERE deleted_at IS NULL;

-- ── Production Orders ─────────────────────────────────────
CREATE TABLE production_orders (
  id                  UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  order_number        VARCHAR(30)    NOT NULL UNIQUE,
  product_id          UUID           NOT NULL REFERENCES products(id),
  work_center_id      UUID           REFERENCES work_centers(id),
  status              VARCHAR(30)    NOT NULL DEFAULT 'PLANNED',
  planned_quantity    NUMERIC(19, 4) NOT NULL,
  completed_quantity  NUMERIC(19, 4) NOT NULL DEFAULT 0,
  scrap_quantity      NUMERIC(19, 4) NOT NULL DEFAULT 0,
  planned_start_date  DATE,
  planned_end_date    DATE,
  actual_start_date   DATE,
  actual_end_date     DATE,
  estimated_cost      NUMERIC(19, 4) NOT NULL DEFAULT 0,
  actual_cost         NUMERIC(19, 4) NOT NULL DEFAULT 0,
  currency            VARCHAR(3)     NOT NULL DEFAULT 'CHF',
  priority            INTEGER        NOT NULL DEFAULT 0,
  notes               TEXT,
  deleted_at          TIMESTAMPTZ,
  created_at          TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at          TIMESTAMPTZ,
  created_by          VARCHAR(255),
  updated_by          VARCHAR(255),
  version             BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_production_orders_number ON production_orders(order_number);
CREATE INDEX idx_production_orders_product ON production_orders(product_id);
CREATE INDEX idx_production_orders_work_center ON production_orders(work_center_id);
CREATE INDEX idx_production_orders_status ON production_orders(status);
CREATE INDEX idx_production_orders_planned_start ON production_orders(planned_start_date);

-- ── Production Order Lines (materials consumed) ───────────
CREATE TABLE production_order_lines (
  id                    UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  production_order_id   UUID           NOT NULL REFERENCES production_orders(id) ON DELETE CASCADE,
  material_id           UUID           NOT NULL REFERENCES materials(id),
  description           VARCHAR(500),
  planned_quantity      NUMERIC(19, 4) NOT NULL,
  actual_quantity       NUMERIC(19, 4) NOT NULL DEFAULT 0,
  unit_price            NUMERIC(19, 4) NOT NULL DEFAULT 0,
  line_cost             NUMERIC(19, 4) NOT NULL DEFAULT 0,
  position              INTEGER        NOT NULL DEFAULT 0,
  created_at            TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at            TIMESTAMPTZ,
  created_by            VARCHAR(255),
  updated_by            VARCHAR(255),
  version               BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_production_order_lines_order ON production_order_lines(production_order_id);
CREATE INDEX idx_production_order_lines_material ON production_order_lines(material_id);

