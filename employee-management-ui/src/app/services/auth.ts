import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginResponse {
  token: string;
  username: string;
  role: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl =
    'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  login(
    username: string,
    password: string
  ): Observable<LoginResponse> {

    return this.http.post<LoginResponse>(
      `${this.apiUrl}/login`,
      {
        username,
        password
      }
    );
  }

  saveLoginData(response: LoginResponse): void {

    localStorage.setItem(
      'token',
      response.token
    );

    localStorage.setItem(
      'username',
      response.username
    );

    localStorage.setItem(
      'role',
      response.role
    );
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  getUsername(): string | null {
    return localStorage.getItem('username');
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}