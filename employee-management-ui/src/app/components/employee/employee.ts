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

    this.employeeService
      .getMyProfile()
      .subscribe({

        next: (employee: Employee) => {

          console.log('Employee profile received:', employee);

          this.employee = employee;

          this.cdr.detectChanges();
        },

        error: (error: any) => {

          console.error(
            'Error loading profile:',
            error
          );

          this.errorMessage =
            'Unable to load your profile';

          this.cdr.detectChanges();
        }

      });
  }

  updateProfile(): void {

    if (!this.employee) {
      return;
    }

    const updateData = {
      name: this.employee.name,
      email: this.employee.email
    };

    this.employeeService
      .updateMyProfile(updateData)
      .subscribe({

        next: (employee: Employee) => {

          console.log(
            'Profile updated:',
            employee
          );

          this.employee = employee;

          this.cdr.detectChanges();

          alert('Profile updated successfully');
        },

        error: (error: any) => {

          console.error(
            'Error updating profile:',
            error
          );

          this.errorMessage =
            'Unable to update profile';

          this.cdr.detectChanges();
        }

      });
  }

  logout(): void {

    this.authService.logout();

    this.router.navigate(['/login']);
  }
}