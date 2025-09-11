import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { LangService } from '../../../services/lang/lang.service';
import { EmployeeService } from '../../../services/employees/employee.service';
import { Subscription } from 'rxjs';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';
import { EmployeeResponse } from '../../../responses/responses';
import { NewEmployeeRequest, RemovedEmployeeRequest, UpdatedEmployeeRequest } from '../../../requests/requests';
import { EmploymentType } from '../../../enums/employment-type.enum';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-employee-management',
  standalone: true,
  imports: [TranslateModule, CommonModule, FormsModule],
  templateUrl: './employee-management.component.html',
  styleUrl: './employee-management.component.scss'
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
    hourly_wage: 0
  };
  updatedEmployee: UpdatedEmployeeRequest | null = null;

  isAdding = false;
  isEditing = false;

  employmentTypes: EmploymentType[] = [EmploymentType.MANDATE_CONTRACT, EmploymentType.PERMANENT];
  errorMessages: { [key: string]: string } = {};
  employees: EmployeeResponse[] = [];
  languageChangeSubscription: Subscription;

  constructor(
    private authenticationService: AuthenticationService,
    private langService: LangService,
    private employeeService: EmployeeService,
    private translate: TranslateService
  ) {
    this.languageChangeSubscription = this.langService.languageChanged$.subscribe(() => {
      this.hideErrorMessages();
    });
  }

  ngOnInit(): void {
    if (this.isManager() ) {
      this.loadEmployees();
    }
  }

  loadEmployees(): void {
    this.employeeService.getEmployees().subscribe(
      (data: EmployeeResponse[]) => this.employees = data.sort((a, b) => a.last_name.localeCompare(b.last_name)),
      (error) => console.log('Error loading employees', error));
  }

  isManager(): boolean {
    return this.authenticationService.isManager();
  }

  getTranslatedJobName(jobName: string): string {
    let jobNameTranslated = this.translate.instant('employee-management.jobs.' + jobName);

    if (jobNameTranslated === 'employee-management.jobs.' + jobName) {
      jobNameTranslated = jobName;
    }
    
    return jobNameTranslated;
  }

  getTranslatedEmploymentType(employmentType: string): string {
    let employmentTypeTranslated = this.translate.instant('employee-management.employment_types.' + employmentType);

    if (employmentTypeTranslated === 'employee-management.employment_types.' + employmentType) {
      employmentTypeTranslated = employmentType;
    }
    
    return employmentTypeTranslated;
  }

  showAddEmployeeTable(): void {
    this.hideErrorMessages();
    this.isAdding = true;
  }

  addEmployee(): void {
    
    this.newEmployee.first_name = this.newEmployee.first_name.charAt(0).toUpperCase() + this.newEmployee.first_name.slice(1).toLowerCase();
    this.newEmployee.last_name = this.newEmployee.last_name.charAt(0).toUpperCase() + this.newEmployee.last_name.slice(1).toLowerCase();
    this.newEmployee.job = this.newEmployee.job.charAt(0).toUpperCase() + this.newEmployee.job.slice(1).toLowerCase();

    this.employeeService.addEmployee(this.newEmployee).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? `Pomyslnie dodano nowego pracownika'!` : `Successfully added new employee!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: '#141414',
          color: 'white',
          confirmButtonText: 'Ok',
        });

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

  editEmployeeRow(employee: EmployeeResponse) {
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
      updated_hired_date: employee.hired_at
    };
  }

  updateEmployee(request: UpdatedEmployeeRequest) {

    this.employeeService.updateEmployee(request).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? 'Pomyslnie zaktualizowano pracownika!' : 'Successfully updated employee!',
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: '#141414',
          color: 'white',
          confirmButtonText: 'Ok',
        });

        this.loadEmployees();
        this.hideUpdateEmployeeTable();
      },
      error: (error) => {
        this.handleError(error);
      }
    });
  }

  removeEmployee(employee: EmployeeResponse): void {
    const confirmationMessage =
      this.langService.currentLang === 'pl'
      ? `Czy na pewno chcesz usunac pracownika z emailem '${employee.email}'?`
      : `Are you sure you want to remove employee with email '${employee.email}'?`;

    Swal.fire({
      title: this.langService.currentLang === 'pl' ? 'Potwierdzenie' : 'Confirmation',
      text: confirmationMessage,
      icon: 'warning',
      iconColor: 'red',
      showCancelButton: true,
      confirmButtonColor: '#0077ff',
      cancelButtonColor: 'red',
      background: '#141414',
      color: 'white',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Tak' : 'Yes',
      cancelButtonText: this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel',
    }).then((result) => {
      if (result.isConfirmed) {
        this.employeeService.removeEmployee({ email: employee.email } as RemovedEmployeeRequest).subscribe({
          next: () => {
            Swal.fire({
              text: this.langService.currentLang === 'pl'
                ? `Pomyslnie usunieto pracownika z emailem '${employee.email}'!`
                : `Successfully removed employee with email '${employee.email}'!`,
              icon: 'success',
              iconColor: 'green',
              confirmButtonColor: 'green',
              background: '#141414',
              color: 'white',
              confirmButtonText: 'Ok',
            });
            this.loadEmployees();
          },
          error: (err) => {
            console.log(err.errorMessages);
            Swal.fire({
              text: err.errorMessages['message'],
              icon: 'error',
              iconColor: 'red',
              confirmButtonColor: 'red',
              background: '#141414',
              color: 'white',
              confirmButtonText: 'Ok',
            });
          }
        });
      }
    });
  }

  hideAddEmployeeTable(): void {
    this.hideErrorMessages();
    this.isAdding = false;
    this.resetNewEmployee();
  }

  hideUpdateEmployeeTable(): void {
    this.isEditing = false;
    this.hideErrorMessages();
  }

  resetNewEmployee(): void {
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
      hourly_wage: 0
    };
  }

  hideErrorMessages(): void {
    this.errorMessages = {};
  }

  handleError(error: any) {
    if (error.errorMessages) {
      this.errorMessages = error.errorMessages;
      console.log(this.errorMessages);
    } else {
      this.errorMessages = { general: 'An unexpected error occurred' };
    }
  }
}
