-- ============================================================
-- V020: Fix seed user passwords
-- ============================================================
-- BCrypt hash for "password" (cost 10, $2b$ format — compatible with Spring BCryptPasswordEncoder)
-- Generated: 2026-03-30
-- ============================================================
UPDATE users
SET password_hash = '$2b$10$hdUwT911cef8ZjwG/zDEcuafvXcwTymufEhfKROsVFSNAR2l70U2K'
WHERE created_by = 'system';

