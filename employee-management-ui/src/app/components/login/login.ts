import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import {
  AuthService,
  LoginResponse
} from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {

  username = '';
  password = '';
  errorMessage = '';
  isLoading = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  login(): void {

    this.errorMessage = '';

    if (this.isLoading) {
      return;
    }

    if (!this.username.trim() ||
        !this.password.trim()) {

      this.errorMessage =
        'Username and password are required';

      return;
    }

    this.isLoading = true;

    this.authService
      .login(
        this.username,
        this.password
      )
      .subscribe({

        next: (response: LoginResponse) => {

          this.isLoading = false;

          this.authService.saveLoginData(
            response,
            this.username
          );

          const role =
            this.authService.getRole();

          if (role === 'ADMIN') {

            this.router.navigate(['/admin']);

          } else if (role === 'EMPLOYEE') {

            this.router.navigate(['/employee']);

          } else {

            this.errorMessage =
              'Invalid user role';
          }
        },

        error: (error) => {

          console.error(
            'Login error:',
            error
          );

          if (error.status === 401) {

            this.errorMessage =
              'Invalid username or password';

          } else {

            this.errorMessage =
              'Unable to connect to server';
          }
        }
      });
  }
}