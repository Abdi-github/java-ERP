-- ===========================================================
-- V002 — Masterdata: categories, units_of_measure, products,
--         materials, bill_of_materials
-- SwiftApp ERP — Swiss Watch Manufacturing & Retail
-- ===========================================================

-- ── Categories (hierarchical) ─────────────────────────────
CREATE TABLE categories (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  name            VARCHAR(255)   NOT NULL,
  description     TEXT,
  parent_id       UUID           REFERENCES categories(id),
  deleted_at      TIMESTAMPTZ,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_categories_parent ON categories(parent_id);
CREATE INDEX idx_categories_name_trgm ON categories USING gin (name gin_trgm_ops);

-- ── Units of Measure ──────────────────────────────────────
CREATE TABLE units_of_measure (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  code            VARCHAR(20)    NOT NULL UNIQUE,
  name            VARCHAR(100)   NOT NULL,
  description     VARCHAR(255),
  deleted_at      TIMESTAMPTZ,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0
);

-- ── Products (finished watches) ───────────────────────────
CREATE TABLE products (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  sku             VARCHAR(50)    NOT NULL UNIQUE,
  name            VARCHAR(255)   NOT NULL,
  description     TEXT,
  category_id     UUID           REFERENCES categories(id),
  unit_price      NUMERIC(19, 4) NOT NULL DEFAULT 0,
  list_price      NUMERIC(19, 4) NOT NULL DEFAULT 0,
  vat_rate        VARCHAR(50)    NOT NULL DEFAULT 'STANDARD_8_1',
  active          BOOLEAN        NOT NULL DEFAULT true,
  deleted_at      TIMESTAMPTZ,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_name_trgm ON products USING gin (name gin_trgm_ops);
CREATE INDEX idx_products_active ON products(active) WHERE deleted_at IS NULL;

-- ── Materials (raw materials, components) ─────────────────
CREATE TABLE materials (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  sku             VARCHAR(50)    NOT NULL UNIQUE,
  name            VARCHAR(255)   NOT NULL,
  description     TEXT,
  category_id     UUID           REFERENCES categories(id),
  unit_of_measure_id UUID        REFERENCES units_of_measure(id),
  unit_price      NUMERIC(19, 4) NOT NULL DEFAULT 0,
  vat_rate        VARCHAR(50)    NOT NULL DEFAULT 'STANDARD_8_1',
  minimum_stock   NUMERIC(19, 4) NOT NULL DEFAULT 0,
  deleted_at      TIMESTAMPTZ,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_materials_sku ON materials(sku);
CREATE INDEX idx_materials_category ON materials(category_id);
CREATE INDEX idx_materials_name_trgm ON materials USING gin (name gin_trgm_ops);

-- ── Bill of Materials (BOM) ───────────────────────────────
CREATE TABLE bill_of_materials (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  product_id      UUID           NOT NULL REFERENCES products(id),
  material_id     UUID           NOT NULL REFERENCES materials(id),
  quantity        NUMERIC(19, 4) NOT NULL,
  unit_of_measure_id UUID        REFERENCES units_of_measure(id),
  position        INTEGER        NOT NULL DEFAULT 0,
  notes           VARCHAR(500),
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0,
  UNIQUE (product_id, material_id)
);

CREATE INDEX idx_bom_product ON bill_of_materials(product_id);
CREATE INDEX idx_bom_material ON bill_of_materials(material_id);

