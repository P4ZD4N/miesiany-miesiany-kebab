import { Pipe, PipeTransform } from '@angular/core';
import { JobOfferGeneralResponse } from '../responses/responses';
import { TranslationHelperService } from '../services/translation-helper/translation-helper.service';

@Pipe({
  name: 'sortJobOffersByPosition',
  standalone: true
})
export class SortJobOffersByPositionPipe implements PipeTransform {
  constructor(private translationHelper: TranslationHelperService) {}

  transform(jobOffers: JobOfferGeneralResponse[]): JobOfferGeneralResponse[] {
    if (!jobOffers || !Array.isArray(jobOffers)) return [];

    return jobOffers.sort((a, b) => {
      const firstPositionNameTranslated = this.translationHelper.getTranslatedPosition(a.position_name);
      const secondPositionNameTranslated = this.translationHelper.getTranslatedPosition(b.position_name);

      return firstPositionNameTranslated.localeCompare(secondPositionNameTranslated);
    });
  }
}
