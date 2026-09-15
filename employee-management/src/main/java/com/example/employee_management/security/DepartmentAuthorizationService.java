package com.example.employee_management.security;

import com.example.employee_management.entity.Employee;
import com.example.employee_management.entity.User;
import com.example.employee_management.repository.EmployeeRepository;
import com.example.employee_management.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class DepartmentAuthorizationService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentAuthorizationService(
            UserRepository userRepository,
            EmployeeRepository employeeRepository) {

        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
    }

    public boolean canAccessEmployee(
            Authentication authentication,
            Long employeeId) {

        // Get currently logged-in user
        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Non-admin users are handled by the existing
        // permission-based security rules.
        if (currentUser.getRole() != Role.ADMIN) {
            return true;
        }

        // Get the admin's employee profile
        if (currentUser.getEmployee() == null) {
            return false;
        }

        String adminDepartment =
                currentUser.getEmployee().getDepartment();

        // Get target employee
        Employee targetEmployee =
                employeeRepository.findById(employeeId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employee not found"));

        String targetDepartment =
                targetEmployee.getDepartment();

        // Admin can access only the same department
        return adminDepartment != null
                && targetDepartment != null
                && adminDepartment.equalsIgnoreCase(targetDepartment);
    }
    public boolean canCreateEmployee(
        Authentication authentication,
        String department) {

    String username = authentication.getName();

    User currentUser = userRepository.findByUsername(username)
            .orElseThrow(() ->
                    new RuntimeException("User not found"));

    // Non-admin users keep existing behavior
    if (currentUser.getRole() != Role.ADMIN) {
        return true;
    }

    // Admin must have an employee profile
    if (currentUser.getEmployee() == null) {
        return false;
    }

    String adminDepartment =
            currentUser.getEmployee().getDepartment();

    // Admin can create only within their department
    return adminDepartment != null
            && department != null
            && adminDepartment.equalsIgnoreCase(department);
}
}