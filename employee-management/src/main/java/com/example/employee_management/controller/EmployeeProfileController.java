package com.example.employee_management.controller;

import com.example.employee_management.dto.EmployeeProfileUpdateRequest;
import com.example.employee_management.entity.Employee;
import com.example.employee_management.service.EmployeeProfileService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/employees/me")
public class EmployeeProfileController {

    private final EmployeeProfileService employeeProfileService;

    public EmployeeProfileController(
            EmployeeProfileService employeeProfileService) {

        this.employeeProfileService = employeeProfileService;
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping
    public ResponseEntity<Employee> getMyProfile(
            Authentication authentication) {

        String username = authentication.getName();

        Employee employee =
                employeeProfileService.getMyProfile(username);

        return ResponseEntity.ok(employee);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping
    public ResponseEntity<Employee> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody EmployeeProfileUpdateRequest request) {

        String username = authentication.getName();

        Employee updatedEmployee =
                employeeProfileService.updateMyProfile(
                        username,
                        request
                );

        return ResponseEntity.ok(updatedEmployee);
    }
}