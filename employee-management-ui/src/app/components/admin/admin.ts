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

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    this.username = this.authService.getUsername();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}