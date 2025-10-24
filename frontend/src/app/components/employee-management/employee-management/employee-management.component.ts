import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { LangService } from '../../../services/lang/lang.service';
import { EmployeeService } from '../../../services/employees/employee.service';
import { Subscription } from 'rxjs';
import { TranslateModule } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';
import { EmployeeResponse } from '../../../responses/responses';
import {
  NewEmployeeRequest,
  RemovedEmployeeRequest,
  UpdatedEmployeeRequest,
} from '../../../requests/requests';
import { EmploymentType } from '../../../enums/employment-type.enum';
import { FormsModule } from '@angular/forms';
import { TranslationHelperService } from '../../../services/translation-helper/translation-helper.service';
import { AlertService } from '../../../services/alert/alert.service';

@Component({
  selector: 'app-employee-management',
  standalone: true,
  imports: [TranslateModule, CommonModule, FormsModule],
  templateUrl: './employee-management.component.html',
  styleUrl: './employee-management.component.scss',
})
export class EmployeeManagementComponent implements OnInit {
  newEmployee: NewEmployeeRequest = {
    first_name: '',
    last_name: '',
    email: '',
    password: '',
    phone: '',
    date_of_birth: null,
    job: '',
    employment_type: null,
    is_student: false,
    hourly_wage: 0,
  };
  employmentTypes: EmploymentType[] = [
    EmploymentType.MANDATE_CONTRACT,
    EmploymentType.PERMANENT,
  ];
  errorMessages: { [key: string]: string } = {};
  employees: EmployeeResponse[] = [];

  updatedEmployee: UpdatedEmployeeRequest | null = null;

  isAdding: boolean = false;
  isEditing: boolean = false;

  languageChangeSubscription: Subscription;

  constructor(
    private authenticationService: AuthenticationService,
    private langService: LangService,
    private employeeService: EmployeeService,
    private translationHelper: TranslationHelperService,
    private alertService: AlertService
  ) {
    this.languageChangeSubscription =
      this.langService.languageChanged$.subscribe(() => {
        this.hideErrorMessages();
      });
  }

  ngOnInit(): void {
    if (this.isManager()) this.loadEmployees();
  }

  private loadEmployees(): void {
    this.employeeService.getEmployees().subscribe({
      next: (data: EmployeeResponse[]) =>
        (this.employees = data.sort((a, b) =>
          a.last_name.localeCompare(b.last_name)
        )),
      error: (error) => console.log('Error loading employees', error),
    });
  }

  protected isManager(): boolean {
    return this.authenticationService.isManager();
  }

  protected getTranslatedJobName(jobName: string): string {
    return this.translationHelper.getTranslatedJobName(jobName);
  }

  protected getTranslatedEmploymentType(employmentType: string): string {
    return this.translationHelper.getTranslatedEmploymentType(employmentType);
  }

  protected showAddEmployeeTable(): void {
    this.hideErrorMessages();
    this.isAdding = true;
  }

  protected showUpdateEmployeeTable(employee: EmployeeResponse): void {
    if (this.isEditing) {
      return;
    }

    this.hideErrorMessages();
    this.isEditing = true;

    this.updatedEmployee = {
      employee_email: employee.email,
      updated_first_name: employee.first_name,
      updated_last_name: employee.last_name,
      updated_email: employee.email,
      updated_password: null,
      updated_phone: employee.phone_number,
      updated_job: employee.job,
      updated_hourly_wage: employee.hourly_wage,
      updated_date_of_birth: employee.date_of_birth,
      updated_employment_type: employee.employment_type,
      updated_active: employee.is_active,
      updated_student: employee.is_student,
      updated_hired_date: employee.hired_at,
    };
  }

  protected hideAddEmployeeTable(): void {
    this.hideErrorMessages();
    this.isAdding = false;
    this.resetNewEmployee();
  }

  protected hideUpdateEmployeeTable(): void {
    this.isEditing = false;
    this.hideErrorMessages();
  }

  protected addEmployee(): void {
    this.newEmployee.first_name = this.capitalize(this.newEmployee.first_name);
    this.newEmployee.last_name = this.capitalize(this.newEmployee.last_name);
    this.newEmployee.job = this.capitalize(this.newEmployee.job);

    this.employeeService.addEmployee(this.newEmployee).subscribe({
      next: () => {
        this.alertService.showSuccessfulEmployeeAddAlert();
        this.loadEmployees();
        this.resetNewEmployee();
        this.hideAddEmployeeTable();
        this.hideErrorMessages();
      },
      error: (error) => {
        this.handleError(error);
      },
    });
  }

  protected updateEmployee(request: UpdatedEmployeeRequest): void {
    this.employeeService.updateEmployee(request).subscribe({
      next: () => {
        this.alertService.showSuccessfulEmployeeUpdateAlert();
        this.loadEmployees();
        this.hideUpdateEmployeeTable();
      },
      error: (error) => {
        this.handleError(error);
      },
    });
  }

  protected removeEmployee(employee: EmployeeResponse): void {
    this.alertService.showRemoveEmployeeAlert(employee).then((confirmed) => {
      if (!confirmed) return;

      this.employeeService
        .removeEmployee({
          email: employee.email,
        } as RemovedEmployeeRequest)
        .subscribe(() => {
          this.alertService.showSuccessfulEmployeeRemoveAlert(employee);
          this.loadEmployees();
        });
    });
  }

  private capitalize(value: string | null | undefined): string {
    if (!value) return '';
    return value.charAt(0).toUpperCase() + value.slice(1).toLowerCase();
  }

  protected resetNewEmployee(): void {
    this.newEmployee = {
      first_name: '',
      last_name: '',
      email: '',
      password: '',
      phone: '',
      date_of_birth: null,
      job: '',
      employment_type: null,
      is_student: false,
      hourly_wage: 0,
    };
  }

  private hideErrorMessages(): void {
    this.errorMessages = {};
  }

  private handleError(error: any): void {
    error.errorMessages
      ? (this.errorMessages = error.errorMessages)
      : (this.errorMessages = { general: 'An unexpected error occurred' });
  }
}
