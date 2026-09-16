import { Employee } from './employee';

export interface EmployeeCreateRequest extends Employee {
  username: string;
  password: string;
}
