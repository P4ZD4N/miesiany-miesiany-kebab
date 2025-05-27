import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { Router } from '@angular/router';
import { LangService } from '../../../services/lang/lang.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-manager-panel',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './manager-panel.component.html',
  styleUrl: './manager-panel.component.scss'
})
export class ManagerPanelComponent {

  constructor(
    private langService: LangService,
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
