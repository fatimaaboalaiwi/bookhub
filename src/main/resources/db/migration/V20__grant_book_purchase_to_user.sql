INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         CROSS JOIN permissions p
WHERE r.name = 'USER'
  AND p.name = 'BOOK_PURCHASE'
    ON CONFLICT (role_id, permission_id) DO NOTHING;