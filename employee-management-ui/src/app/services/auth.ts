import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
}

export interface PasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/api/auth';
  private usersApiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${this.apiUrl}/login`,
      { username, password }
    );
  }

  refreshToken(): Observable<LoginResponse> {
    const refreshToken = this.getRefreshToken();

    return this.http.post<LoginResponse>(
      `${this.apiUrl}/refresh`,
      { refreshToken }
    );
  }

  changeMyPassword(request: PasswordChangeRequest): Observable<string> {
    return this.http.put(
      `${this.usersApiUrl}/me/password`,
      request,
      { responseType: 'text' }
    );
  }

  saveLoginData(response: LoginResponse, username: string): void {
    localStorage.setItem('token', response.accessToken);
    localStorage.setItem('refreshToken', response.refreshToken);
    localStorage.setItem('username', username);

    const role = this.getRoleFromToken(response.accessToken);
    if (role) {
      localStorage.setItem('role', role);
    }
  }

  saveRefreshData(response: LoginResponse): void {
    localStorage.setItem('token', response.accessToken);
    localStorage.setItem('refreshToken', response.refreshToken);

    const role = this.getRoleFromToken(response.accessToken);
    if (role) {
      localStorage.setItem('role', role);
    }
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getRefreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  getUsername(): string | null {
    return localStorage.getItem('username');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
  }

  private getRoleFromToken(token: string): string | null {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) {
        return null;
      }

      let base64 = parts[1]
        .replace(/-/g, '+')
        .replace(/_/g, '/');

      while (base64.length % 4 !== 0) {
        base64 += '=';
      }

      const claims = JSON.parse(atob(base64));
      return claims.role ?? null;
    } catch {
      return null;
    }
  }
}
