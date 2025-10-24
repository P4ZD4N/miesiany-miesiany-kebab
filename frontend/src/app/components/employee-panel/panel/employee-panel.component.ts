import { Component } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { EmployeeService } from '../../../services/employees/employee.service';
import { AlertService } from '../../../services/alert/alert.service';
import { StaffPanelComponent } from '../../staff-panel/staff-panel.component';

@Component({
  selector: 'app-employee-panel',
  standalone: true,
  imports: [CommonModule, TranslateModule, RouterModule],
  templateUrl: './employee-panel.component.html',
  styleUrl: './employee-panel.component.scss',
})
export class EmployeePanelComponent extends StaffPanelComponent {

  constructor(
    authenticationService: AuthenticationService,
    employeeService: EmployeeService,
    alertService: AlertService,
    router: Router
  ) {
    super(authenticationService, employeeService, alertService, router);
  }

  override ngOnInit(): void {
    if (this.isEmployee()) super.ngOnInit();
  }

  protected isEmployee(): boolean {
    return this.authenticationService.isEmployee();
  }

}
