import { Component, OnInit } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { LangService } from '../../../services/lang/lang.service';
import Swal from 'sweetalert2';
import { EmployeeService } from '../../../services/employees/employee.service';
import { EmployeeResponse } from '../../../responses/responses';
import { UpdatedCredentialsRequest } from '../../../requests/requests';
import { AlertService } from '../../../services/alert/alert.service';

@Component({
  selector: 'app-employee-panel',
  standalone: true,
  imports: [CommonModule, TranslateModule, RouterModule],
  templateUrl: './employee-panel.component.html',
  styleUrl: './employee-panel.component.scss',
})
export class EmployeePanelComponent implements OnInit {
  currentEmployeeData: EmployeeResponse | null = null;

  constructor(
    private authenticationService: AuthenticationService,
    private employeeService: EmployeeService,
    private alertService: AlertService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCurrentEmployeeDetails();
  }

  loadCurrentEmployeeDetails(): void {
    this.employeeService.getCurrentEmployee().subscribe(
      (data: EmployeeResponse) => (this.currentEmployeeData = data),
      (error) => console.log('Error loading current employee data')
    );
  }

  isEmployee(): boolean {
    return this.authenticationService.isEmployee();
  }

  logout(): void {
    this.authenticationService.logout().subscribe({
      next: () => {
        this.router.navigate(['/']);
        this.alertService.showSuccessfulLogoutAlert();
      },
    });
  }

  getGreetingKey(): string {
    const hour = new Date().getHours();

    if (hour >= 5 && hour < 12) return 'common.staff-panel.good-morning';
    else if (hour >= 12 && hour < 18) return 'common.staff-panel.good-afternoon';
    else return 'common.staff-panel.good-evening';
  }

  async updateCurrentEmployeeCredential(
    field: 'email' | 'password'
  ): Promise<void> {
    const isEmail = field === 'email';

    isEmail
      ? this.updateCurrentEmployeeEmail(isEmail)
      : this.updateCurrentEmployeePassword(isEmail);
  }

  private async updateCurrentEmployeeEmail(isEmail: boolean): Promise<void> {
    const formValues = await this.alertService.showUpdateEmailAlert(
      this.currentEmployeeData
    );

    if (!formValues) return;

    await this.saveUpdatedCredential(
      { password: formValues.password, updated_email: formValues.email },
      isEmail
    );
  }

  private async updateCurrentEmployeePassword(isEmail: boolean): Promise<void> {
    const formValues = await this.alertService.showUpdatePasswordAlert();

    if (!formValues) return;

    this.saveUpdatedCredential(
      {
        password: formValues.password,
        updated_password: formValues.updated_password,
      },
      isEmail
    );
  }

  private async saveUpdatedCredential(
    payload: UpdatedCredentialsRequest,
    isEmail: boolean
  ): Promise<void> {
    this.employeeService.updateEmployeeCredentials(payload).subscribe(
      () => {
        this.alertService.showSuccessfulCredentialUpdateAlert(isEmail);
        this.loadCurrentEmployeeDetails();
      },
      (error) => this.alertService.showCredentialUpdateErrorAlert(error)
    );
  }
}
