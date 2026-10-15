ALTER TABLE roles
    ADD COLUMN if not exists is_system_role BOOLEAN NOT NULL DEFAULT FALSE;