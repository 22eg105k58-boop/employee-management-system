package com.example.employee_management.service;

import com.example.employee_management.entity.Employee;
import com.example.employee_management.entity.User;
import com.example.employee_management.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        return userRepository.save(user);
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