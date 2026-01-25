-- ===========================================================
-- V011 — Quality Control: inspection_plans, quality_checks,
--         non_conformance_reports
-- SwiftApp ERP — Swiss Watch Manufacturing & Retail
-- ===========================================================

CREATE TABLE inspection_plans (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  plan_number     VARCHAR(30)    NOT NULL UNIQUE,
  name            VARCHAR(255)   NOT NULL,
  description     TEXT,
  product_id      UUID           REFERENCES products(id),
  material_id     UUID           REFERENCES materials(id),
  active          BOOLEAN        NOT NULL DEFAULT true,
  deleted_at      TIMESTAMPTZ,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_inspection_plans_number ON inspection_plans(plan_number);
CREATE INDEX idx_inspection_plans_product ON inspection_plans(product_id);

CREATE TABLE quality_checks (
  id                  UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  check_number        VARCHAR(30)    NOT NULL UNIQUE,
  inspection_plan_id  UUID           NOT NULL REFERENCES inspection_plans(id),
  production_order_id UUID,
  checked_by          VARCHAR(255),
  check_date          DATE           NOT NULL DEFAULT CURRENT_DATE,
  result              VARCHAR(30)    NOT NULL,  -- PASS, FAIL, CONDITIONAL
  notes               TEXT,
  created_at          TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at          TIMESTAMPTZ,
  created_by          VARCHAR(255),
  updated_by          VARCHAR(255),
  version             BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_quality_checks_plan ON quality_checks(inspection_plan_id);
CREATE INDEX idx_quality_checks_result ON quality_checks(result);
CREATE INDEX idx_quality_checks_date ON quality_checks(check_date DESC);

CREATE TABLE non_conformance_reports (
  id                UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  ncr_number        VARCHAR(30)    NOT NULL UNIQUE,
  quality_check_id  UUID           NOT NULL REFERENCES quality_checks(id),
  severity          VARCHAR(30)    NOT NULL,  -- MINOR, MAJOR, CRITICAL
  description       TEXT           NOT NULL,
  corrective_action TEXT,
  status            VARCHAR(30)    NOT NULL DEFAULT 'OPEN',  -- OPEN, IN_PROGRESS, CLOSED
  closed_at         TIMESTAMPTZ,
  created_at        TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at        TIMESTAMPTZ,
  created_by        VARCHAR(255),
  updated_by        VARCHAR(255),
  version           BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_ncr_check ON non_conformance_reports(quality_check_id);
CREATE INDEX idx_ncr_status ON non_conformance_reports(status);
CREATE INDEX idx_ncr_severity ON non_conformance_reports(severity);

