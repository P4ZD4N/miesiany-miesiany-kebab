import { Component } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { LangService } from '../../../services/lang/lang.service';
import { AuthenticationRequest } from '../../../requests/requests';
import { AlertService } from '../../../services/alert/alert.service';

@Component({
  selector: 'app-authentication',
  standalone: true,
  imports: [CommonModule, TranslateModule, FormsModule],
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.scss'],
})
export class AuthenticationComponent {
  email: string = '';
  password: string = '';

  errorMessages: { [key: string]: string } = {};

  languageChangeSubscription: Subscription;

  constructor(
    private authService: AuthenticationService,
    private router: Router,
    private langService: LangService,
    private alertService: AlertService
  ) {
    this.languageChangeSubscription =
      this.langService.languageChanged$.subscribe(() => {
        this.errorMessages = {};
      });
  }

  authenticate(): void {
    const authData: AuthenticationRequest = {
      email: this.email,
      password: this.password,
    };

    this.authService.authenticate(authData).subscribe({
      next: () => {
        this.errorMessages = {};
        this.alertService.showSuccessfulLoginAlert(authData);
        this.router.navigate(['/']);
      },
      error: (error) => this.handleError(error),
    });
  }

  handleError(error: any): void {
    error.errorMessages
      ? (this.errorMessages = error.errorMessages)
      : (this.errorMessages = {
          general:
            this.langService.currentLang === 'pl'
              ? 'Wystąpił niespodziewany błąd'
              : 'An unexpected error occurred',
        });
  }
}
