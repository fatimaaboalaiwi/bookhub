-- =========================================
-- V8: Add system user protection flag
-- =========================================

ALTER TABLE users
    ADD COLUMN is_system_user BOOLEAN NOT NULL DEFAULT FALSE;