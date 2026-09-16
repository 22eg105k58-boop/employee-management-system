import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';

export const authGuard: CanActivateFn = (route) => {

  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    return router.createUrlTree(['/login']);
  }

  const requiredRoles = route.data['roles'] as string[] | undefined;
  const requiredRole = route.data['role'] as string | undefined;
  const userRole = authService.getRole();

  const allowedRoles = requiredRoles ?? (requiredRole ? [requiredRole] : []);

  if (allowedRoles.length > 0 &&
      (!userRole || !allowedRoles.includes(userRole))) {

    if (userRole === 'ADMIN' ||
        userRole === 'IT_ADMIN' ||
        userRole === 'HR_ADMIN') {
      return router.createUrlTree(['/admin']);
    }

    if (userRole === 'EMPLOYEE') {
      return router.createUrlTree(['/employee']);
    }

    return router.createUrlTree(['/login']);
  }

  return true;
};
