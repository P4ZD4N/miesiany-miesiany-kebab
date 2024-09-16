import { Component } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-employee-panel',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './employee-panel.component.html',
  styleUrl: './employee-panel.component.scss'
})
export class EmployeePanelComponent {

  constructor(
    private authenticationService: AuthenticationService,
    private router: Router
  ) {}

  isEmployee(): boolean {
    return this.authenticationService.isEmployee();
  }

  logout(): void {
    this.authenticationService.logout().subscribe({
      next: () => {
        this.router.navigate(['/']);
      }
    });
  }
}
