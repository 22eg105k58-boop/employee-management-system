import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { EmployeeList } from '../employee-list/employee-list';
import { EmployeeForm } from '../employee-form/employee-form';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [
    CommonModule,
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

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    this.username = this.authService.getUsername();
    this.role = this.authService.getRole();

    if (this.role === 'IT_ADMIN') {
      this.dashboardTitle = 'IT Admin Dashboard';
    } else if (this.role === 'HR_ADMIN') {
      this.dashboardTitle = 'HR Admin Dashboard';
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}