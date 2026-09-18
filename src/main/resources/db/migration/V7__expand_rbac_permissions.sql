-- =========================================
-- V7: Expand RBAC permissions
-- =========================================

-- =========================================
-- 1. Add all application permissions
-- =========================================

INSERT INTO permissions (name)
VALUES
    -- Book permissions
    ('BOOK_READ'),
    ('BOOK_CREATE'),
    ('BOOK_UPDATE'),
    ('BOOK_DELETE'),

    -- Author permissions
    ('AUTHOR_READ'),
    ('AUTHOR_CREATE'),
    ('AUTHOR_UPDATE'),
    ('AUTHOR_DELETE'),

    -- Category permissions
    ('CATEGORY_READ'),
    ('CATEGORY_CREATE'),
    ('CATEGORY_UPDATE'),
    ('CATEGORY_DELETE'),

    -- User permissions
    ('USER_READ'),
    ('USER_CREATE'),
    ('USER_UPDATE'),
    ('USER_DELETE')
    ON CONFLICT (name) DO NOTHING;


-- =========================================
-- 2. ADMIN gets ALL permissions
-- =========================================

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


-- =========================================
-- 3. MANAGER permissions
-- =========================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p
              ON p.name IN (
                            'BOOK_READ',
                            'BOOK_CREATE',
                            'BOOK_UPDATE',

                            'AUTHOR_READ',
                            'AUTHOR_CREATE',
                            'AUTHOR_UPDATE',

                            'CATEGORY_READ',
                            'CATEGORY_CREATE',
                            'CATEGORY_UPDATE',

                            'USER_READ',
                            'USER_CREATE',
                            'USER_UPDATE'
                  )
WHERE r.name = 'MANAGER'
  AND NOT EXISTS (
    SELECT 1
    FROM role_permissions rp
    WHERE rp.role_id = r.id
      AND rp.permission_id = p.id
);


-- =========================================
-- 4. USER permissions
-- =========================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p
              ON p.name IN (
                            'BOOK_READ',
                            'AUTHOR_READ',
                            'CATEGORY_READ',
                            'USER_READ'
                  )
WHERE r.name = 'USER'
  AND NOT EXISTS (
    SELECT 1
    FROM role_permissions rp
    WHERE rp.role_id = r.id
      AND rp.permission_id = p.id
);