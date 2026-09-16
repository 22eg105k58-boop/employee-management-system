# Employee Management System - Role Scope Update

This version changes administrator scope to:

- ADMIN: global access to all departments and optional department filtering.
- IT_ADMIN: IT department only.
- HR_ADMIN: HR department only.
- EMPLOYEE: own profile only.

The backend enforces the department scope; Angular only improves the UI.

## Default accounts created by DataInitializer

- admin / admin123
- itadmin / itadmin123
- hradmin / hradmin123
- harsha / harsha123

If an account already exists, DataInitializer does not recreate it. Use ROLE_SCOPE_UPDATE.sql to correct an existing itadmin/hradmin role if required.

## Run

1. Start PostgreSQL and make sure employee_db and employee_app are available.
2. Set DB_PASSWORD and JWT_SECRET.
3. Start Spring Boot backend.
4. Start Angular frontend with `npm install` (if dependencies are not present) and `npm start`.
5. Test each account.

## Expected behavior

### ADMIN
GET /api/employees?page=0&size=10 -> all employees.
GET /api/employees?page=0&size=10&department=IT -> IT only.
GET /api/employees?page=0&size=10&department=HR -> HR only.

### IT_ADMIN
GET /api/employees?page=0&size=10 -> IT only.
A request with department=HR is still forced to IT by the backend.

### HR_ADMIN
GET /api/employees?page=0&size=10 -> HR only.
A request with department=IT is still forced to HR by the backend.

The department filter is shown in Angular only for ADMIN.
