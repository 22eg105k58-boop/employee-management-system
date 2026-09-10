package com.example.employee_management.service;

import com.example.employee_management.dto.LoginRequest;
import com.example.employee_management.dto.LoginResponse;
import com.example.employee_management.entity.User;
import com.example.employee_management.repository.UserRepository;
import com.example.employee_management.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtService.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        return new LoginResponse(
                token,
                user.getUsername(),
                user.getRole().name()
        );
    }
}