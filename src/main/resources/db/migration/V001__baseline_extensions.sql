-- ===========================================================
-- V001 — Baseline: Enable PostgreSQL extensions
-- SwiftApp ERP — Swiss Watch Manufacturing & Retail
-- ===========================================================

-- UUID generation support
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Trigram support for full-text search
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

