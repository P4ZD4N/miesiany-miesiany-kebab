import { Injectable } from '@angular/core';
import { ContactType } from '../../enums/contact-type.enum';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
  providedIn: 'root',
})
export class TranslationHelperService {
  constructor(private translate: TranslateService) {}

  getTranslatedContactType(contactType: ContactType): string {
    let contactTypeTranslated = this.translate.instant(
      'contact.types.' + contactType
    );

    if (contactTypeTranslated === 'contact.types.' + contactType) {
      contactTypeTranslated = contactType;
    }

    return contactTypeTranslated;
  }
}
