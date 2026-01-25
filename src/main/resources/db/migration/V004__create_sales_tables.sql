-- ===========================================================
-- V004 — Sales: customers, sales_orders, sales_order_lines,
--         quotations, quotation_lines
-- SwiftApp ERP — Swiss Watch Manufacturing & Retail
-- ===========================================================

-- ── Customers ─────────────────────────────────────────────
CREATE TABLE customers (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  customer_number VARCHAR(30)    NOT NULL UNIQUE,
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
  credit_limit    NUMERIC(19, 4) NOT NULL DEFAULT 0,
  notes           TEXT,
  active          BOOLEAN        NOT NULL DEFAULT true,
  deleted_at      TIMESTAMPTZ,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_customers_number ON customers(customer_number);
CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_name_trgm ON customers USING gin (
  (COALESCE(company_name, '') || ' ' || COALESCE(first_name, '') || ' ' || COALESCE(last_name, '')) gin_trgm_ops
);

-- ── Sales Orders ──────────────────────────────────────────
CREATE TABLE sales_orders (
  id                UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  order_number      VARCHAR(30)    NOT NULL UNIQUE,
  customer_id       UUID           NOT NULL REFERENCES customers(id),
  status            VARCHAR(30)    NOT NULL DEFAULT 'DRAFT',
  order_date        DATE           NOT NULL DEFAULT CURRENT_DATE,
  delivery_date     DATE,
  subtotal          NUMERIC(19, 4) NOT NULL DEFAULT 0,
  vat_amount        NUMERIC(19, 4) NOT NULL DEFAULT 0,
  total_amount      NUMERIC(19, 4) NOT NULL DEFAULT 0,
  currency          VARCHAR(3)     NOT NULL DEFAULT 'CHF',
  notes             TEXT,
  shipping_street   VARCHAR(255),
  shipping_city     VARCHAR(100),
  shipping_postal_code VARCHAR(20),
  shipping_canton   VARCHAR(50),
  shipping_country  VARCHAR(3)     DEFAULT 'CH',
  deleted_at        TIMESTAMPTZ,
  created_at        TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at        TIMESTAMPTZ,
  created_by        VARCHAR(255),
  updated_by        VARCHAR(255),
  version           BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_sales_orders_number ON sales_orders(order_number);
CREATE INDEX idx_sales_orders_customer ON sales_orders(customer_id);
CREATE INDEX idx_sales_orders_status ON sales_orders(status);
CREATE INDEX idx_sales_orders_date ON sales_orders(order_date DESC);

-- ── Sales Order Lines ─────────────────────────────────────
CREATE TABLE sales_order_lines (
  id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  sales_order_id  UUID           NOT NULL REFERENCES sales_orders(id) ON DELETE CASCADE,
  product_id      UUID           NOT NULL REFERENCES products(id),
  description     VARCHAR(500),
  quantity        NUMERIC(19, 4) NOT NULL,
  unit_price      NUMERIC(19, 4) NOT NULL,
  discount_pct    NUMERIC(5, 2)  NOT NULL DEFAULT 0,
  vat_rate        VARCHAR(50)    NOT NULL DEFAULT 'STANDARD_8_1',
  line_total      NUMERIC(19, 4) NOT NULL DEFAULT 0,
  position        INTEGER        NOT NULL DEFAULT 0,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ,
  created_by      VARCHAR(255),
  updated_by      VARCHAR(255),
  version         BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_sales_order_lines_order ON sales_order_lines(sales_order_id);
CREATE INDEX idx_sales_order_lines_product ON sales_order_lines(product_id);

