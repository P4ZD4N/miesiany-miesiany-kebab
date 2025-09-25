import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';
import { LangService } from '../lang/lang.service';
import {
  AuthenticationRequest,
  NewJobOfferRequest,
} from '../../requests/requests';
import {
  EmployeeResponse,
  JobOfferGeneralResponse,
} from '../../responses/responses';
import { TranslationHelperService } from '../translation-helper/translation-helper.service';
import { JobApplicationFormData } from '../../util-types/util-types';

@Injectable({
  providedIn: 'root',
})
export class AlertService {
  constructor(
    private langService: LangService,
    private translationHelper: TranslationHelperService
  ) {}

  showSuccessfulLoginAlert(authData: AuthenticationRequest): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Zalogowano pomyslnie uzytkownika z emailem ${authData.email}!`
          : `Successfully logged in user with ${authData.email} email!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showSuccessfulLogoutAlert(): void {
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
  }

  showSuccessfulContactUpdateAlert(contactTypeTranslated: string): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie zaktualizowano kontakt typu '${contactTypeTranslated}'!`
          : `Successfully updated contact of type '${contactTypeTranslated}'!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showSuccessfulDiscountCodeAddAlert(): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie dodano nowy kod rabatowy'!`
          : `Successfully added new discount code!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showSuccessfulDiscountCodeUpdateAlert(): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? 'Pomyslnie zaktualizowano kod rabatowy!'
          : 'Successfully updated discount code!',
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showRemoveDiscountCodeAlert(): Promise<boolean> {
    return Swal.fire({
      title:
        this.langService.currentLang === 'pl'
          ? 'Potwierdzenie'
          : 'Confirmation',
      text:
        this.langService.currentLang === 'pl'
          ? `Czy na pewno chcesz usunac ten kod rabatowy?`
          : `Are you sure you want to remove this discount code?`,
      icon: 'warning',
      iconColor: 'red',
      showCancelButton: true,
      confirmButtonColor: '#0077ff',
      cancelButtonColor: 'red',
      background: '#141414',
      color: 'white',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Tak' : 'Yes',
      cancelButtonText:
        this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel',
    }).then((result) => result.isConfirmed);
  }

  showSuccessfulDiscountCodeRemoveAlert(): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie usunieto kod rabatowy!`
          : `Successfully removed discount code!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showSuccessfulEmployeeUpdateAlert(): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? 'Pomyslnie zaktualizowano pracownika!'
          : 'Successfully updated employee!',
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showRemoveEmployeeAlert(employee: EmployeeResponse): Promise<boolean> {
    return Swal.fire({
      title:
        this.langService.currentLang === 'pl'
          ? 'Potwierdzenie'
          : 'Confirmation',
      text:
        this.langService.currentLang === 'pl'
          ? `Czy na pewno chcesz usunac pracownika z emailem '${employee.email}'?`
          : `Are you sure you want to remove employee with email '${employee.email}'?`,
      icon: 'warning',
      iconColor: 'red',
      showCancelButton: true,
      confirmButtonColor: '#0077ff',
      cancelButtonColor: 'red',
      background: '#141414',
      color: 'white',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Tak' : 'Yes',
      cancelButtonText:
        this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel',
    }).then((result) => result.isConfirmed);
  }

  showSuccessfulEmployeeRemoveAlert(employee: EmployeeResponse): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie usunieto pracownika z emailem '${employee.email}'!`
          : `Successfully removed employee with email '${employee.email}'!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showSuccessfulEmployeeAddAlert(): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie dodano nowego pracownika'!`
          : `Successfully added new employee!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  async showUpdateEmailAlert(currentEmployeeData: EmployeeResponse | null) {
    const confirmButtonText =
      this.langService.currentLang === 'pl' ? 'Zapisz' : 'Save';
    const cancelButtonText =
      this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';

    const { value: formValues } = await Swal.fire({
      title:
        this.langService.currentLang === 'pl'
          ? `<span style="color: red;">Aktualizuj email</span>`
          : `<span style="color: red;">Update email</span>`,
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
        currentEmployeeData?.email
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
              ? 'Wszystkie pola są wymagane!'
              : 'All fields are required!'
          );
          return false;
        }

        if (email.length > 35) {
          Swal.showValidationMessage(
            this.langService.currentLang === 'pl'
              ? 'Email nie może przekraczać 35 znaków!'
              : "Email length can't exceed 35 characters!"
          );
          return false;
        }

        return { password, email };
      },
    });

    return formValues;
  }

  async showUpdatePasswordAlert() {
    const confirmButtonText =
      this.langService.currentLang === 'pl' ? 'Zapisz' : 'Save';
    const cancelButtonText =
      this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';

    const { value: formValues } = await Swal.fire({
      title:
        this.langService.currentLang === 'pl'
          ? `<span style="color: red;">Aktualizuj haslo</span>`
          : `<span style="color: red;">Update password</span>`,
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
          this.langService.currentLang === 'pl' ? 'Stare haslo' : 'Old password'
        }">
        <input id="swal-input2" class="swal2-input" type="password" placeholder="${
          this.langService.currentLang === 'pl' ? 'Nowe haslo' : 'New password'
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

    return formValues;
  }

  showSuccessfulCredentialUpdateAlert(isEmail: boolean) {
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
  }

  showCredentialUpdateErrorAlert(error: any) {
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

  showSuccessfulJobOfferAddAlert(newJobOffer: NewJobOfferRequest): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie dodano oferte pracy na stanowisko '${newJobOffer.position_name}'!`
          : `Successfully added job offer for position '${newJobOffer.position_name}'!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showSuccessfulJobOfferUpdateAlert(position: string): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie zaktualizowano oferte pracy na pozycji '${position}'!`
          : `Successfully updated job offer on '${position}' position!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showRemoveJobOfferAlert(position: string): Promise<boolean> {
    return Swal.fire({
      title:
        this.langService.currentLang === 'pl'
          ? 'Potwierdzenie'
          : 'Confirmation',
      text:
        this.langService.currentLang === 'pl'
          ? `Czy na pewno chcesz usunac oferte pracy na stanowisku '${position}'?`
          : `Are you sure you want to remove job offer on '${position}' position?`,
      icon: 'warning',
      iconColor: 'red',
      showCancelButton: true,
      confirmButtonColor: '#0077ff',
      cancelButtonColor: 'red',
      background: '#141414',
      color: 'white',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Tak' : 'Yes',
      cancelButtonText:
        this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel',
    }).then((result) => result.isConfirmed);
  }

  showSuccessfulJobOfferRemoveAlert(position: string): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie usunieto oferte pracy na stanowisku '${position}'!`
          : `Successfully removed job offer on '${position}' position!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showApplyToJobOfferAlert(
    jobOffer: JobOfferGeneralResponse
  ): Promise<JobApplicationFormData> {
    let positionNameTranslated = this.translationHelper.getTranslatedPosition(
      jobOffer.position_name
    );

    const title =
      this.langService.currentLang === 'pl'
        ? 'Aplikuj na stanowisko ' + positionNameTranslated
        : 'Apply for ' + jobOffer.position_name + ' position';
    const confirmButtonText =
      this.langService.currentLang === 'pl' ? 'Aplikuj' : 'Apply';
    const cancelButtonText =
      this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';
    const areYouStudent =
      this.langService.currentLang === 'pl'
        ? 'Czy jestes studentem?'
        : 'Are you a student?';
    const attachCv =
      this.langService.currentLang === 'pl' ? 'Zalacz CV!' : 'Attach CV!';
    const chooseFile =
      this.langService.currentLang === 'pl' ? 'Wybierz plik' : 'Choose file';
    const noFileChosen =
      this.langService.currentLang === 'pl' ? 'Brak pliku' : 'No file chosen';

    const firstNamePlaceholder =
      this.langService.currentLang === 'pl' ? 'Imie' : 'First Name';
    const lastNamePlaceholder =
      this.langService.currentLang === 'pl' ? 'Nazwisko' : 'Last Name';
    const emailPlaceholder =
      this.langService.currentLang === 'pl' ? 'Email' : 'Email';
    const telephonePlaceholder =
      this.langService.currentLang === 'pl'
        ? 'Telefon (9 cyfr)'
        : 'Phone (9 digits)';
    const additionalMessagePlaceholder =
      this.langService.currentLang === 'pl'
        ? 'Wiadomosc dodatkowa'
        : 'Additional Message';

    const html = `
      <style>
        input[type="text"], input[type="email"], textarea {
          color: white;
          text-align: center;
          background-color: inherit;
          width: 100%; 
          max-width: 400px; 
          padding: 10px; 
          margin: 10px 0; 
          border: 1px solid #ccc; 
          border-radius: 5px;
          transition: border 0.3s ease;
          outline: none;
        }

        input[type="text"]:focus, input[type="email"]:focus, textarea:focus {
            border: 1px solid red; 
        }

        input[type="checkbox"] {
          margin-right: 5px; 
          accent-color: red;
        }

        button {
          margin-top: 10px; 
          background-color: red; 
          color: white; 
          border: none; 
          padding: 10px 20px; 
          border-radius: 5px; 
          cursor: pointer;
        }
      </style>
      
      <div>
        <input type="text" id="firstName" maxlength=25  placeholder="${firstNamePlaceholder}">
      </div>
      <div>
        <input type="text" id="lastName" maxlength=25  placeholder="${lastNamePlaceholder}">
      </div>
      <div>
        <input type="email" id="email" maxlength=30  placeholder="${emailPlaceholder}">
      </div>
      <div>
        <input type="text" id="telephone" maxlength=9 placeholder="${telephonePlaceholder}">
      </div>
      <div>
        <textarea id="additionalMessage" maxlength=200 placeholder="${additionalMessagePlaceholder}"></textarea>
      </div>
      <div style="margin-top: 10px;">
        <input type="checkbox" id="isStudent">
        <label for="isStudent">${areYouStudent}</label>
      </div>
      <div style="margin-top: 10px; display: flex; justify-content: center; flex-direction: column;">
        <label for="cv" class="swal2-label">${attachCv}</label>
        <input type="file" id="cv" class="swal2-input" accept=".pdf, .doc, .docx" style="display: none;" />
        <button type="button" onclick="document.getElementById('cv').click();">
          ${chooseFile}
        </button>
        <div id="file-name" style="margin-top: 10px; color: white;">${noFileChosen}</div>
      </div>
    `;

    return Swal.fire({
      title: `<span style="color: red;">${title}</span>`,
      background: '#141414',
      color: 'white',
      html: html,
      confirmButtonText: confirmButtonText,
      confirmButtonColor: '#198754',
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
      showCancelButton: true,
      focusConfirm: false,
      customClass: {
        validationMessage: 'custom-validation-message',
      },
      preConfirm: () => {
        const firstName = (
          document.getElementById('firstName') as HTMLInputElement
        ).value;
        const lastName = (
          document.getElementById('lastName') as HTMLInputElement
        ).value;
        const email = (document.getElementById('email') as HTMLInputElement)
          .value;
        const telephone = (
          document.getElementById('telephone') as HTMLInputElement
        ).value;
        const additionalMessage = (
          document.getElementById('additionalMessage') as HTMLTextAreaElement
        ).value;
        const isStudent = (
          document.getElementById('isStudent') as HTMLInputElement
        ).checked;
        const cvFile = (document.getElementById('cv') as HTMLInputElement)
          .files?.[0];

        if (!firstName || !lastName || !email || !telephone) {
          Swal.showValidationMessage(
            this.langService.currentLang === 'pl'
              ? 'Wszystkie pola z wyjatkiem dodatkowej wiadomosci sa wymagane'
              : 'All fields except additional message are required'
          );
          return null;
        }

        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        if (!emailRegex.test(email)) {
          Swal.showValidationMessage(
            this.langService.currentLang === 'pl'
              ? 'Niepoprawny email!'
              : 'Invalid email!'
          );
          return null;
        }

        const phoneRegex = /^[0-9]{9}$/;
        if (!phoneRegex.test(telephone)) {
          Swal.showValidationMessage(
            this.langService.currentLang === 'pl'
              ? 'Niepoprawny numer telefonu!'
              : 'Invalid phone number!'
          );
          return null;
        }

        if (cvFile == null) {
          Swal.showValidationMessage(
            this.langService.currentLang === 'pl'
              ? 'Brak zalaczonego CV!'
              : 'No CV attached!'
          );
          return null;
        }

        return {
          positionName: jobOffer.position_name,
          firstName,
          lastName,
          email,
          telephone,
          additionalMessage,
          isStudent,
          cvFile,
        };
      },
    }).then((result) => (result.isConfirmed ? result.value : null));
  }

  showSuccessfulJobOfferApplyAlert(jobOffer: JobOfferGeneralResponse): void {
    let positionNameTranslated = this.translationHelper.getTranslatedPosition(
      jobOffer.position_name
    );

    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie zaaplikowano na pozycje '${positionNameTranslated}'!`
          : `Successfully applied for a '${positionNameTranslated}' position!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showJobOfferApplyErrorAlert(errorMessages: { [key: string]: string }): void {
    Swal.fire({
      text: errorMessages['message'],
      icon: 'error',
      iconColor: 'red',
      confirmButtonColor: 'red',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showRemoveJobApplicationAlert(): Promise<boolean> {
    return Swal.fire({
      title:
        this.langService.currentLang === 'pl'
          ? 'Potwierdzenie'
          : 'Confirmation',
      text: 
        this.langService.currentLang === 'pl'
          ? `Czy na pewno chcesz usunac aplikacje?`
          : `Are you sure you want to remove job application?`,
      icon: 'warning',
      iconColor: 'red',
      showCancelButton: true,
      confirmButtonColor: '#0077ff',
      cancelButtonColor: 'red',
      background: '#141414',
      color: 'white',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Tak' : 'Yes',
      cancelButtonText:
        this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel',
    }).then((result) =>  result.isConfirmed);
  }

  showSuccessfulJobApplicationRemoveAlert(position: string): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie usunieto aplikacje na oferte pracy na pozycji '${position}'!`
          : `Successfully removed job application to job offer on '${position}' position!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }
}
