UPDATE roles
SET is_system_role = TRUE
WHERE UPPER(name) = 'ADMIN';