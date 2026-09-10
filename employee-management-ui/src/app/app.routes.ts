import { Routes } from '@angular/router';

import { LoginComponent } from './components/login/login';
import { AdminComponent } from './components/admin/admin';
import { EmployeeComponent } from './components/employee/employee';

import { authGuard } from './guards/auth.guard';

export const routes: Routes = [

  {
    path: 'login',
    component: LoginComponent
  },

  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [authGuard],
    data: {
      role: 'ADMIN'
    }
  },

  {
    path: 'employee',
    component: EmployeeComponent,
    canActivate: [authGuard],
    data: {
      role: 'EMPLOYEE'
    }
  },

  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  }
];