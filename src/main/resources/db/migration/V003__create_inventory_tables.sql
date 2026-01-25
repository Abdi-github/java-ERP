-- ===========================================================
-- V003 — Inventory: warehouses, stock_levels, stock_movements
-- SwiftApp ERP — Swiss Watch Manufacturing & Retail
-- ===========================================================

-- ── Warehouses ────────────────────────────────────────────
CREATE TABLE warehouses (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  code            VARCHAR(20)    NOT NULL UNIQUE,
  name            VARCHAR(255)   NOT NULL,
  description     TEXT,
  address         TEXT,
  active          BOOLEAN        NOT NULL DEFAULT true,
  deleted_at      TIMESTAMPTZ,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_warehouses_code ON warehouses(code);
CREATE INDEX idx_warehouses_active ON warehouses(active) WHERE deleted_at IS NULL;

-- ── Stock Levels ──────────────────────────────────────────
CREATE TABLE stock_levels (
  id                  UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  item_id             UUID            NOT NULL,
  item_type           VARCHAR(20)     NOT NULL,
  warehouse_id        UUID            NOT NULL REFERENCES warehouses(id),
  quantity_on_hand    NUMERIC(19, 4)  NOT NULL DEFAULT 0,
  quantity_reserved   NUMERIC(19, 4)  NOT NULL DEFAULT 0,
  created_at          TIMESTAMPTZ     NOT NULL DEFAULT now(),
  updated_at          TIMESTAMPTZ,
  created_by          VARCHAR(255),
  updated_by          VARCHAR(255),
  version             BIGINT          NOT NULL DEFAULT 0,
  UNIQUE (item_id, item_type, warehouse_id)
);

CREATE INDEX idx_stock_levels_item ON stock_levels(item_id, item_type);
CREATE INDEX idx_stock_levels_warehouse ON stock_levels(warehouse_id);

-- ── Stock Movements ───────────────────────────────────────
CREATE TABLE stock_movements (
  id                    UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  reference_number      VARCHAR(50)     NOT NULL UNIQUE,
  movement_type         VARCHAR(30)     NOT NULL,
  item_id               UUID            NOT NULL,
  item_type             VARCHAR(20)     NOT NULL,
  source_warehouse_id   UUID            REFERENCES warehouses(id),
  target_warehouse_id   UUID            REFERENCES warehouses(id),
  quantity              NUMERIC(19, 4)  NOT NULL,
  movement_date         TIMESTAMPTZ     NOT NULL,
  reason                VARCHAR(500),
  source_document_type  VARCHAR(50),
  source_document_id    UUID,
  created_at            TIMESTAMPTZ     NOT NULL DEFAULT now(),
  updated_at            TIMESTAMPTZ,
  created_by            VARCHAR(255),
  updated_by            VARCHAR(255),
  version               BIGINT          NOT NULL DEFAULT 0
);

CREATE INDEX idx_stock_movements_ref ON stock_movements(reference_number);
CREATE INDEX idx_stock_movements_item ON stock_movements(item_id, item_type);
CREATE INDEX idx_stock_movements_date ON stock_movements(movement_date DESC);
CREATE INDEX idx_stock_movements_type ON stock_movements(movement_type);
CREATE INDEX idx_stock_movements_source_wh ON stock_movements(source_warehouse_id);
CREATE INDEX idx_stock_movements_target_wh ON stock_movements(target_warehouse_id);

