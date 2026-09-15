import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';

import { Employee } from '../../models/employee';
import { EmployeeService } from '../../services/employee';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './employee-list.html',
  styleUrl: './employee-list.css'
})
export class EmployeeList implements OnInit, OnDestroy {

  employees: Employee[] = [];

  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;

  // Department filter
  department = '';
  errorMessage = '';
  isLoading = false;

  private employeeAddedSubscription!: Subscription;

  constructor(
    private employeeService: EmployeeService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    // Load first page
    this.loadEmployees();

    // Listen for new employees added from the form
    this.employeeAddedSubscription =
      this.employeeService.employeeAdded$.subscribe(() => {
        this.loadEmployees();
      });
  }

  // Load employees with pagination and filtering
  loadEmployees(): void {

    this.isLoading = true;
    this.errorMessage = '';

    this.employeeService
      .getEmployees(
        this.currentPage,
        this.pageSize,
        this.department
      )
      .subscribe({

        next: (data) => {

          this.isLoading = false;

          console.log('Employees received:', data);

          // Employee records are inside content
          this.employees = data.content;

          // Pagination information
          this.totalPages = data.totalPages;
          this.totalElements = data.totalElements;

          // Tell Angular to update the table
          this.cdr.detectChanges();
        },

        error: (error) => {

          this.isLoading = false;

          console.error(
            'Error loading employees:',
            error
          );

          if (error.status === 403) {
            this.errorMessage = 'You do not have permission to view employees.';
          } else {
            this.errorMessage = 'Unable to load employees.';
          }

          this.cdr.detectChanges();

        }

      });
  }

  // Go to next page
  nextPage(): void {

    if (this.currentPage < this.totalPages - 1) {

      this.currentPage++;

      this.loadEmployees();
    }
  }

  // Go to previous page
  previousPage(): void {

    if (this.currentPage > 0) {

      this.currentPage--;

      this.loadEmployees();
    }
  }

  // Filter employees by department
  filterByDepartment(): void {

    // Start from first page when applying a filter
    this.currentPage = 0;

    this.loadEmployees();
  }

  // Clear department filter
  clearFilter(): void {

    this.department = '';

    this.currentPage = 0;

    this.loadEmployees();
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

        console.error(
          'Error deleting employee:',
          error
        );

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

        console.error(
          'Error updating employee:',
          error
        );

        alert('Failed to update employee.');
      }

    });
  }

  // Clean up subscription
  ngOnDestroy(): void {

    this.employeeAddedSubscription.unsubscribe();
  }
}