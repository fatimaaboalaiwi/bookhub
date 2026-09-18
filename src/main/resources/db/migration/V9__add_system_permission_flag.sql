ALTER TABLE permissions
    ADD COLUMN is_system_permission BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE permissions
SET is_system_permission = TRUE;