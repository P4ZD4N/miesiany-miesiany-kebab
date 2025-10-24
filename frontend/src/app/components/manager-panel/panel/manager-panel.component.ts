import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { Router, RouterModule } from '@angular/router';
import { EmployeeService } from '../../../services/employees/employee.service';
import { AlertService } from '../../../services/alert/alert.service';
import { StaffPanelComponent } from '../../staff-panel/staff-panel.component';

@Component({
  selector: 'app-manager-panel',
  standalone: true,
  imports: [CommonModule, TranslateModule, RouterModule],
  templateUrl: './manager-panel.component.html',
  styleUrl: './manager-panel.component.scss',
})
export class ManagerPanelComponent extends StaffPanelComponent {
  constructor(
    authenticationService: AuthenticationService,
    employeeService: EmployeeService,
    alertService: AlertService,
    router: Router,
  ) {
    super(authenticationService, employeeService, alertService, router)
  }

  protected isManager(): boolean {
    return this.authenticationService.isManager();
  }
}
