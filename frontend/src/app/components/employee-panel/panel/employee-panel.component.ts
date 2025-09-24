import { Component, OnInit } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { LangService } from '../../../services/lang/lang.service';
import Swal from 'sweetalert2';
import { EmployeeService } from '../../../services/employees/employee.service';
import { EmployeeResponse } from '../../../responses/responses';
import { UpdatedCredentialsRequest } from '../../../requests/requests';

@Component({
  selector: 'app-employee-panel',
  standalone: true,
  imports: [CommonModule, TranslateModule, RouterModule],
  templateUrl: './employee-panel.component.html',
  styleUrl: './employee-panel.component.scss',
})
export class EmployeePanelComponent implements OnInit {
  currentEmployeeData: EmployeeResponse | null = null;

  constructor(
    private langService: LangService,
    private authenticationService: AuthenticationService,
    private employeeService: EmployeeService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCurrentEmployeeDetails();
  }

  loadCurrentEmployeeDetails(): void {
    this.employeeService.getCurrentEmployee().subscribe(
      (data: EmployeeResponse) => (this.currentEmployeeData = data),
      (error) => console.log('Error loading current employee data')
    );
  }

  isEmployee(): boolean {
    return this.authenticationService.isEmployee();
  }

  logout(): void {
    this.authenticationService.logout().subscribe({
      next: () => {
        this.router.navigate(['/']);

        Swal.fire({
          text:
            this.langService.currentLang === 'pl'
              ? `Wylogowano pomyslnie!`
              : `Successfully logged out!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: '#141414',
          color: 'white',
          confirmButtonText: 'Ok',
        });
      },
    });
  }

  getGreetingKey(): string {
    const hour = new Date().getHours();

    if (hour >= 5 && hour < 12) {
      return 'common.staff-panel.good-morning';
    } else if (hour >= 12 && hour < 18) {
      return 'common.staff-panel.good-afternoon';
    } else {
      return 'common.staff-panel.good-evening';
    }
  }

  async updateCurrentEmployeeCredential(field: 'email' | 'password') {
    const isEmail = field === 'email';
    const confirmButtonText =
      this.langService.currentLang === 'pl' ? 'Zapisz' : 'Save';
    const cancelButtonText =
      this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';

    const titlePl = isEmail ? 'Aktualizuj email' : 'Aktualizuj haslo';
    const titleEn = isEmail ? 'Update email' : 'Update password';

    if (isEmail) {
      const { value: formValues } = await Swal.fire({
        title:
          this.langService.currentLang === 'pl'
            ? `<span style="color: red;">${titlePl}</span>`
            : `<span style="color: red;">${titleEn}</span>`,
        background: '#141414',
        color: 'white',
        showCancelButton: true,
        cancelButtonText,
        cancelButtonColor: 'red',
        confirmButtonText,
        confirmButtonColor: '#198754',
        customClass: { validationMessage: 'custom-validation-message' },
        html: `
          <input id="swal-password" class="swal2-input" type="password" placeholder="${
            this.langService.currentLang === 'pl' ? 'Haslo' : 'Password'
          }">
          <input id="swal-email" class="swal2-input" type="text" placeholder="Email" value="${
            this.currentEmployeeData?.email
          }">
        `,
        focusConfirm: false,
        preConfirm: () => {
          const password = (
            document.getElementById('swal-password') as HTMLInputElement
          ).value;
          const email = (
            document.getElementById('swal-email') as HTMLInputElement
          ).value;

          if (!password || !email) {
            Swal.showValidationMessage(
              this.langService.currentLang === 'pl'
                ? 'Wszystkie pola sa wymagane!'
                : 'All fields are required!'
            );
            return false;
          }

          if (email.length > 35) {
            Swal.showValidationMessage(
              this.langService.currentLang === 'pl'
                ? 'Email nie moze przekraczac 35 znakow!'
                : "Email length can't exceed 35 characters!"
            );
            return false;
          }

          return { password, email };
        },
      });

      if (!formValues) return;
      
      await this.saveUpdatedCredential(
        { password: formValues.password, updated_email: formValues.email },
        isEmail
      );
    } else {
      const { value: formValues } = await Swal.fire({
        title:
          this.langService.currentLang === 'pl'
            ? `<span style="color: red;">${titlePl}</span>`
            : `<span style="color: red;">${titleEn}</span>`,
        background: '#141414',
        color: 'white',
        showCancelButton: true,
        cancelButtonText,
        cancelButtonColor: 'red',
        confirmButtonText,
        confirmButtonColor: '#198754',
        customClass: { validationMessage: 'custom-validation-message' },
        html: `
          <input id="swal-input1" class="swal2-input" type="password" placeholder="${
            this.langService.currentLang === 'pl'
              ? 'Stare haslo'
              : 'Old password'
          }">
          <input id="swal-input2" class="swal2-input" type="password" placeholder="${
            this.langService.currentLang === 'pl'
              ? 'Nowe haslo'
              : 'New password'
          }">
        `,
        focusConfirm: false,
        preConfirm: () => {
          const password = (
            document.getElementById('swal-input1') as HTMLInputElement
          ).value;
          const newPassword = (
            document.getElementById('swal-input2') as HTMLInputElement
          ).value;

          if (!password || !newPassword) {
            Swal.showValidationMessage(
              this.langService.currentLang === 'pl'
                ? 'Wszystkie pola sa wymagane!'
                : 'All fields are required!'
            );
            return false;
          }

          if (newPassword.length < 5) {
            Swal.showValidationMessage(
              this.langService.currentLang === 'pl'
                ? 'Nowe haslo musi miec przynajmniej 5 znakow!'
                : 'Password must be at least 5 characters long!'
            );
            return false;
          }

          return {
            password: password,
            updated_password: newPassword,
          };
        },
      });

      if (!formValues) return;

      this.saveUpdatedCredential(
        {
          password: formValues.old_password,
          updated_password: formValues.updated_password,
        },
        isEmail
      );
    }
  }

  private async saveUpdatedCredential(
    payload: UpdatedCredentialsRequest,
    isEmail: boolean
  ) {
    console.log(payload);
    this.employeeService.updateEmployeeCredentials(payload).subscribe(
      () => {
        Swal.fire({
          text:
            this.langService.currentLang === 'pl'
              ? `Pomyslnie zaktualizowano ${isEmail ? 'email' : 'haslo'}!`
              : `Successfully updated ${isEmail ? 'email' : 'password'}!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: '#141414',
          color: 'white',
          confirmButtonText: 'Ok',
        });
        this.loadCurrentEmployeeDetails();
      },
      (error) => {
        const errMsg =
          error.errorMessages.message ||
          error.errorMessages.updatedEmail ||
          error.errorMessages.updated_password;

        Swal.fire({
          text: errMsg,
          icon: 'error',
          iconColor: 'red',
          confirmButtonColor: 'red',
          background: '#141414',
          color: 'white',
          confirmButtonText: 'Ok',
        });
      }
    );
  }
}
