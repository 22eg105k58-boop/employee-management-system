package com.example.employee_management.controller;

import com.example.employee_management.dto.EmployeeCreateRequest;
import com.example.employee_management.dto.EmployeeUpdateRequest;
import com.example.employee_management.entity.Employee;
import com.example.employee_management.service.EmployeeService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // =========================
    // CREATE EMPLOYEE
    // =========================

    @PreAuthorize(
            "hasAuthority('EMPLOYEE_CREATE') and " +
            "@departmentAuthorizationService.canCreateEmployee(authentication, #request.department)"
    )
    @PostMapping
    public Employee createEmployee(
            @Valid @RequestBody EmployeeCreateRequest request) {

        return employeeService.createEmployee(request);
    }

    // =========================
    // GET EMPLOYEES
    // =========================

    @PreAuthorize("hasAuthority('EMPLOYEE_READ')")
    @GetMapping
    public Page<Employee> getAllEmployees(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(required = false)
            String department) {

        Pageable pageable =
                PageRequest.of(page, size);

        return employeeService.getEmployees(
                department,
                pageable
        );
    }

    // =========================
    // GET EMPLOYEE BY ID
    // =========================

    @PreAuthorize(
            "hasAuthority('EMPLOYEE_READ') and " +
            "@departmentAuthorizationService.canAccessEmployee(authentication, #id)"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(
            @PathVariable Long id) {

        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================
    // UPDATE EMPLOYEE
    // =========================

    @PreAuthorize(
            "hasAuthority('EMPLOYEE_UPDATE') and " +
            "@departmentAuthorizationService.canAccessEmployee(authentication, #id)"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeUpdateRequest request) {

        try {

            Employee updatedEmployee =
                    employeeService.updateEmployee(id, request);

            return ResponseEntity.ok(updatedEmployee);

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }

    // =========================
    // DELETE EMPLOYEE
    // =========================

    @PreAuthorize(
            "hasAuthority('EMPLOYEE_DELETE') and " +
            "@departmentAuthorizationService.canAccessEmployee(authentication, #id)"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable Long id) {

        try {

            employeeService.deleteEmployee(id);

            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }
}