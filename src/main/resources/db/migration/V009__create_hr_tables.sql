-- ===========================================================
-- V009 — HR: departments, employees
-- SwiftApp ERP — Swiss Watch Manufacturing & Retail
-- ===========================================================

-- ── Departments ───────────────────────────────────────────
CREATE TABLE departments (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  code            VARCHAR(20)    NOT NULL UNIQUE,
  name            VARCHAR(255)   NOT NULL,
  description     TEXT,
  manager_id      UUID,  -- self-ref to employees (added after employees table)
  active          BOOLEAN        NOT NULL DEFAULT true,
  deleted_at      TIMESTAMPTZ,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_departments_code ON departments(code);
CREATE INDEX idx_departments_name_trgm ON departments USING gin (name gin_trgm_ops);

-- ── Employees ─────────────────────────────────────────────
CREATE TABLE employees (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  employee_number VARCHAR(30)    NOT NULL UNIQUE,
  first_name      VARCHAR(100)   NOT NULL,
  last_name       VARCHAR(100)   NOT NULL,
  email           VARCHAR(255),
  phone           VARCHAR(50),
  hire_date       DATE           NOT NULL,
  termination_date DATE,
  department_id   UUID           REFERENCES departments(id),
  position        VARCHAR(255),
  salary          NUMERIC(19, 4),
  active          BOOLEAN        NOT NULL DEFAULT true,
  deleted_at      TIMESTAMPTZ,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_employees_number ON employees(employee_number);
CREATE INDEX idx_employees_department ON employees(department_id);
CREATE INDEX idx_employees_name_trgm ON employees USING gin (
  (first_name || ' ' || last_name) gin_trgm_ops
);

-- Add FK from departments.manager_id to employees
ALTER TABLE departments
  ADD CONSTRAINT fk_departments_manager
  FOREIGN KEY (manager_id) REFERENCES employees(id);

