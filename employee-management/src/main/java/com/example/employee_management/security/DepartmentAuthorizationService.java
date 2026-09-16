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

    /**
     * ADMIN is global. department administrators are restricted to their
     * configured department. Other roles are handled by permissions.
     */
    public boolean canAccessEmployee(
            Authentication authentication,
            Long employeeId) {

        User currentUser = getCurrentUser(authentication);
        String allowedDepartment = getAllowedDepartment(currentUser);

        // Global ADMIN can access every department.
        if (allowedDepartment == null) {
            return currentUser.getRole() == Role.ADMIN;
        }

        Employee targetEmployee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return sameDepartment(
                allowedDepartment,
                targetEmployee.getDepartment());
    }

    public boolean canCreateEmployee(
            Authentication authentication,
            String department) {

        User currentUser = getCurrentUser(authentication);
        String allowedDepartment = getAllowedDepartment(currentUser);

        // Global ADMIN can create an employee in any department.
        if (currentUser.getRole() == Role.ADMIN) {
            return true;
        }

        // Department administrators can create only in their own department.
        return allowedDepartment != null
                && sameDepartment(allowedDepartment, department);
    }

    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String getAllowedDepartment(User user) {
        return switch (user.getRole()) {
            case IT_ADMIN -> "IT";
            case HR_ADMIN -> "HR";
            case FINANCE_ADMIN -> "Finance";
            case ADMIN, EMPLOYEE -> null;
        };
    }

    private boolean sameDepartment(String first, String second) {
        return first != null
                && second != null
                && first.equalsIgnoreCase(second);
    }
}
