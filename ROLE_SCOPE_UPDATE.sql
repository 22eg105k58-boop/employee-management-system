-- Existing database update for the new administrator roles.
-- Run only if these usernames already exist and need their roles corrected.
UPDATE users SET role = 'ADMIN' WHERE username = 'admin';
UPDATE users SET role = 'IT_ADMIN' WHERE username = 'itadmin';
UPDATE users SET role = 'HR_ADMIN' WHERE username = 'hradmin';

-- Optional verification
SELECT username, role, employee_id, active
FROM users
ORDER BY username;

SELECT id, name, email, department, salary
FROM employees
ORDER BY id;


-- Support global ADMIN-created Finance department administrators
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;
ALTER TABLE users ADD CONSTRAINT users_role_check
CHECK (role IN ('ADMIN', 'IT_ADMIN', 'HR_ADMIN', 'FINANCE_ADMIN', 'EMPLOYEE'));
