-- No new column is required for employee login accounts.
-- The existing users table already stores username, BCrypt password,
-- role, active status and employee_id.
--
-- The updated application creates a User row automatically when
-- POST /api/employees is used. The password is BCrypt-hashed by Spring Security.
-- New employee accounts are always created with role = 'EMPLOYEE'.
--
-- If your database still has the old role constraint, run:
ALTER TABLE users
DROP CONSTRAINT IF EXISTS users_role_check;

ALTER TABLE users
ADD CONSTRAINT users_role_check
CHECK (role IN ('ADMIN', 'IT_ADMIN', 'HR_ADMIN', 'EMPLOYEE'));
