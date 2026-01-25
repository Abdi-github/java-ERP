-- ===========================================================
-- V007 — Auth: users, roles, user_roles
-- SwiftApp ERP — Swiss Watch Manufacturing & Retail
-- ===========================================================

-- ── Roles ─────────────────────────────────────────────────
CREATE TABLE roles (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  name            VARCHAR(50)    NOT NULL UNIQUE,
  description     VARCHAR(255),
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0
);

-- ── Users ─────────────────────────────────────────────────
CREATE TABLE users (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  username        VARCHAR(100)   NOT NULL UNIQUE,
  email           VARCHAR(255)   NOT NULL UNIQUE,
  password_hash   VARCHAR(255)   NOT NULL,
  first_name      VARCHAR(100),
  last_name       VARCHAR(100),
  enabled         BOOLEAN        NOT NULL DEFAULT true,
  locked          BOOLEAN        NOT NULL DEFAULT false,
  deleted_at      TIMESTAMPTZ,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- ── User-Role mapping ─────────────────────────────────────
CREATE TABLE user_roles (
  user_id         UUID           NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  role_id         UUID           NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
  PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_user_roles_user ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role_id);

-- ── Seed default roles ────────────────────────────────────
INSERT INTO roles (name, description) VALUES
  ('ADMIN',       'Full system administrator'),
  ('MANAGER',     'Department manager with broad read/write access'),
  ('SALES',       'Sales department staff'),
  ('PRODUCTION',  'Production department staff'),
  ('WAREHOUSE',   'Warehouse and inventory staff'),
  ('ACCOUNTANT',  'Accounting department staff'),
  ('HR',          'Human resources staff'),
  ('VIEWER',      'Read-only access');

