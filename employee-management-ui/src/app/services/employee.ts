import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Subject } from 'rxjs';
import { Employee } from '../models/employee';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private apiUrl = 'http://localhost:8080/api/employees';

  private employeeAddedSubject = new Subject<void>();

  employeeAdded$ = this.employeeAddedSubject.asObservable();

  constructor(private http: HttpClient) {}

  getEmployees() {
    return this.http.get<Employee[]>(this.apiUrl);
  }

  getEmployeeById(id: number) {
    return this.http.get<Employee>(
      `${this.apiUrl}/${id}`
    );
  }

  createEmployee(employee: Employee) {
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