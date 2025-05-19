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
import Swal from 'sweetalert2';

@Component({
  selector: 'app-authentication',
  standalone: true,
  imports: [CommonModule, TranslateModule, FormsModule],
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.scss']
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
        this.hideErrorMessages();

        Swal.fire({
          text: this.langService.currentLang === 'pl' 
          ? `Zalogowano pomyslnie uzytkownika z emailem ${authData.email}!` 
          : `Successfully logged in user with ${authData.email} email!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: '#141414',
          color: 'white',
          confirmButtonText: 'Ok',
        });

        this.handleUpdateResponse(response);
        this.router.navigate(['/']);
      },
      error: (error) => this.handleError(error)
    });
  }

  handleUpdateResponse(response: AuthenticationResponse) {
    console.log('Login successful', response);
    this.hideErrorMessages();
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
