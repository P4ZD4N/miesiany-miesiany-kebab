import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';
import { LangService } from '../lang/lang.service';
import { AuthenticationRequest } from '../../requests/requests';

@Injectable({
  providedIn: 'root',
})
export class AlertService {
  constructor(private langService: LangService) {}

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
}
