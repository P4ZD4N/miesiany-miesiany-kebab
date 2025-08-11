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
  isUpdating: boolean = false;

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
      return 'employee-panel.good-morning';
    } else if (hour >= 12 && hour < 18) {
      return 'employee-panel.good-afternoon';
    } else {
      return 'employee-panel.good-evening';
    }
  }

  async updateCurrentEmployeeCredential(field: 'email' | 'password') {
    this.isUpdating = true;

    const isEmail = field === 'email';
    const confirmButtonText =
      this.langService.currentLang === 'pl' ? 'Zapisz' : 'Save';
    const cancelButtonText =
      this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';

    const titlePl = isEmail ? 'Aktualizuj email' : 'Aktualizuj haslo';
    const titleEn = isEmail ? 'Update email' : 'Update password';

    const inputValue = isEmail ? this.currentEmployeeData?.email : '';

    const { value: newValue } = await Swal.fire({
      title:
        this.langService.currentLang === 'pl'
          ? `<span style="color: red;">${titlePl}</span>`
          : `<span style="color: red;">${titleEn}</span>`,
      background: '#141414',
      color: 'white',
      focusConfirm: false,
      showCancelButton: true,
      cancelButtonText,
      cancelButtonColor: 'red',
      confirmButtonText,
      confirmButtonColor: '#198754',
      customClass: { validationMessage: 'custom-validation-message' },
      input: isEmail ? 'text' : 'password',
      inputValue,
      inputValidator: (value: string) => {
        if (!value) {
          return this.langService.currentLang === 'pl'
            ? 'Pole nie moze byc puste!'
            : 'You need to write something!';
        }

        if (isEmail && value.length > 35) {
          return this.langService.currentLang === 'pl'
            ? `Email nie moze przekraczac 35 znakow!`
            : `Email length can't exceed 35 characters!`;
        }

        if (!isEmail && value.length < 5) {
          return this.langService.currentLang === 'pl'
            ? `Haslo musi mieć przynajmniej 5 znakow!`
            : `Password must be at least 5 characters long!`;
        }

        return;
      },
    });

    if (!newValue) {
      this.isUpdating = false;
      return;
    }

    const payload = isEmail
      ? { updated_email: newValue }
      : { updated_password: newValue };

    this.employeeService
      .updateEmployeeCredentials(payload as UpdatedCredentialsRequest)
      .subscribe(
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

    this.isUpdating = false;
  }
}
