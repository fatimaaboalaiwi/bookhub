-- First, make all permissions regular permissions.
UPDATE permissions
SET is_system_permission = FALSE;

-- Protect only selected system permissions.
UPDATE permissions
SET is_system_permission = TRUE
WHERE name IN (
               'ASSIGN_ROLE_PERMISSIONS',
               'ASSIGN_USER_ROLE',
               'REMOVE_USER_ROLE'
    );