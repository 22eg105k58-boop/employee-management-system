package com.example.employee_management.security;

import com.example.employee_management.entity.Employee;
import com.example.employee_management.entity.User;
import com.example.employee_management.repository.EmployeeRepository;
import com.example.employee_management.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeUsers(
            UserRepository userRepository,
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Create ADMIN user
            if (userRepository.findByUsername("admin").isEmpty()) {

                User admin = new User();

                admin.setUsername("admin");
                admin.setPassword(
                        passwordEncoder.encode("admin123")
                );
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);
            }

            // Create EMPLOYEE user
            if (userRepository.findByUsername("harsha").isEmpty()) {

                Employee employee;

                if (employeeRepository.findAll().isEmpty()) {

                    employee = new Employee();

                    employee.setName("Harsha");
                    employee.setEmail("harsha@gmail.com");
                    employee.setDepartment("IT");
                    employee.setSalary(50000.0);

                    employee =
                            employeeRepository.save(employee);

                } else {

                    employee =
                            employeeRepository.findAll().get(0);
                }

                User employeeUser = new User();

                employeeUser.setUsername("harsha");
                employeeUser.setPassword(
                        passwordEncoder.encode("harsha123")
                );
                employeeUser.setRole(Role.EMPLOYEE);
                employeeUser.setEmployee(employee);

                userRepository.save(employeeUser);
            }
        };
    }
}