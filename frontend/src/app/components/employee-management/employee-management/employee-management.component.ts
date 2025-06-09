import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { LangService } from '../../../services/lang/lang.service';
import { EmployeeService } from '../../../services/employees/employee.service';
import { Subscription } from 'rxjs';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';
import { EmployeeResponse } from '../../../responses/responses';

@Component({
  selector: 'app-employee-management',
  standalone: true,
  imports: [TranslateModule, CommonModule],
  templateUrl: './employee-management.component.html',
  styleUrl: './employee-management.component.scss'
})
export class EmployeeManagementComponent implements OnInit {

  employees: EmployeeResponse[] = [];
  languageChangeSubscription: Subscription;

  constructor(
    private authenticationService: AuthenticationService,
    private langService: LangService,
    private employeeService: EmployeeService,
    private translate: TranslateService
  ) {
    this.languageChangeSubscription = this.langService.languageChanged$.subscribe(() => {});
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
}
