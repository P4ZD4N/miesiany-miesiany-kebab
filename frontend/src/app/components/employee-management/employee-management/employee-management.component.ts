import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { LangService } from '../../../services/lang/lang.service';
import { EmployeeService } from '../../../services/employees/employee.service';
import { Subscription } from 'rxjs';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';
import { EmployeeResponse } from '../../../responses/responses';
import { NewEmployeeRequest } from '../../../requests/requests';
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
    hourly_wage: 0
  };

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

 hideAddEmployeeTable(): void {
    this.hideErrorMessages();
    this.isAdding = false;
    this.resetNewEmployee();
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
