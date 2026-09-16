import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { EmployeeList } from '../employee-list/employee-list';
import { EmployeeForm } from '../employee-form/employee-form';
import { AuthService } from '../../services/auth';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    EmployeeList,
    EmployeeForm
  ],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class AdminComponent {

  username: string | null = '';
  role: string | null = '';
  dashboardTitle = 'Admin Dashboard';

  adminName = '';
  adminEmail = '';
  adminUsername = '';
  adminPassword = '';
  adminDepartment = 'Finance';
  adminMessage = '';
  adminError = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private http: HttpClient
  ) {
    this.username = this.authService.getUsername();
    this.role = this.authService.getRole();

    if (this.role === 'IT_ADMIN') {
      this.dashboardTitle = 'IT Admin Dashboard';
    } else if (this.role === 'HR_ADMIN') {
      this.dashboardTitle = 'HR Admin Dashboard';
    } else if (this.role === 'FINANCE_ADMIN') {
      this.dashboardTitle = 'Finance Admin Dashboard';
    }
  }

  createDepartmentAdmin(): void {
    this.adminMessage = '';
    this.adminError = '';

    this.http.post(
      'http://localhost:8080/api/users/department-admin',
      {
        name: this.adminName,
        email: this.adminEmail,
        department: this.adminDepartment,
        username: this.adminUsername,
        password: this.adminPassword
      },
      { responseType: 'text' }
    ).subscribe({
      next: (message) => {
        this.adminMessage = message;
        this.adminName = '';
        this.adminEmail = '';
        this.adminUsername = '';
        this.adminPassword = '';
        this.adminDepartment = 'Finance';
      },
      error: (error) => {
        this.adminError = error.error?.error || 'Failed to create department admin.';
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}