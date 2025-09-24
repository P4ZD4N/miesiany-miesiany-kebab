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

  getTranslatedJobName(jobName: string): string {
    let jobNameTranslated = this.translate.instant(
      'employee-management.jobs.' + jobName
    );

    if (jobNameTranslated === 'employee-management.jobs.' + jobName) {
      jobNameTranslated = jobName;
    }

    return jobNameTranslated;
  }

  getTranslatedEmploymentType(employmentType: string): string {
    let employmentTypeTranslated = this.translate.instant(
      'employee-management.employment_types.' + employmentType
    );

    if (
      employmentTypeTranslated ===
      'employee-management.employment_types.' + employmentType
    ) {
      employmentTypeTranslated = employmentType;
    }

    return employmentTypeTranslated;
  }
}
