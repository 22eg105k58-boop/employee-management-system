package com.example.employee_management.controller;

import com.example.employee_management.entity.User;
import com.example.employee_management.dto.DepartmentAdminCreateRequest;
import com.example.employee_management.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.employee_management.dto.PasswordChangeRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/department-admin")
    public ResponseEntity<String> createDepartmentAdmin(
            @Valid @RequestBody DepartmentAdminCreateRequest request) {

        User createdUser = userService.createDepartmentAdmin(request);

        return ResponseEntity.ok(
                "Department admin created successfully: " + createdUser.getUsername());
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateUser(
            @PathVariable Long id) {

        userService.deactivateUser(id);

        return ResponseEntity.ok(
                "User deactivated successfully"
        );
    }
    @PutMapping("/me/password")
public ResponseEntity<String> changePassword(
        @Valid @RequestBody PasswordChangeRequest request,
        Authentication authentication) {

    userService.changePassword(
            authentication.getName(),
            request.getCurrentPassword(),
            request.getNewPassword()
    );

    return ResponseEntity.ok("Password changed successfully");
}
}