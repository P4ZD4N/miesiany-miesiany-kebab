import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';
import { LangService } from '../lang/lang.service';
import { AuthenticationRequest, RemovedDiscountCodeRequest } from '../../requests/requests';
import { DiscountCodesService } from '../discount-codes/discount-codes.service';

@Injectable({
  providedIn: 'root',
})
export class AlertService {
  constructor(
    private langService: LangService,
    private discountCodesService: DiscountCodesService
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
    }).then(result => result.isConfirmed);
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
}
