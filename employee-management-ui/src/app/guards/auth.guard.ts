import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';

export const authGuard: CanActivateFn = (route) => {

  const authService = inject(AuthService);
  const router = inject(Router);

  // Check whether the user is logged in
  if (!authService.isLoggedIn()) {
    return router.createUrlTree(['/login']);
  }

  // Get the role required by the route
  const requiredRole = route.data['role'];

  // Get the logged-in user's role
  const userRole = authService.getRole();

  // Check role
  if (requiredRole && userRole !== requiredRole) {

    if (userRole === 'ADMIN') {
      return router.createUrlTree(['/admin']);
    }

    if (userRole === 'EMPLOYEE') {
      return router.createUrlTree(['/employee']);
    }

    return router.createUrlTree(['/login']);
  }

  return true;
};