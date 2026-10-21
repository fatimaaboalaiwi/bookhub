
-- Add permissions for role management.
INSERT INTO permissions (name)
VALUES
    ('ROLE_CREATE'),
    ('ROLE_UPDATE'),
    ('ROLE_DELETE')
    ON CONFLICT (name) DO NOTHING;

-- Assign the new permissions to the ADMIN role only.
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         CROSS JOIN permissions p
WHERE r.name = 'ADMIN'
  AND p.name IN ('ROLE_CREATE', 'ROLE_UPDATE', 'ROLE_DELETE')
  AND NOT EXISTS (
    SELECT 1
    FROM role_permissions rp
    WHERE rp.role_id = r.id
      AND rp.permission_id = p.id
);
