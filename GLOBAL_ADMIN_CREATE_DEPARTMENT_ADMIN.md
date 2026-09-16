# Global Admin: Create Department Admin

The global `ADMIN` can create IT, HR, or Finance department administrators from the Angular Admin Dashboard.

API: `POST /api/users/department-admin`

Access: `ADMIN` only.

Request fields: `name`, `email`, `department`, `username`, `password`.

The backend creates the employee profile and user account in one transaction, hashes the password with BCrypt, and maps the department to `IT_ADMIN`, `HR_ADMIN`, or `FINANCE_ADMIN`.

Before using Finance Admin, update the PostgreSQL role check constraint using `ROLE_SCOPE_UPDATE.sql`.
