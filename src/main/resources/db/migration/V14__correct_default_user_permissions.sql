-- Remove permissions that should not belong to regular users.
DELETE FROM role_permissions
WHERE role_id IN (
    SELECT id
    FROM roles
    WHERE name = 'USER'
)
  AND permission_id IN (
    SELECT id
    FROM permissions
    WHERE name IN ('CATEGORY_DELETE', 'USER_READ')
);