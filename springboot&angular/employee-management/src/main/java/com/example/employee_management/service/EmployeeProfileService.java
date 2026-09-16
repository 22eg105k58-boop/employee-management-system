package com.example.employee_management.service;

import com.example.employee_management.dto.EmployeeProfileUpdateRequest;
import com.example.employee_management.entity.Employee;
import org.springframework.stereotype.Service;

@Service
public class EmployeeProfileService {

    private final UserService userService;
    private final EmployeeService employeeService;

    public EmployeeProfileService(
            UserService userService,
            EmployeeService employeeService) {

        this.userService = userService;
        this.employeeService = employeeService;
    }

    public Employee getMyProfile(String username) {

        return userService.getEmployeeForUser(username);
    }

    public Employee updateMyProfile(
            String username,
            EmployeeProfileUpdateRequest request) {

        Employee employee =
                userService.getEmployeeForUser(username);

        employee.setName(request.getName());
        employee.setEmail(request.getEmail());

        return employeeService.saveEmployee(employee);
    }
}