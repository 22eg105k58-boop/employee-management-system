import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Subject, Observable } from 'rxjs';

import { Employee, EmployeePage } from '../models/employee';
import { EmployeeCreateRequest } from '../models/employee-create';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private apiUrl = 'http://localhost:8080/api/employees';

  private employeeAddedSubject = new Subject<void>();

  employeeAdded$ = this.employeeAddedSubject.asObservable();

  constructor(private http: HttpClient) {}

  // Get employees with pagination and optional department filter
  getEmployees(
    page: number = 0,
    size: number = 10,
    department: string = ''
  ): Observable<EmployeePage> {

    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    if (department.trim()) {
      params = params.set(
        'department',
        department.trim()
      );
    }

    return this.http.get<EmployeePage>(
      this.apiUrl,
      { params }
    );
  }

  getEmployeeById(id: number) {
    return this.http.get<Employee>(
      `${this.apiUrl}/${id}`
    );
  }

  createEmployee(employee: EmployeeCreateRequest) {
    return this.http.post<Employee>(
      this.apiUrl,
      employee
    );
  }

  updateEmployee(
    id: number,
    employee: Employee
  ) {
    return this.http.put<Employee>(
      `${this.apiUrl}/${id}`,
      employee
    );
  }

  deleteEmployee(id: number) {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }

  notifyEmployeeAdded(): void {
    this.employeeAddedSubject.next();
  }

  // Employee's own profile
  getMyProfile() {
    return this.http.get<Employee>(
      `${this.apiUrl}/me`
    );
  }

  // Employee can update only name and email
  updateMyProfile(data: {
    name: string;
    email: string;
  }) {
    return this.http.put<Employee>(
      `${this.apiUrl}/me`,
      data
    );
  }
}