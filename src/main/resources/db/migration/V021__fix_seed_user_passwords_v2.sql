-- ============================================================
-- V021: Fix seed user passwords to "password123"
-- ============================================================
-- BCrypt hash for "password123" (cost 10, $2b$ format — compatible with Spring BCryptPasswordEncoder)
-- Generated: 2026-03-30
-- ============================================================
UPDATE users
SET password_hash = '$2b$10$DjXkAFt4HRjgr58GRLKRguBzeAGzA11CIQJgwupGsxYuF4tQR5u4K'
WHERE created_by = 'system';

