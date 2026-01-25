-- ===========================================================
-- V023 — RBAC: permissions, role_permissions
-- SwiftApp ERP — Granular Permission-Based Access Control
-- ===========================================================
-- Adds a `permissions` table with MODULE:ACTION codes and a
-- `role_permissions` junction table to link roles → permissions.
-- Seeds ~50 permissions and assigns them to the 8 existing roles.
-- ===========================================================

-- ── Permissions ───────────────────────────────────────────
CREATE TABLE permissions (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  code            VARCHAR(100)   NOT NULL UNIQUE,
  description     VARCHAR(255),
  module          VARCHAR(50)    NOT NULL,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255)   DEFAULT 'system',
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_permissions_code   ON permissions(code);
CREATE INDEX idx_permissions_module ON permissions(module);

-- ── Role-Permission mapping ───────────────────────────────
CREATE TABLE role_permissions (
  role_id         UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
  permission_id   UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
  PRIMARY KEY (role_id, permission_id)
);

CREATE INDEX idx_role_permissions_role ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_perm ON role_permissions(permission_id);

-- ═══════════════════════════════════════════════════════════
-- Seed permissions — MODULE:ACTION convention
-- ═══════════════════════════════════════════════════════════

INSERT INTO permissions (code, description, module) VALUES
  -- Dashboard
  ('DASHBOARD:VIEW',          'View dashboard',                            'DASHBOARD'),

  -- Master Data
  ('MASTERDATA:VIEW',         'View products, materials, categories',      'MASTERDATA'),
  ('MASTERDATA:CREATE',       'Create master data records',                'MASTERDATA'),
  ('MASTERDATA:EDIT',         'Edit master data records',                  'MASTERDATA'),
  ('MASTERDATA:DELETE',       'Delete master data records',                'MASTERDATA'),

  -- Inventory
  ('INVENTORY:VIEW',          'View stock levels and warehouses',          'INVENTORY'),
  ('INVENTORY:CREATE',        'Create stock movements and warehouses',     'INVENTORY'),
  ('INVENTORY:EDIT',          'Edit warehouses and stock adjustments',     'INVENTORY'),
  ('INVENTORY:DELETE',        'Delete warehouses',                         'INVENTORY'),

  -- Sales
  ('SALES:VIEW',              'View sales orders and customers',           'SALES'),
  ('SALES:CREATE',            'Create sales orders and customers',         'SALES'),
  ('SALES:EDIT',              'Edit sales orders and customers',           'SALES'),
  ('SALES:DELETE',            'Delete sales orders and customers',         'SALES'),

  -- Purchasing
  ('PURCHASING:VIEW',         'View purchase orders and suppliers',        'PURCHASING'),
  ('PURCHASING:CREATE',       'Create purchase orders and suppliers',      'PURCHASING'),
  ('PURCHASING:EDIT',         'Edit purchase orders and suppliers',        'PURCHASING'),
  ('PURCHASING:DELETE',       'Delete purchase orders and suppliers',      'PURCHASING'),

  -- Production
  ('PRODUCTION:VIEW',         'View production orders and work centers',   'PRODUCTION'),
  ('PRODUCTION:CREATE',       'Create production orders',                  'PRODUCTION'),
  ('PRODUCTION:EDIT',         'Edit production orders and work centers',   'PRODUCTION'),
  ('PRODUCTION:DELETE',       'Delete production orders',                  'PRODUCTION'),

  -- Accounting
  ('ACCOUNTING:VIEW',         'View chart of accounts and journal entries','ACCOUNTING'),
  ('ACCOUNTING:CREATE',       'Create journal entries',                    'ACCOUNTING'),
  ('ACCOUNTING:EDIT',         'Edit accounts and journal entries',         'ACCOUNTING'),
  ('ACCOUNTING:DELETE',       'Delete journal entries',                    'ACCOUNTING'),

  -- HR
  ('HR:VIEW',                 'View employees and departments',            'HR'),
  ('HR:CREATE',               'Create employees and departments',          'HR'),
  ('HR:EDIT',                 'Edit employees and departments',            'HR'),
  ('HR:DELETE',               'Delete employees and departments',          'HR'),

  -- CRM
  ('CRM:VIEW',                'View contacts and interactions',            'CRM'),
  ('CRM:CREATE',              'Create contacts and interactions',          'CRM'),
  ('CRM:EDIT',                'Edit contacts and interactions',            'CRM'),
  ('CRM:DELETE',              'Delete contacts and interactions',          'CRM'),

  -- Quality Control
  ('QC:VIEW',                 'View inspection plans, checks, and NCRs',   'QC'),
  ('QC:CREATE',               'Create inspection plans, checks, and NCRs', 'QC'),
  ('QC:EDIT',                 'Edit inspection plans, checks, and NCRs',   'QC'),
  ('QC:DELETE',               'Delete inspection plans, checks, and NCRs', 'QC'),

  -- Notifications
  ('NOTIFICATIONS:VIEW',      'View own notifications',                    'NOTIFICATIONS'),
  ('NOTIFICATIONS:MANAGE',    'Manage all notifications and campaigns',    'NOTIFICATIONS'),

  -- Administration
  ('ADMIN:USERS_VIEW',        'View user list and details',                'ADMIN'),
  ('ADMIN:USERS_MANAGE',      'Create, edit, and delete users',            'ADMIN'),
  ('ADMIN:ROLES_VIEW',        'View roles and permissions',                'ADMIN'),
  ('ADMIN:ROLES_MANAGE',      'Create, edit roles and assign permissions', 'ADMIN'),
  ('ADMIN:MAIL_CAMPAIGNS',    'Manage mail campaigns',                     'ADMIN');


-- ═══════════════════════════════════════════════════════════
-- Assign permissions to roles
-- ═══════════════════════════════════════════════════════════

-- ADMIN — all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN';

-- MANAGER — broad read + write on operations, read on finance
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'MANAGER'
  AND p.code IN (
    'DASHBOARD:VIEW',
    'MASTERDATA:VIEW', 'MASTERDATA:CREATE', 'MASTERDATA:EDIT',
    'INVENTORY:VIEW', 'INVENTORY:CREATE', 'INVENTORY:EDIT',
    'SALES:VIEW', 'SALES:CREATE', 'SALES:EDIT',
    'PURCHASING:VIEW', 'PURCHASING:CREATE', 'PURCHASING:EDIT',
    'PRODUCTION:VIEW', 'PRODUCTION:CREATE', 'PRODUCTION:EDIT',
    'ACCOUNTING:VIEW',
    'HR:VIEW',
    'CRM:VIEW', 'CRM:CREATE', 'CRM:EDIT',
    'QC:VIEW', 'QC:CREATE', 'QC:EDIT',
    'NOTIFICATIONS:VIEW'
  );

-- SALES — Sales + CRM full, read master data & inventory
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'SALES'
  AND p.code IN (
    'DASHBOARD:VIEW',
    'MASTERDATA:VIEW',
    'INVENTORY:VIEW',
    'SALES:VIEW', 'SALES:CREATE', 'SALES:EDIT', 'SALES:DELETE',
    'CRM:VIEW', 'CRM:CREATE', 'CRM:EDIT', 'CRM:DELETE',
    'NOTIFICATIONS:VIEW'
  );

-- PRODUCTION — Production + QC full, read inventory & master data
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'PRODUCTION'
  AND p.code IN (
    'DASHBOARD:VIEW',
    'MASTERDATA:VIEW',
    'INVENTORY:VIEW', 'INVENTORY:CREATE',
    'PRODUCTION:VIEW', 'PRODUCTION:CREATE', 'PRODUCTION:EDIT', 'PRODUCTION:DELETE',
    'QC:VIEW', 'QC:CREATE', 'QC:EDIT', 'QC:DELETE',
    'NOTIFICATIONS:VIEW'
  );

-- WAREHOUSE — Inventory full, read purchasing & master data
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'WAREHOUSE'
  AND p.code IN (
    'DASHBOARD:VIEW',
    'MASTERDATA:VIEW',
    'INVENTORY:VIEW', 'INVENTORY:CREATE', 'INVENTORY:EDIT', 'INVENTORY:DELETE',
    'PURCHASING:VIEW',
    'NOTIFICATIONS:VIEW'
  );

-- ACCOUNTANT — Accounting full, read sales & purchasing
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ACCOUNTANT'
  AND p.code IN (
    'DASHBOARD:VIEW',
    'SALES:VIEW',
    'PURCHASING:VIEW',
    'ACCOUNTING:VIEW', 'ACCOUNTING:CREATE', 'ACCOUNTING:EDIT', 'ACCOUNTING:DELETE',
    'NOTIFICATIONS:VIEW'
  );

-- HR — HR full
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'HR'
  AND p.code IN (
    'DASHBOARD:VIEW',
    'HR:VIEW', 'HR:CREATE', 'HR:EDIT', 'HR:DELETE',
    'NOTIFICATIONS:VIEW'
  );

-- VIEWER — read-only across all modules
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'VIEWER'
  AND p.code IN (
    'DASHBOARD:VIEW',
    'MASTERDATA:VIEW',
    'INVENTORY:VIEW',
    'SALES:VIEW',
    'PURCHASING:VIEW',
    'PRODUCTION:VIEW',
    'ACCOUNTING:VIEW',
    'HR:VIEW',
    'CRM:VIEW',
    'QC:VIEW',
    'NOTIFICATIONS:VIEW'
  );

