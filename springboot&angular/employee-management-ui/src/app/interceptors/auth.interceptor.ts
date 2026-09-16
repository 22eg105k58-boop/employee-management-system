import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  // Login and refresh are sent without the access-token header.
  const isLoginRequest = req.url.includes('/api/auth/login');
  const isRefreshRequest = req.url.includes('/api/auth/refresh');

  if (isLoginRequest || isRefreshRequest) {
    return next(req);
  }

  const token = authService.getToken();

  const requestWithToken = token
    ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      })
    : req;

  return next(requestWithToken).pipe(
    catchError((error: HttpErrorResponse) => {
      // If the request was unauthorized, try the refresh token once.
      // Do not attempt refresh for authentication/refresh endpoints.
      if (error.status !== 401 || !authService.getRefreshToken()) {
        return throwError(() => error);
      }

      return authService.refreshToken().pipe(
        switchMap((response) => {
          authService.saveRefreshData(response);

          const retryRequest = req.clone({
            setHeaders: {
              Authorization: `Bearer ${response.accessToken}`
            }
          });

          return next(retryRequest);
        }),
        catchError((refreshError) => {
          authService.logout();
          return throwError(() => refreshError);
        })
      );
    })
  );
};
