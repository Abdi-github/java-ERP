-- ===========================================================
-- V013 — Entity Translation Tables (Companion Pattern)
-- SwiftApp ERP — Swiss Watch Manufacturing & Retail
-- Supports locales: de (default), fr, it, en
-- ===========================================================

-- ── Product Translations ──────────────────────────────────
CREATE TABLE product_translations (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  product_id      UUID           NOT NULL REFERENCES products(id) ON DELETE CASCADE,
  locale          VARCHAR(10)    NOT NULL,
  name            VARCHAR(255),
  description     TEXT,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0,
  CONSTRAINT uq_product_translations_product_locale UNIQUE (product_id, locale)
);

CREATE INDEX idx_product_translations_product ON product_translations(product_id);
CREATE INDEX idx_product_translations_locale  ON product_translations(locale);

-- ── Category Translations ─────────────────────────────────
CREATE TABLE category_translations (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  category_id     UUID           NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
  locale          VARCHAR(10)    NOT NULL,
  name            VARCHAR(255),
  description     TEXT,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0,
  CONSTRAINT uq_category_translations_category_locale UNIQUE (category_id, locale)
);

CREATE INDEX idx_category_translations_category ON category_translations(category_id);

-- ── Material Translations ─────────────────────────────────
CREATE TABLE material_translations (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  material_id     UUID           NOT NULL REFERENCES materials(id) ON DELETE CASCADE,
  locale          VARCHAR(10)    NOT NULL,
  name            VARCHAR(255),
  description     TEXT,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0,
  CONSTRAINT uq_material_translations_material_locale UNIQUE (material_id, locale)
);

CREATE INDEX idx_material_translations_material ON material_translations(material_id);

-- ── Unit of Measure Translations ──────────────────────────
CREATE TABLE uom_translations (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  uom_id          UUID           NOT NULL REFERENCES units_of_measure(id) ON DELETE CASCADE,
  locale          VARCHAR(10)    NOT NULL,
  name            VARCHAR(100),
  description     VARCHAR(255),
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0,
  CONSTRAINT uq_uom_translations_uom_locale UNIQUE (uom_id, locale)
);

CREATE INDEX idx_uom_translations_uom ON uom_translations(uom_id);

-- ── Warehouse Translations ────────────────────────────────
CREATE TABLE warehouse_translations (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  warehouse_id    UUID           NOT NULL REFERENCES warehouses(id) ON DELETE CASCADE,
  locale          VARCHAR(10)    NOT NULL,
  name            VARCHAR(255),
  description     TEXT,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0,
  CONSTRAINT uq_warehouse_translations_warehouse_locale UNIQUE (warehouse_id, locale)
);

CREATE INDEX idx_warehouse_translations_warehouse ON warehouse_translations(warehouse_id);

-- ── Work Center Translations ──────────────────────────────
CREATE TABLE work_center_translations (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  work_center_id  UUID           NOT NULL REFERENCES work_centers(id) ON DELETE CASCADE,
  locale          VARCHAR(10)    NOT NULL,
  name            VARCHAR(255),
  description     TEXT,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0,
  CONSTRAINT uq_work_center_translations_wc_locale UNIQUE (work_center_id, locale)
);

CREATE INDEX idx_work_center_translations_wc ON work_center_translations(work_center_id);

-- ── Department Translations ───────────────────────────────
CREATE TABLE department_translations (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  department_id   UUID           NOT NULL REFERENCES departments(id) ON DELETE CASCADE,
  locale          VARCHAR(10)    NOT NULL,
  name            VARCHAR(255),
  description     TEXT,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0,
  CONSTRAINT uq_department_translations_dept_locale UNIQUE (department_id, locale)
);

CREATE INDEX idx_department_translations_dept ON department_translations(department_id);

