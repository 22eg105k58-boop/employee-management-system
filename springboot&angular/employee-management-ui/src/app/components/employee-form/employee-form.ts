import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { EmployeeCreateRequest } from '../../models/employee-create';
import { EmployeeService } from '../../services/employee';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-employee-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './employee-form.html',
  styleUrl: './employee-form.css'
})
export class EmployeeForm {

  role: string | null = '';
  fixedDepartment: string | null = null;

  employee: EmployeeCreateRequest = this.emptyEmployee();

  constructor(
    private employeeService: EmployeeService,
    private authService: AuthService
  ) {
    this.role = this.authService.getRole();
    this.applyDepartmentScope();
  }

  private emptyEmployee(): EmployeeCreateRequest {
    return {
      name: '',
      email: '',
      department: '',
      salary: 0,
      username: '',
      password: ''
    };
  }

  private applyDepartmentScope(): void {
    if (this.role === 'IT_ADMIN') {
      this.fixedDepartment = 'IT';
      this.employee.department = 'IT';
    } else if (this.role === 'HR_ADMIN') {
      this.fixedDepartment = 'HR';
      this.employee.department = 'HR';
    }
  }

  addEmployee(): void {
    if (this.fixedDepartment) {
      this.employee.department = this.fixedDepartment;
    }

    this.employeeService.createEmployee(this.employee).subscribe({
      next: (data) => {
        console.log('Employee added successfully:', data);
        alert('Employee and login account created successfully!');

        this.employee = this.emptyEmployee();
        this.applyDepartmentScope();
        this.employeeService.notifyEmployeeAdded();
      },
      error: (error) => {
        console.error('Error adding employee:', error);

        if (error.status === 400 && error.error?.error) {
          alert(error.error.error);
        } else {
          alert('Failed to add employee.');
        }
      }
    });
  }
}
