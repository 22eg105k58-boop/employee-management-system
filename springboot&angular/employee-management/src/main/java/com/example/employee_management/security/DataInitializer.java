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

            // Global administrator. No employee profile is required for ADMIN.
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }

            // IT department administrator.
            if (userRepository.findByUsername("itadmin").isEmpty()) {
                Employee itEmployee = findOrCreateEmployee(
                        employeeRepository,
                        "IT",
                        "IT Administrator",
                        "itadmin@example.com",
                        60000.0
                );

                User itAdmin = new User();
                itAdmin.setUsername("itadmin");
                itAdmin.setPassword(passwordEncoder.encode("itadmin123"));
                itAdmin.setRole(Role.IT_ADMIN);
                itAdmin.setEmployee(itEmployee);
                userRepository.save(itAdmin);
            }

            // HR department administrator.
            if (userRepository.findByUsername("hradmin").isEmpty()) {
                Employee hrEmployee = findOrCreateEmployee(
                        employeeRepository,
                        "HR",
                        "HR Administrator",
                        "hradmin@example.com",
                        60000.0
                );

                User hrAdmin = new User();
                hrAdmin.setUsername("hradmin");
                hrAdmin.setPassword(passwordEncoder.encode("hradmin123"));
                hrAdmin.setRole(Role.HR_ADMIN);
                hrAdmin.setEmployee(hrEmployee);
                userRepository.save(hrAdmin);
            }

            // Normal employee.
            if (userRepository.findByUsername("harsha").isEmpty()) {
                Employee employee = findOrCreateEmployee(
                        employeeRepository,
                        "IT",
                        "Harsha",
                        "harsha@gmail.com",
                        50000.0
                );

                User employeeUser = new User();
                employeeUser.setUsername("harsha");
                employeeUser.setPassword(passwordEncoder.encode("harsha123"));
                employeeUser.setRole(Role.EMPLOYEE);
                employeeUser.setEmployee(employee);
                userRepository.save(employeeUser);
            }
        };
    }

    private Employee findOrCreateEmployee(
            EmployeeRepository employeeRepository,
            String department,
            String name,
            String email,
            double salary) {

        return employeeRepository.findAll().stream()
                .filter(employee -> employee.getEmail() != null
                        && employee.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseGet(() -> {
                    Employee employee = new Employee();
                    employee.setName(name);
                    employee.setEmail(email);
                    employee.setDepartment(department);
                    employee.setSalary(salary);
                    return employeeRepository.save(employee);
                });
    }
}
