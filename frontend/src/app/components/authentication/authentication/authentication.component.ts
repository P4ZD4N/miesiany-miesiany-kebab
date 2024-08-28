import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TranslateModule } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthenticationService } from '../../../services/authentication/authentication.service';

@Component({
  selector: 'app-authentication',
  standalone: true,
  imports: [TranslateModule, FormsModule],
  templateUrl: './authentication.component.html',
  styleUrl: './authentication.component.scss'
})
export class AuthenticationComponent {
  email: string = '';
  password: string = '';

  constructor(private authService: AuthenticationService, private router: Router) {}
  
  authenticate() {

    this.authService.authenticate(this.email, this.password)
      .subscribe({
        next: (response) => {
          this.handleUpdateResponse(response);
          this.router.navigate(['/']);
        },
        error: (error) => this.handleError(error)
      });
  }

  handleUpdateResponse(response: any) {
    console.log('Login successful', response);
  }
  
  handleError(error: any) {
    console.error('Login failed', error);
  }
}
