update roles set is_system_role = true where name = 'USER';

-- Insert default admin user if it does not exist
INSERT INTO users (username, password, is_system_user)
SELECT 'admin', '$2a$10$xRCaDanWV79O/4plqaS4ceeZw0VVxCYgV9Y3Aa9lkTUkcrTg/DBba', TRUE
    WHERE NOT EXISTS (
    SELECT 1
    FROM users
    WHERE username = 'admin'
);

-- Assign the existing ADMIN role if not already assigned
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         CROSS JOIN roles r
WHERE u.username = 'admin'
  AND r.name = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1
    FROM user_roles ur
    WHERE ur.user_id = u.id
      AND ur.role_id = r.id
);