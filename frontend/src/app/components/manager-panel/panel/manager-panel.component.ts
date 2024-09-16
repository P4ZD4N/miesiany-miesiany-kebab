import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-manager-panel',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './manager-panel.component.html',
  styleUrl: './manager-panel.component.scss'
})
export class ManagerPanelComponent {

  constructor(
    private authenticationService: AuthenticationService,
    private router: Router
  ) {}

  isManager(): boolean {
    return this.authenticationService.isManager();
  }

  logout(): void {
    this.authenticationService.logout().subscribe({
      next: () => {
        this.router.navigate(['/']);
      }
    });
  }
}
