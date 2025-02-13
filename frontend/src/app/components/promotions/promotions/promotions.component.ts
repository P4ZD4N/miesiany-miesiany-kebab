import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { PromotionsService } from '../../../services/promotions/promotions.service';
import { LangService } from '../../../services/lang/lang.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { AddonPromotionResponse, BeveragePromotionResponse, MealPromotionResponse } from '../../../responses/responses';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-promotions',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './promotions.component.html',
  styleUrl: './promotions.component.scss'
})
export class PromotionsComponent implements OnInit {

  mealPromotions: MealPromotionResponse[] = [];
  beveragePromotions: BeveragePromotionResponse[] = [];
  addonPromotions: AddonPromotionResponse[] = []
  errorMessages: { [key: string]: string } = {};
  languageChangeSubscription: Subscription;

  constructor(
      private authenticationService: AuthenticationService, 
      private promotionsService: PromotionsService,
      private langService: LangService,
      private translate: TranslateService
    ) {
      this.languageChangeSubscription = this.langService.languageChanged$.subscribe(() => {
        this.hideErrorMessages();
      })
    }

  ngOnInit(): void {
    this.loadMealPromotions();
    this.loadBeveragePromotions();
    this.loadAddonPromotions();
  }

  loadMealPromotions(): void {
    this.promotionsService.getMealPromotions().subscribe(
      (data: MealPromotionResponse[]) => {
        this.mealPromotions = data;
      },
      error => {
        this.handleError(error);
      },
    );
  }

  loadBeveragePromotions(): void {
    this.promotionsService.getBeveragePromotions().subscribe(
      (data: BeveragePromotionResponse[]) => {
        this.beveragePromotions = data;
      },
      error => {
        this.handleError(error);
      },
    );
  }

  loadAddonPromotions(): void {
    this.promotionsService.getAddonPromotions().subscribe(
      (data: AddonPromotionResponse[]) => {
        this.addonPromotions = data;
      },
      error => {
        this.handleError(error);
      },
    );
  }

  isManager(): boolean {
    return this.authenticationService.isManager();
  }

  hideErrorMessages(): void {
    this.errorMessages = {};
  }

  handleError(error: any) {
    if (error.errorMessages) {
      this.errorMessages = error.errorMessages;
    } else {
      this.errorMessages = { general: 'An unexpected error occurred' };
    }
  }
}
