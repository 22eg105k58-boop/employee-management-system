package com.example.employee_management.service;

import com.example.employee_management.dto.LoginRequest;
import com.example.employee_management.dto.LoginResponse;
import com.example.employee_management.entity.RefreshToken;
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
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("Invalid username or password"));

        if (!user.isActive()) {
            throw new RuntimeException("User account is deactivated");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid username or password");
        }

        var permissions = user.getRole()
                .getPermissions()
                .stream()
                .map(Enum::name)
                .toList();

        String accessToken = jwtService.generateAccessToken(
                user.getUsername(),
                user.getRole(),
                permissions
        );

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(
                        user.getUsername()
                );

        return new LoginResponse(
                accessToken,
                refreshToken.getToken()
        );
    }
    public LoginResponse refreshAccessToken(String refreshTokenValue) {

    // Validate and rotate the old refresh token
    RefreshToken newRefreshToken =
            refreshTokenService.rotateRefreshToken(
                    refreshTokenValue
            );

    User user = newRefreshToken.getUser();

    // Get current permissions
    var permissions = user.getRole()
            .getPermissions()
            .stream()
            .map(Enum::name)
            .toList();

    // Generate a new short-lived access token
    String newAccessToken = jwtService.generateAccessToken(
            user.getUsername(),
            user.getRole(),
            permissions
    );

    return new LoginResponse(
            newAccessToken,
            newRefreshToken.getToken()
    );
}
}