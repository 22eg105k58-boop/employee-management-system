import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';

import { Employee } from '../../models/employee';
import { EmployeeService } from '../../services/employee';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './employee-list.html',
  styleUrl: './employee-list.css'
})
export class EmployeeList implements OnInit, OnDestroy {

  employees: Employee[] = [];

  private employeeAddedSubscription!: Subscription;

  constructor(
    private employeeService: EmployeeService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    // Load employees when the page starts
    this.loadEmployees();

    // Listen for new employees added from the form
    this.employeeAddedSubscription =
      this.employeeService.employeeAdded$.subscribe(() => {
        this.loadEmployees();
      });
  }

  // Get all employees from Spring Boot
  loadEmployees(): void {

    this.employeeService.getEmployees().subscribe({

      next: (data) => {

        console.log('Employees received:', data);

        this.employees = data;

        // Tell Angular to update the table
        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error('Error loading employees:', error);

      }

    });
  }

  // Delete employee
  deleteEmployee(id: number): void {

    const confirmed = confirm(
      'Are you sure you want to delete this employee?'
    );

    if (!confirmed) {
      return;
    }

    this.employeeService.deleteEmployee(id).subscribe({

      next: () => {

        alert('Employee deleted successfully!');

        // Refresh employee list
        this.loadEmployees();
      },

      error: (error) => {

        console.error('Error deleting employee:', error);

        alert('Failed to delete employee.');
      }

    });
  }

  // Edit employee
  editEmployee(employee: Employee): void {

    const name = prompt(
      'Enter employee name:',
      employee.name
    );

    if (name === null) {
      return;
    }

    const email = prompt(
      'Enter employee email:',
      employee.email
    );

    if (email === null) {
      return;
    }

    const department = prompt(
      'Enter employee department:',
      employee.department
    );

    if (department === null) {
      return;
    }

    const salaryInput = prompt(
      'Enter employee salary:',
      employee.salary.toString()
    );

    if (salaryInput === null) {
      return;
    }

    const updatedEmployee: Employee = {
      name: name,
      email: email,
      department: department,
      salary: Number(salaryInput)
    };

    this.employeeService.updateEmployee(
      employee.id!,
      updatedEmployee
    ).subscribe({

      next: () => {

        alert('Employee updated successfully!');

        // Refresh employee list
        this.loadEmployees();
      },

      error: (error) => {

        console.error('Error updating employee:', error);

        alert('Failed to update employee.');
      }

    });
  }

  // Clean up subscription
  ngOnDestroy(): void {

    this.employeeAddedSubscription.unsubscribe();

  }
}