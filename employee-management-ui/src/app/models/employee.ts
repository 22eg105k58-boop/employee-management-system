export interface Employee {
  id?: number;
  name: string;
  email: string;
  department: string;
  salary: number;
}
export interface EmployeePage {
  content: Employee[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}