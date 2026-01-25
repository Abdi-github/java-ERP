-- ==========================================================
-- V018 — Add missing audit columns to notification_templates
-- BaseEntity requires: created_by, updated_by, version
-- ==========================================================

ALTER TABLE notification_templates
    ADD COLUMN IF NOT EXISTS created_by  VARCHAR(255),
    ADD COLUMN IF NOT EXISTS updated_by  VARCHAR(255),
    ADD COLUMN IF NOT EXISTS version     BIGINT NOT NULL DEFAULT 0;



