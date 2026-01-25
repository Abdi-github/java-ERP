-- ===========================================================
-- V010 — CRM: contacts, interactions
-- SwiftApp ERP — Swiss Watch Manufacturing & Retail
-- ===========================================================

CREATE TABLE contacts (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  first_name      VARCHAR(100)   NOT NULL,
  last_name       VARCHAR(100)   NOT NULL,
  email           VARCHAR(255),
  phone           VARCHAR(50),
  company         VARCHAR(255),
  position        VARCHAR(255),
  customer_id     UUID           REFERENCES customers(id),
  notes           TEXT,
  active          BOOLEAN        NOT NULL DEFAULT true,
  deleted_at      TIMESTAMPTZ,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_contacts_customer ON contacts(customer_id);
CREATE INDEX idx_contacts_name_trgm ON contacts USING gin (
  (first_name || ' ' || last_name) gin_trgm_ops
);

CREATE TABLE interactions (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  contact_id      UUID           NOT NULL REFERENCES contacts(id),
  interaction_type VARCHAR(30)   NOT NULL,  -- CALL, EMAIL, MEETING, NOTE
  subject         VARCHAR(255)   NOT NULL,
  description     TEXT,
  interaction_date TIMESTAMPTZ   NOT NULL DEFAULT now(),
  follow_up_date  DATE,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_interactions_contact ON interactions(contact_id);
CREATE INDEX idx_interactions_date ON interactions(interaction_date DESC);
CREATE INDEX idx_interactions_follow_up ON interactions(follow_up_date) WHERE follow_up_date IS NOT NULL;

