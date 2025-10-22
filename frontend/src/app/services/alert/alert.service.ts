import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';
import { LangService } from '../lang/lang.service';
import {
  AuthenticationRequest,
  NewJobOfferRequest,
  UpdatedOrderRequest,
} from '../../requests/requests';
import {
  EmployeeResponse,
  JobOfferGeneralResponse,
  OrderResponse,
} from '../../responses/responses';
import { TranslationHelperService } from '../translation-helper/translation-helper.service';
import { JobApplicationFormData } from '../../util-types/util-types';
import { OrderStatus } from '../../enums/order-status.enum';
import { NewsletterMessagesLanguage } from '../../enums/newsletter-messages-language.enum';

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
    }).then((result) => result.isConfirmed);
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

  showSuccessfulBeverageUpdateAlert(beverageNameTranslated: string): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie zaktualizowano napoj '${beverageNameTranslated}'!`
          : `Successfully updated beverage '${beverageNameTranslated}'!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showSuccessfulAddonUpdateAlert(addonNameTranslated: string): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie zaktualizowano dodatek '${addonNameTranslated}'!`
          : `Successfully updated addon '${addonNameTranslated}'!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showSuccessfulMealUpdateAlert(mealNameTranslated: string): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie zaktualizowano danie '${mealNameTranslated}'!`
          : `Successfully updated meal '${mealNameTranslated}'!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showRemoveBeverageAlert(beverageNameTranslated: string): Promise<boolean> {
    return Swal.fire({
      title:
        this.langService.currentLang === 'pl'
          ? 'Potwierdzenie'
          : 'Confirmation',
      text:
        this.langService.currentLang === 'pl'
          ? `Czy na pewno chcesz usunac napoj ${beverageNameTranslated}?`
          : `Are you sure you want to remove beverage ${beverageNameTranslated}?`,
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

  showSuccessfulBeverageRemoveAlert(beverageNameTranslated: string): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie usunieto napoj '${beverageNameTranslated}'!`
          : `Successfully removed beverage '${beverageNameTranslated}'!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showRemoveAddonAlert(addonNameTranslated: string): Promise<boolean> {
    return Swal.fire({
      title:
        this.langService.currentLang === 'pl'
          ? 'Potwierdzenie'
          : 'Confirmation',
      text:
        this.langService.currentLang === 'pl'
          ? `Czy na pewno chcesz usunac dodatek ${addonNameTranslated}?`
          : `Are you sure you want to remove addon ${addonNameTranslated}?`,
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

  showSuccessfulAddonRemoveAlert(addonNameTranslated: string): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie usunieto dodatek '${addonNameTranslated}'!`
          : `Successfully removed addon '${addonNameTranslated}'!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showRemoveMealAlert(mealNameTranslated: string): Promise<boolean> {
    return Swal.fire({
      title:
        this.langService.currentLang === 'pl'
          ? 'Potwierdzenie'
          : 'Confirmation',
      text:
        this.langService.currentLang === 'pl'
          ? `Czy na pewno chcesz usunac danie ${mealNameTranslated}?`
          : `Are you sure you want to remove meal ${mealNameTranslated}?`,
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

  showSuccessfulMealRemoveAlert(mealNameTranslated: string): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie usunieto danie '${mealNameTranslated}'!`
          : `Successfully removed meal '${mealNameTranslated}'!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showRemoveIngredientAlert(
    ingredientNameTranslated: string,
    mealsWithThisIngredient: string
  ): Promise<boolean> {
    return Swal.fire({
      title:
        this.langService.currentLang === 'pl'
          ? 'Potwierdzenie'
          : 'Confirmation',
      text:
        this.langService.currentLang === 'pl'
          ? `Czy na pewno chcesz usunac skladnik ${ingredientNameTranslated}? Nastepujace dania zawieraja ten skladnik: ${mealsWithThisIngredient}`
          : `Are you sure you want to remove ingredient ${ingredientNameTranslated}? Following meals contains this ingredient: ${mealsWithThisIngredient}`,
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

  showSuccessfulIngredientRemoveAlert(ingredientNameTranslated: string): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie usunieto skladnik '${ingredientNameTranslated}'!`
          : `Successfully removed ingredient '${ingredientNameTranslated}'!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showSuccessfulBeverageAddAlert(beverageName: string): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie dodano napoj '${beverageName}'!`
          : `Successfully added beverage '${beverageName}'!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showSuccessfulAddonAddAlert(addonName: string): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie dodano dodatek '${addonName}'!`
          : `Successfully added addon '${addonName}'!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showSuccessfulMealAddAlert(mealName: string): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie dodano danie '${mealName}'!`
          : `Successfully added meal '${mealName}'!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showSuccessfulIngredientAddAlert(ingredientName: string): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie dodano skladnik '${ingredientName}'!`
          : `Successfully added ingredient '${ingredientName}'!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showSuccessfulOpeningHourUpdateAlert(translatedDayOfWeek: string): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie zapisano godziny otwarcia w ${translatedDayOfWeek}!`
          : `Successfully saved opening hours on ${translatedDayOfWeek}!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showRemoveOrderAlert(order: OrderResponse): Promise<boolean> {
    return Swal.fire({
      title:
        this.langService.currentLang === 'pl'
          ? 'Potwierdzenie'
          : 'Confirmation',
      text:
        this.langService.currentLang === 'pl'
          ? `Czy na pewno chcesz usunac zamowienie z id '${order.id}'?`
          : `Are you sure you want to remove order with id '${order.id}'?`,
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

  showSuccessfulOrderRemoveAlert(order: OrderResponse): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie usunieto zamowienie z id '${order.id}'!`
          : `Successfully removed order with id '${order.id}'!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showUpdateOrderAlert(order: OrderResponse): Promise<any> {
    const hasAddress =
      order.street && order.house_number && order.postal_code && order.city;

    const confirmButtonText =
      this.langService.currentLang === 'pl' ? 'Zapisz' : 'Save';
    const cancelButtonText =
      this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';

    const streetPlaceholder =
      this.langService.currentLang === 'pl' ? 'Ulica' : 'Street';
    const houseNumberPlaceholder =
      this.langService.currentLang === 'pl' ? 'Numer domu' : 'House number';
    const postalCodePlaceholder =
      this.langService.currentLang === 'pl' ? 'Kod pocztowy' : 'Postal code';
    const cityPlaceholder =
      this.langService.currentLang === 'pl' ? 'Miasto' : 'City';
    const additionalCommentsPlaceholder =
      this.langService.currentLang === 'pl'
        ? 'Dodatkowe komentarze'
        : 'Additional comments';

    return Swal.fire({
      title:
        this.langService.currentLang === 'pl'
          ? `<span style="color: red;">Aktualizuj zamowienie</span>`
          : `<span style="color: red;">Update order</span>`,
      background: '#141414',
      color: 'white',
      html: `
            <style>
              .form-group {
                display: flex;
                flex-direction: column;
                align-items: center;
                width: 100%;
                max-width: 400px;
                margin: 10px auto;
              }
    
              .form-group label {
                color: white;
                margin-bottom: 5px;
                text-align:center;
              }
    
              input[type="text"], input[type="number"], textarea {
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
    
              input[type="text"]:focus, input[type="number"]:focus, textarea:focus {
                  border: 1px solid red; 
              }
    
              select {
                  width: 100%;
                  text-align: center;
                  color: white;
                  border: none;
                  border-bottom: 1px solid white;
                  outline: none;
                  background: inherit;
                  transition: border-bottom 0.3s ease;
                  cursor: pointer;
    
                  &:focus {
                      border-bottom: 1px solid red;
                  }
              }
    
              option {
                  text-align: center;
                  background: black;
              }
            </style>
    
            <div class="form-group">
              <label>Status</label>
              <select id="status">
                ${Object.values(OrderStatus)
                  .map(
                    (status) =>
                      `<option value="${status}" ${
                        order.order_status === status ? 'selected' : ''
                      }>${this.translationHelper.getTranslatedOrderStatus(
                        status
                      )}</option>`
                  )
                  .join('')}
              </select>
            </div>
    
            ${
              hasAddress
                ? `
              <div class="form-group">
                <label>${streetPlaceholder}</label>
                <input type="text" id="street" maxlength="25" value="${
                  order.street || ''
                }" placeholder="${streetPlaceholder}">
              </div>
    
              <div class="form-group">
                <label>${houseNumberPlaceholder}</label>
                <input type="number" id="house_number" value="${
                  order.house_number || ''
                }" placeholder="${houseNumberPlaceholder}">
              </div>
    
              <div class="form-group">
                <label>${postalCodePlaceholder}</label>
                <input type="text" id="postal_code" maxlength="6" value="${
                  order.postal_code || ''
                }" placeholder="${postalCodePlaceholder}">
              </div>
    
              <div class="form-group">
                <label>${cityPlaceholder}</label>
                <input type="text" id="city" maxlength="25" value="${
                  order.city || ''
                }" placeholder="${cityPlaceholder}">
              </div>
            `
                : ''
            }
    
            <div class="form-group">
              <label>${additionalCommentsPlaceholder}</label>
              <textarea id="comments">${
                order.additional_comments || ''
              }</textarea>
            </div>
            
          `,
      focusConfirm: false,
      showCancelButton: true,
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
      confirmButtonText: confirmButtonText,
      confirmButtonColor: '#198754',
      customClass: {
        validationMessage: 'custom-validation-message',
      },
      preConfirm: () => {
        const street = (document.getElementById('street') as HTMLInputElement)
          ?.value;
        const houseNumber = Number(
          (document.getElementById('house_number') as HTMLInputElement)?.value
        );
        const postalCode = (
          document.getElementById('postal_code') as HTMLInputElement
        )?.value;
        const city = (document.getElementById('city') as HTMLInputElement)
          ?.value;

        if (hasAddress) {
          if (!street || !houseNumber || !postalCode || !city) {
            Swal.showValidationMessage(
              this.langService.currentLang === 'pl'
                ? 'Wszystkie pola sa wymagane'
                : 'All fields are required'
            );
            return null;
          }

          if (street.length > 25) {
            Swal.showValidationMessage(
              this.langService.currentLang === 'pl'
                ? 'Niepoprawna ulica!'
                : 'Invalid street!'
            );
            return null;
          }

          if (Number(houseNumber) < 1) {
            Swal.showValidationMessage(
              this.langService.currentLang === 'pl'
                ? 'Niepoprawny numer domu!'
                : 'Invalid house number!'
            );
            return null;
          }

          const postalCodeRegex = /^[0-9]{2}-[0-9]{3}$/;
          if (!postalCodeRegex.test(postalCode)) {
            Swal.showValidationMessage(
              this.langService.currentLang === 'pl'
                ? 'Niepoprawny kod pocztowy!'
                : 'Invalid postal code!'
            );
            return null;
          }

          if (city.length > 25) {
            Swal.showValidationMessage(
              this.langService.currentLang === 'pl'
                ? 'Niepoprawne miasto!'
                : 'Invalid city!'
            );
            return null;
          }
        }

        const updatedOrder: UpdatedOrderRequest = {
          id: order.id,
          updated_order_status: (
            document.getElementById('status') as HTMLSelectElement
          )?.value as OrderStatus,
          updated_street: street || null,
          updated_house_number: Number(houseNumber) || null,
          updated_postal_code: postalCode || null,
          updated_city: city || null,
          updated_additional_comments:
            (document.getElementById('comments') as HTMLTextAreaElement)
              ?.value || null,
        };

        return updatedOrder;
      },
    }).then((result) => result);
  }

  showSuccessfulOrderUpdateAlert(order: OrderResponse): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie zaktualizowano zamowienie z id '${order.id}'!`
          : `Successfully updated order with id '${order.id}'!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showSuccessfulMealPromotionAddAlert(): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie dodano nowa promocje na dania'!`
          : `Successfully added new meal promotion!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showSuccessfulBeveragePromotionAddAlert(): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie dodano nowa promocje na napoje'!`
          : `Successfully added new beverage promotion!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showSuccessfulAddonPromotionAddAlert(): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie dodano nowa promocje na dodatek'!`
          : `Successfully added new addon promotion!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showSuccessfulMealPromotionUpdateAlert(): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? 'Pomyslnie zaktualizowano promocje na dania!'
          : 'Successfully updated meal promotion!',
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showSuccessfulBeveragePromotionUpdateAlert(): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? 'Pomyslnie zaktualizowano promocje na napoje!'
          : 'Successfully updated beverage promotion!',
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showSuccessfulAddonPromotionUpdateAlert(): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? 'Pomyslnie zaktualizowano promocje na dodatki!'
          : 'Successfully updated addon promotion!',
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showRemoveMealPromotionAlert(): Promise<boolean> {
    return Swal.fire({
      title:
        this.langService.currentLang === 'pl'
          ? 'Potwierdzenie'
          : 'Confirmation',
      text:
        this.langService.currentLang === 'pl'
          ? `Czy na pewno chcesz usunac ta promocje na dania?`
          : `Are you sure you want to remove this meal promotion?`,
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

  showSuccessfulMealPromotionRemoveAlert(): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie usunieto promocje na dania!`
          : `Successfully removed meal promotion!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showRemoveBeveragePromotionAlert(): Promise<boolean> {
    return Swal.fire({
      title:
        this.langService.currentLang === 'pl'
          ? 'Potwierdzenie'
          : 'Confirmation',
      text:
        this.langService.currentLang === 'pl'
          ? `Czy na pewno chcesz usunac ta promocje na napoje?`
          : `Are you sure you want to remove this beverage promotion?`,
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

  showSuccessfulBeveragePromotionRemoveAlert(): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie usunieto promocje na napoje!`
          : `Successfully removed beverage promotion!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showRemoveAddonPromotionAlert(): Promise<boolean> {
    return Swal.fire({
      title:
        this.langService.currentLang === 'pl'
          ? 'Potwierdzenie'
          : 'Confirmation',
      text:
        this.langService.currentLang === 'pl'
          ? `Czy na pewno chcesz usunac ta promocje na dodatki?`
          : `Are you sure you want to remove this addon promotion?`,
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

  showSuccessfulAddonPromotionRemoveAlert(): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie usunieto promocje na dodatki!`
          : `Successfully removed addon promotion!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  async getNewsletterEmailAlert(firstName: string): Promise<string> {
    const title =
      this.langService.currentLang === 'pl'
        ? `Milo Cie poznac ${firstName}!`
        : `Nice to meet you ${firstName}!`;
    const inputLabel =
      this.langService.currentLang === 'pl'
        ? `Podaj swoj email`
        : `Enter your email`;
    const inputPlaceholder =
      this.langService.currentLang === 'pl' ? `Twoj email` : `Your email`;
    const cancelButtonText =
      this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';
    const errorText =
      this.langService.currentLang === 'pl'
        ? 'Podaj poprawny adres email'
        : 'Please enter a valid email';
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    const { value: email } = await Swal.fire({
      title: title,
      input: 'email',
      inputLabel: inputLabel,
      inputPlaceholder: inputPlaceholder,
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
      confirmButtonColor: '#198754',
      showCancelButton: true,
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
      customClass: {
        validationMessage: 'custom-validation-message',
      },
      inputValidator: (value) => {
        if (!value || !emailRegex.test(value)) {
          return errorText;
        }

        return null;
      },
    });

    return email;
  }

  async getNewsletterFirstNameAlert(): Promise<string> {
    const title = this.langService.currentLang === 'pl' ? `Czesc!` : `Hi!`;
    const text =
      this.langService.currentLang === 'pl'
        ? `Podaj swoje imie :)`
        : `Enter your name :)`;
    const inputPlaceholder =
      this.langService.currentLang === 'pl'
        ? `Podaj swoje imie :)`
        : `Enter your name :)`;
    const cancelButtonText =
      this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';

    const { value: firstName } = await Swal.fire({
      title: title,
      text: text,
      input: 'text',
      inputPlaceholder: inputPlaceholder,
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
      confirmButtonColor: '#198754',
      showCancelButton: true,
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
    });

    return firstName;
  }

  async getNewsletterLanguageAlert(
    firstName: string
  ): Promise<NewsletterMessagesLanguage> {
    const text =
      this.langService.currentLang === 'pl'
        ? `Wybierz jezyk, w jakim chcesz otrzymywac wiadomosci!`
        : `Choose language, in which you would like to receive messages!`;
    const cancelButtonText =
      this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';
    const errorText =
      this.langService.currentLang === 'pl'
        ? 'Prosze wybrac jezyk'
        : 'Please select a language';

    const inputOptions = new Promise((resolve) => {
      resolve({
        POLISH:
          this.langService.currentLang === 'pl' ? `🇵🇱 Polski` : `🇵🇱 Polish`,
        ENGLISH:
          this.langService.currentLang === 'pl' ? `🇺🇸 Angielski` : `🇺🇸 English`,
      });
    });

    const { value: language } = await Swal.fire({
      title: `${firstName}!`,
      text: text,
      input: 'radio',
      inputOptions,
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
      confirmButtonColor: '#198754',
      showCancelButton: true,
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
      customClass: {
        validationMessage: 'custom-validation-message',
        input: 'swal-radio-container',
      },
      inputValidator: (value) => {
        if (!value) {
          return errorText;
        }
        return null;
      },
    });

    return language;
  }

  async getNewsletterOtpAlert(title: string, text: string): Promise<any> {
    const cancelButtonText =
      this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';
    const regenerateButtonText =
      this.langService.currentLang === 'pl' ? 'Nowy kod' : 'New code';
    const inputPlaceholder =
      this.langService.currentLang === 'pl' ? `6-cyfrowy kod` : `6-digit code`;

    return await Swal.fire({
      allowOutsideClick: false,
      title: title,
      input: 'text',
      text: text,
      inputPlaceholder: inputPlaceholder,
      confirmButtonColor: 'green',
      showCancelButton: true,
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
      showDenyButton: true,
      denyButtonText: regenerateButtonText,
      denyButtonColor: '#007bff',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
      inputValidator: (value) => {
        if (!value) {
          return this.langService.currentLang === 'pl'
            ? 'Musisz wprowadzic kod!'
            : 'You need to enter the code!';
        }

        if (value.length !== 6) {
          return this.langService.currentLang === 'pl'
            ? 'Kod musi miec dokladnie 6 cyfr!'
            : 'Code must be exactly 6 digits!';
        }

        if (!/^\d+$/.test(value)) {
          return this.langService.currentLang === 'pl'
            ? 'Kod moze zawierac tylko cyfry!'
            : 'Code can only contain digits!';
        }

        return null;
      },
      customClass: {
        validationMessage: 'custom-validation-message',
      },
    });
  }

  showSubscribeNewsletterErrorAlert(error: any): void {
    const title =
      this.langService.currentLang === 'pl' ? 'Wystapil blad' : 'Error occured';

    Swal.fire({
      title: title,
      text: error.errorMessages.message,
      icon: 'error',
      iconColor: 'red',
      confirmButtonColor: 'red',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showSuccessfulNewsletterSubscribeAlert(): void {
    const title =
      this.langService.currentLang === 'pl'
        ? 'Subskrypcja potwierdzona!'
        : 'Subscription confirmed!';
    const text =
      this.langService.currentLang === 'pl'
        ? 'Dziekujemy, ze chcesz byc czescia kebabowej spolecznosci! Mamy nadzieje, ze przyszle promocje Cie zainteresuja :)'
        : 'Thank you for your desire to be part of the kebab community! We hope that future promotions will be interesting for you :)';

    Swal.fire({
      title: title,
      text: text,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }

  showStopTrackingOrderAlert(id: number | null): void {
    Swal.fire({
      text:
        this.langService.currentLang === 'pl'
          ? `Pomyslnie zaprzestano sledzic zamowienie z id '${id}'!`
          : `Successfully stopped tracking order with id '${id}'!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });
  }
}
