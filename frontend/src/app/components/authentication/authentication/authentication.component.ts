import { Component } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { LangService } from '../../../services/lang/lang.service';
import { AuthenticationRequest } from '../../../requests/requests';
import { AuthenticationResponse } from '../../../responses/responses';

@Component({
  selector: 'app-authentication',
  standalone: true,
  imports: [CommonModule, TranslateModule, FormsModule],
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.scss'] // Fixed typo here: should be styleUrls
})
export class AuthenticationComponent {
  email: string = '';
  password: string = '';
  errorMessages: { [key: string]: string } = {};
  private languageChangeSubscription: Subscription;

  constructor(private authService: AuthenticationService, private router: Router, private langService: LangService) {
    this.languageChangeSubscription = this.langService.languageChanged$.subscribe(() => {
      this.hideErrorMessages();
    });
  }
  
  authenticate() {
    const authData: AuthenticationRequest = { email: this.email, password: this.password };

    this.authService.authenticate(authData).subscribe({
      next: (response) => {
        this.handleUpdateResponse(response);
        this.router.navigate(['/']);
      },
      error: (error) => this.handleError(error)
    });
  }

  handleUpdateResponse(response: AuthenticationResponse) {
    console.log('Login successful', response);
    this.errorMessages = {};
  }
  
  handleError(error: any) {
    console.error('Login failed', error);

    if (error.errorMessages) {
      this.errorMessages = error.errorMessages;
    } else {
      this.errorMessages = { general: this.langService.currentLang === 'pl' ? 'Wystąpił niespodziewany błąd' : 'An unexpected error occurred' };
    }
  }

  hideErrorMessages() {
    this.errorMessages = {};
  }
}
