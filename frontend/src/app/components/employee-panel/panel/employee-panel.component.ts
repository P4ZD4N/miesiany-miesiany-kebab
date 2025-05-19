import { Component } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { LangService } from '../../../services/lang/lang.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-employee-panel',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './employee-panel.component.html',
  styleUrl: './employee-panel.component.scss'
})
export class EmployeePanelComponent {

  constructor(
    private langService: LangService,
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

        Swal.fire({
          text: this.langService.currentLang === 'pl' 
          ? `Wylogowano pomyslnie!` 
          : `Successfully logged out!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: '#141414',
          color: 'white',
          confirmButtonText: 'Ok',
        });
      }
    });
  }
}
