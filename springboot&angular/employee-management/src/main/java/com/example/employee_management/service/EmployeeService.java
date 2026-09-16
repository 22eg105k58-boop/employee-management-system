package com.example.employee_management.service;

import com.example.employee_management.dto.EmployeeCreateRequest;
import com.example.employee_management.dto.EmployeeUpdateRequest;
import com.example.employee_management.entity.Employee;
import com.example.employee_management.entity.User;
import com.example.employee_management.repository.EmployeeRepository;
import com.example.employee_management.repository.UserRepository;
import com.example.employee_management.security.AuditAction;
import com.example.employee_management.security.Role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AuditService auditService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            AuditService auditService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.employeeRepository = employeeRepository;
        this.auditService = auditService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // CREATE EMPLOYEE
    // =========================

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee createEmployee(EmployeeCreateRequest request) {

        String newUsername = request.getUsername().trim();

        if (userRepository.findByUsername(newUsername).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        Employee employee = new Employee();

        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setDepartment(request.getDepartment());
        employee.setSalary(request.getSalary());

        Employee savedEmployee = employeeRepository.save(employee);

        // Every employee created through the employee-management form
        // receives a normal EMPLOYEE login account. Department-admin
        // permissions are never granted through this form.
        User employeeUser = new User();
        employeeUser.setUsername(newUsername);
        employeeUser.setPassword(passwordEncoder.encode(request.getPassword()));
        employeeUser.setRole(Role.EMPLOYEE);
        employeeUser.setEmployee(savedEmployee);
        employeeUser.setActive(true);
        userRepository.save(employeeUser);

        String username = getCurrentUsername();

        auditService.log(
                username,
                AuditAction.CREATE_EMPLOYEE,
                savedEmployee.getId(),
                "Created employee: " + savedEmployee.getName()
                        + " with login username: " + newUsername
        );

        return savedEmployee;
    }

    // =========================
    // GET ALL EMPLOYEES
    // =========================

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // =========================
    // PAGINATION + DEPARTMENT
    // =========================

    public Page<Employee> getEmployees(
            String department,
            Pageable pageable) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username = authentication.getName();

        User currentUser =
                userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException("User not found"));

        /*
         * ADMIN is global and may optionally filter by department.
         */
        if (currentUser.getRole() == Role.ADMIN) {
            if (department != null && !department.isBlank()) {
                return employeeRepository.findByDepartmentIgnoreCase(
                        department.trim(),
                        pageable
                );
            }

            return employeeRepository.findAll(pageable);
        }

        /*
         * IT_ADMIN and HR_ADMIN are always restricted to their own department.
         * A department query parameter cannot be used to bypass this scope.
         */
        if (currentUser.getRole() == Role.IT_ADMIN) {
            return employeeRepository.findByDepartmentIgnoreCase(
                    "IT",
                    pageable
            );
        }

        if (currentUser.getRole() == Role.HR_ADMIN) {
            return employeeRepository.findByDepartmentIgnoreCase(
                    "HR",
                    pageable
            );
        }

        return employeeRepository.findAll(pageable);
    }

    // =========================
    // GET EMPLOYEE BY ID
    // =========================

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    // =========================
    // UPDATE EMPLOYEE
    // =========================

    public Employee updateEmployee(
            Long id,
            EmployeeUpdateRequest request) {

        Employee existingEmployee =
                employeeRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employee not found"));

        String oldName =
                existingEmployee.getName();

        String oldEmail =
                existingEmployee.getEmail();

        String oldDepartment =
                existingEmployee.getDepartment();

        double oldSalary =
                existingEmployee.getSalary();

        existingEmployee.setName(request.getName());
        existingEmployee.setEmail(request.getEmail());
        existingEmployee.setDepartment(request.getDepartment());
        existingEmployee.setSalary(request.getSalary());

        Employee updatedEmployee =
                employeeRepository.save(existingEmployee);

        String details =
                "Updated employee. " +
                "Name: " + oldName +
                " → " + updatedEmployee.getName() +

                ", Email: " + oldEmail +
                " → " + updatedEmployee.getEmail() +

                ", Department: " + oldDepartment +
                " → " + updatedEmployee.getDepartment() +

                ", Salary: " + oldSalary +
                " → " + updatedEmployee.getSalary();

        String username = getCurrentUsername();

        auditService.log(
                username,
                AuditAction.UPDATE_EMPLOYEE,
                updatedEmployee.getId(),
                details
        );

        return updatedEmployee;
    }

    // =========================
    // DELETE EMPLOYEE
    // =========================

    public void deleteEmployee(Long id) {

        Employee employee =
                employeeRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employee not found"));

        employeeRepository.delete(employee);

        String username = getCurrentUsername();

        auditService.log(
                username,
                AuditAction.DELETE_EMPLOYEE,
                id,
                "Deleted employee: " + employee.getName()
        );
    }

    // =========================
    // CURRENT USERNAME
    // =========================

    private String getCurrentUsername() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication.getName();
    }
}