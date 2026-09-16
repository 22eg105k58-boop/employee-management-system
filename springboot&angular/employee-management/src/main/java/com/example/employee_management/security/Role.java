package com.example.employee_management.security;

import java.util.Set;

public enum Role {

    // Global administrator: can manage employees across all departments.
    ADMIN(Set.of(
            Permission.EMPLOYEE_CREATE,
            Permission.EMPLOYEE_READ,
            Permission.EMPLOYEE_UPDATE,
            Permission.EMPLOYEE_DELETE
    )),

    // Department-scoped administrator: IT only.
    IT_ADMIN(Set.of(
            Permission.EMPLOYEE_CREATE,
            Permission.EMPLOYEE_READ,
            Permission.EMPLOYEE_UPDATE,
            Permission.EMPLOYEE_DELETE
    )),

    // Department-scoped administrator: HR only.
    HR_ADMIN(Set.of(
            Permission.EMPLOYEE_CREATE,
            Permission.EMPLOYEE_READ,
            Permission.EMPLOYEE_UPDATE,
            Permission.EMPLOYEE_DELETE
    )),

    EMPLOYEE(Set.of(
            Permission.PROFILE_READ,
            Permission.PROFILE_UPDATE
    ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}
