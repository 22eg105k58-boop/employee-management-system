package com.example.employee_management.controller;

import com.example.employee_management.entity.Employee;
import com.example.employee_management.service.EmployeeService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Employee createEmployee(
            @Valid @RequestBody Employee employee) {

        return employeeService.createEmployee(employee);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Employee> getAllEmployees() {

        return employeeService.getAllEmployees();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(
            @PathVariable Long id) {

        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable Long id,
            @RequestBody Employee employee) {

        try {

            Employee updatedEmployee =
                    employeeService.updateEmployee(id, employee);

            return ResponseEntity.ok(updatedEmployee);

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
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