package com.example.employee_management.controller;

import com.example.employee_management.dto.LoginRequest;
import com.example.employee_management.dto.LoginResponse;
import com.example.employee_management.dto.RefreshTokenRequest;
import com.example.employee_management.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request) {

        return authService.login(request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        LoginResponse response =
                authService.refreshAccessToken(
                        request.getRefreshToken()
                );

        return ResponseEntity.ok(response);
    }
}