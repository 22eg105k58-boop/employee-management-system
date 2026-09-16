package com.example.employee_management.service;

import com.example.employee_management.entity.RefreshToken;
import com.example.employee_management.entity.User;
import com.example.employee_management.repository.RefreshTokenRepository;
import com.example.employee_management.security.JwtService;
import com.example.employee_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            JwtService jwtService) {

        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public RefreshToken createRefreshToken(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!user.isActive()) {
            throw new RuntimeException(
                    "User account is deactivated");
        }

        // Remove previous refresh tokens
        refreshTokenRepository.deleteByUserId(user.getId());

        // Generate new refresh token
        String token = jwtService.generateRefreshToken(username);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken(token);
        refreshToken.setUser(user);

        refreshToken.setExpiryDate(
                LocalDateTime.now()
                        .plusSeconds(refreshTokenExpiration / 1000)
        );

        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token) {

        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Refresh token not found"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException(
                    "Refresh token has been revoked");
        }

        if (refreshToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Refresh token has expired");
        }

        User user = refreshToken.getUser();

        if (!user.isActive()) {
            throw new RuntimeException(
                    "User account is deactivated");
        }

        if (!jwtService.isTokenValid(token)) {
            throw new RuntimeException(
                    "Invalid refresh token");
        }

        if (!"refresh".equals(
                jwtService.extractTokenType(token))) {

            throw new RuntimeException(
                    "Invalid token type");
        }

        return refreshToken;
    }

    @Transactional
    public void revokeToken(String token) {

        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Refresh token not found"));

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }
    @Transactional
public RefreshToken rotateRefreshToken(String oldToken) {

    // First validate the existing refresh token
    RefreshToken existingToken = verifyRefreshToken(oldToken);

    User user = existingToken.getUser();

    // Revoke the old refresh token
    existingToken.setRevoked(true);
    refreshTokenRepository.save(existingToken);

    // Generate a completely new refresh token
    String newToken = jwtService.generateRefreshToken(
            user.getUsername()
    );

    RefreshToken newRefreshToken = new RefreshToken();

    newRefreshToken.setToken(newToken);
    newRefreshToken.setUser(user);

    newRefreshToken.setExpiryDate(
            LocalDateTime.now()
                    .plusSeconds(refreshTokenExpiration / 1000)
    );

    newRefreshToken.setRevoked(false);

    return refreshTokenRepository.save(newRefreshToken);
}
}