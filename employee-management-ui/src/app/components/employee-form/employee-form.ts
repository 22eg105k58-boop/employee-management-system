import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Employee } from '../../models/employee';
import { EmployeeService } from '../../services/employee';

@Component({
  selector: 'app-employee-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './employee-form.html',
  styleUrl: './employee-form.css'
})
export class EmployeeForm {

  employee: Employee = {
    name: '',
    email: '',
    department: '',
    salary: 0
  };

  constructor(private employeeService: EmployeeService) {}

  addEmployee(): void {

    this.employeeService.createEmployee(this.employee).subscribe({
      next: (data) => {
  console.log('Employee added successfully:', data);

  alert('Employee added successfully!');

  this.employee = {
    name: '',
    email: '',
    department: '',
    salary: 0
  };

  this.employeeService.notifyEmployeeAdded();
},

      error: (error) => {
        console.error('Error adding employee:', error);
        alert('Failed to add employee.');
      }
    });
  }
}