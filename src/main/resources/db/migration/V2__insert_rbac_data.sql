-- =========================================
-- V2: Insert RBAC roles and permissions
-- =========================================

-- =========================================
-- ROLES
-- =========================================

INSERT INTO roles (name)
VALUES
    ('ADMIN'),
    ('MANAGER'),
    ('USER');


-- =========================================
-- PERMISSIONS
-- =========================================

INSERT INTO permissions (name)
VALUES
    ('USER_READ'),
    ('USER_CREATE'),
    ('USER_DELETE');