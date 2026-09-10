import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, LoginResponse } from '../../services/auth';

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

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  login(): void {

    this.errorMessage = '';

    this.authService.login(
      this.username,
      this.password
    ).subscribe({

      next: (response: LoginResponse) => {

        this.authService.saveLoginData(response);

        if (response.role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/employee']);
        }
      },

      error: () => {
        this.errorMessage =
          'Invalid username or password';
      }
    });
  }
}