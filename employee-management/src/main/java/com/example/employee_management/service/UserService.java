package com.example.employee_management.service;

import com.example.employee_management.entity.Employee;
import com.example.employee_management.entity.User;
import com.example.employee_management.repository.EmployeeRepository;
import com.example.employee_management.security.Role;
import com.example.employee_management.dto.DepartmentAdminCreateRequest;
import org.springframework.transaction.annotation.Transactional;
import com.example.employee_management.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepository;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmployeeRepository employeeRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.employeeRepository = employeeRepository;
    }

    public User createUser(User user) {

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        return userRepository.save(user);
    }

    @Transactional
    public User createDepartmentAdmin(DepartmentAdminCreateRequest request) {
        String username = request.getUsername().trim();
        String department = request.getDepartment().trim();

        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        Role role = switch (department.toLowerCase()) {
            case "it" -> Role.IT_ADMIN;
            case "hr" -> Role.HR_ADMIN;
            case "finance" -> Role.FINANCE_ADMIN;
            default -> throw new RuntimeException(
                    "Unsupported department. Use IT, HR, or Finance");
        };

        Employee employee = new Employee();
        employee.setName(request.getName().trim());
        employee.setEmail(request.getEmail().trim());
        employee.setDepartment(department);
        employee.setSalary(0.0);
        Employee savedEmployee = employeeRepository.save(employee);

        User admin = new User();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setRole(role);
        admin.setEmployee(savedEmployee);
        admin.setActive(true);

        return userRepository.save(admin);
    }

    public Employee getEmployeeForUser(String username) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (user.getEmployee() == null) {
            throw new RuntimeException(
                    "No employee profile linked to this user");
        }

        return user.getEmployee();
    }

    public User deactivateUser(Long id) {

        User user = userRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setActive(false);

        return userRepository.save(user);
    }
    public void changePassword(
        String username,
        String currentPassword,
        String newPassword) {

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (!user.isActive()) {
        throw new RuntimeException("User account is deactivated");
    }

    if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
        throw new RuntimeException("Current password is incorrect");
    }

    user.setPassword(passwordEncoder.encode(newPassword));

    userRepository.save(user);
}
}