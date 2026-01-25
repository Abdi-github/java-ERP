-- ==========================================================
-- V019 — Add missing audit columns to notifications table
-- BaseEntity requires: created_by, updated_by
-- ==========================================================

ALTER TABLE notifications
    ADD COLUMN IF NOT EXISTS created_by  VARCHAR(255),
    ADD COLUMN IF NOT EXISTS updated_by  VARCHAR(255);

