package com.example.employee_management.service;

import com.example.employee_management.entity.Employee;
import com.example.employee_management.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
public Employee saveEmployee(Employee employee) {
    return employeeRepository.save(employee);
}
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public Employee updateEmployee(Long id, Employee employee) {

        Employee existingEmployee =
                employeeRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Employee not found"));

        existingEmployee.setName(employee.getName());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setDepartment(employee.getDepartment());
        existingEmployee.setSalary(employee.getSalary());

        return employeeRepository.save(existingEmployee);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}