package com.example.employee_management.service;
import com.example.employee_management.entity.Employee;
import java.util.Optional;
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
}