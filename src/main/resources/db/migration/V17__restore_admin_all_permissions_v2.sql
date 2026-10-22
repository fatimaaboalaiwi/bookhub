-- V8__insert_missing_permissions.sql

INSERT INTO permissions (name, is_system_permission)
SELECT p.name, p.is_system_permission
FROM (
         VALUES
             ('AUTHOR_UPDATE', FALSE),
             ('UPDATE_AUTHOR', FALSE),
             ('PERMISSION_READ', FALSE),
             ('U_READ', FALSE),
             ('BOOK_EXPORT', FALSE),
             ('GET_ROLE_PERMISSIONS', FALSE),
             ('GET_PERMISSION_BY_ID', FALSE),
             ('GET_USER_ROLES', FALSE),
             ('CREATE_PERMISSION', FALSE),
             ('BOOK_PURCHASE', FALSE),
             ('PERMISSION_CREATE', TRUE),
             ('ASSIGN_ROLE_PERMISSIONS', TRUE),
             ('ASSIGN_USER_ROLE', TRUE),
             ('REMOVE_USER_ROLE', TRUE)
     ) AS p(name, is_system_permission)
WHERE NOT EXISTS (
    SELECT 1
    FROM permissions existing
    WHERE existing.name = p.name
);

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         CROSS JOIN permissions p
WHERE r.name = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1
    FROM role_permissions rp
    WHERE rp.role_id = r.id
      AND rp.permission_id = p.id
);