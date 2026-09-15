import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { EmployeeService } from '../../services/employee';
import { AuthService } from '../../services/auth';
import { Employee } from '../../models/employee';

@Component({
  selector: 'app-employee',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './employee.html',
  styleUrl: './employee.css'
})
export class EmployeeComponent implements OnInit {

  employee: Employee | null = null;
  username: string | null = '';
  errorMessage = '';
  profileMessage = '';

  currentPassword = '';
  newPassword = '';
  confirmPassword = '';
  passwordMessage = '';
  passwordError = '';

  constructor(
    private employeeService: EmployeeService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.username = this.authService.getUsername();
  }

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.errorMessage = '';

    this.employeeService.getMyProfile().subscribe({
      next: (employee: Employee) => {
        this.employee = employee;
        this.cdr.detectChanges();
      },
      error: (error: any) => {
        console.error('Error loading profile:', error);

        if (error.status === 401) {
          this.errorMessage = 'Your session has expired. Please log in again.';
        } else if (error.status === 403) {
          this.errorMessage = 'You do not have permission to view your profile.';
        } else {
          this.errorMessage = 'Unable to load your profile';
        }

        this.cdr.detectChanges();
      }
    });
  }

  updateProfile(): void {
    if (!this.employee) {
      return;
    }

    this.profileMessage = '';
    this.errorMessage = '';

    if (!this.employee.name.trim() || !this.employee.email.trim()) {
      this.errorMessage = 'Name and email are required';
      return;
    }

    this.employeeService.updateMyProfile({
      name: this.employee.name.trim(),
      email: this.employee.email.trim()
    }).subscribe({
      next: (employee: Employee) => {
        this.employee = employee;
        this.profileMessage = 'Profile updated successfully';
        this.cdr.detectChanges();
      },
      error: (error: any) => {
        console.error('Error updating profile:', error);

        if (error.status === 400) {
          this.errorMessage = 'Please enter valid profile details.';
        } else if (error.status === 403) {
          this.errorMessage = 'You do not have permission to update your profile.';
        } else {
          this.errorMessage = 'Unable to update profile';
        }

        this.cdr.detectChanges();
      }
    });
  }

  changePassword(): void {
    this.passwordMessage = '';
    this.passwordError = '';

    if (!this.currentPassword || !this.newPassword || !this.confirmPassword) {
      this.passwordError = 'All password fields are required';
      return;
    }

    if (this.newPassword.length < 8) {
      this.passwordError = 'New password must be at least 8 characters';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.passwordError = 'New password and confirmation do not match';
      return;
    }

    this.authService.changeMyPassword({
      currentPassword: this.currentPassword,
      newPassword: this.newPassword
    }).subscribe({
      next: (message: string) => {
        this.passwordMessage = message || 'Password changed successfully';
        this.currentPassword = '';
        this.newPassword = '';
        this.confirmPassword = '';
        this.cdr.detectChanges();
      },
      error: (error: any) => {
        console.error('Error changing password:', error);

        if (error.status === 400) {
          this.passwordError = 'Current password is incorrect or the new password is invalid.';
        } else if (error.status === 401) {
          this.passwordError = 'Your session has expired. Please log in again.';
        } else {
          this.passwordError = 'Unable to change password';
        }

        this.cdr.detectChanges();
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
