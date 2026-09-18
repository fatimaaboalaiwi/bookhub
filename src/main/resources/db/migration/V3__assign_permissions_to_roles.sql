-- =========================================
-- V3: Assign permissions to roles
-- =========================================

-- ADMIN gets everything
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         CROSS JOIN permissions p
WHERE r.name = 'ADMIN';


-- MANAGER gets read + create
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p
              ON p.name IN (
                            'USER_READ',
                            'USER_CREATE'
                  )
WHERE r.name = 'MANAGER';


-- USER gets read only
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p
              ON p.name = 'USER_READ'
WHERE r.name = 'USER';