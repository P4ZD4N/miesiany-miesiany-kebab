import { Injectable } from '@angular/core';
import { ContactType } from '../../enums/contact-type.enum';
import { TranslateService } from '@ngx-translate/core';
import { OrderStatus } from '../../enums/order-status.enum';

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

  getTranslatedPosition(position: string): string {
    let positionTranslated = this.translate.instant('jobs.offers.' + position);

    if (positionTranslated === 'jobs.offers.' + position) {
      positionTranslated = position;
    }

    return positionTranslated;
  }

  getTranslatedMealName(mealName: string): string {
    let mealNameTranslated = this.translate.instant('menu.meals.' + mealName);

    if (mealNameTranslated === 'menu.meals.' + mealName) {
      mealNameTranslated = mealName;
    }

    return mealNameTranslated;
  }

  getTranslatedBeverageName(beverageName: string): string {
    let beverageNameTranslated = this.translate.instant(
      'menu.beverages.' + beverageName
    );

    if (beverageNameTranslated === 'menu.addons.' + beverageName) {
      beverageNameTranslated = beverageName;
    }

    return beverageNameTranslated;
  }

  getTranslatedAddonName(addonName: string): string {
    let addonNameTranslated = this.translate.instant(
      'menu.addons.' + addonName
    );

    if (addonNameTranslated === 'menu.addons.' + addonName) {
      addonNameTranslated = addonName;
    }

    return addonNameTranslated;
  }

  getTranslatedIngredientName(ingredientName: string): string {
    let ingredientNameTranslated = this.translate.instant(
      'menu.meals.ingredients.' + ingredientName
    );

    if (
      ingredientNameTranslated ===
      'menu.meals.ingredients.' + ingredientName
    ) {
      ingredientNameTranslated = ingredientName;
    }

    return ingredientNameTranslated;
  }

  getTranslatedOrderStatus(orderStatus: OrderStatus): string {
    let orderStatusTranslated = this.translate.instant(
      'order-management.order_status.' + orderStatus
    );

    if (
      orderStatusTranslated ===
      'order-management.order_status.' + orderStatus
    ) {
      orderStatusTranslated = orderStatus;
    }

    return orderStatusTranslated;
  }
}
