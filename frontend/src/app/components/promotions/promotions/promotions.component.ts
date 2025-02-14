import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { PromotionsService } from '../../../services/promotions/promotions.service';
import { LangService } from '../../../services/lang/lang.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { AddonPromotionResponse, BeveragePromotionResponse, MealPromotionResponse, MealResponse } from '../../../responses/responses';
import { CommonModule } from '@angular/common';
import { NewMealPromotionRequest } from '../../../requests/requests';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Size } from '../../../enums/size.enum';
import { MenuService } from '../../../services/menu/menu.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-promotions',
  standalone: true,
  imports: [CommonModule, TranslateModule, ReactiveFormsModule, FormsModule],
  templateUrl: './promotions.component.html',
  styleUrl: './promotions.component.scss'
})
export class PromotionsComponent implements OnInit {

  mealNames: string[] = []
  sizeOrder: Size[] = [Size.SMALL, Size.MEDIUM, Size.LARGE, Size.XL];

  newMealPromotion: NewMealPromotionRequest = {
    description: '',
    sizes: [],
    discount_percentage: 0,
    meal_names: []
  };

  mealPromotions: MealPromotionResponse[] = [];
  beveragePromotions: BeveragePromotionResponse[] = [];
  addonPromotions: AddonPromotionResponse[] = []
  errorMessages: { [key: string]: string } = {};
  languageChangeSubscription: Subscription;

  isAddingMealPromotion = false;
  isAdding = false;

  constructor(
      private authenticationService: AuthenticationService, 
      private promotionsService: PromotionsService,
      private langService: LangService,
      private translate: TranslateService,
      private menuService: MenuService, 
    ) {
      this.languageChangeSubscription = this.langService.languageChanged$.subscribe(() => {
        this.hideErrorMessages();
      })
    }

  ngOnInit(): void {
    this.loadMealPromotions();
    this.loadBeveragePromotions();
    this.loadAddonPromotions();
    this.loadMealNames();
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

  loadMealNames(): void {
    this.menuService.getMeals().subscribe(
      (data: MealResponse[]) => {
        this.mealNames = data.map(meal => meal.name)
      },
      error => {
        this.handleError(error);
      },
    );
  }

  showAddMealPromotionTable(): void {
    this.hideErrorMessages();
    this.isAddingMealPromotion = true;
    this.isAdding = true;
  }

  addMealPromotion(): void {
    this.promotionsService.addMealPromotion(this.newMealPromotion).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? `Pomyslnie dodano nowa promocje na dania'!` : `Successfully added new meal promotion!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
        });

        this.loadMealPromotions();
        this.resetNewMealPromotion();
        this.hideAddMealPromotionTable();
        this.hideErrorMessages();
      },
      error: (error) => {
        this.handleError(error);
      },
    });
  }

  hideAddMealPromotionTable(): void {
    this.hideErrorMessages();
    this.isAddingMealPromotion = false;
    this.isAdding = false;
    this.resetNewMealPromotion();
  }

  resetNewMealPromotion(): void {
    this.newMealPromotion = {
      description: '',
      sizes: [],
      discount_percentage: 0,
      meal_names: []
    };
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
      console.log(this.errorMessages);
    } else {
      this.errorMessages = { general: 'An unexpected error occurred' };
    }
  }

  toggleSize(size: Size, event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    
    if (checked) this.newMealPromotion.sizes.push(size);
    else this.newMealPromotion.sizes = this.newMealPromotion.sizes.filter(s => s !== size);
  }

  toggleMealName(mealName: string, event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    
    if (checked) this.newMealPromotion.meal_names.push(mealName);
    else this.newMealPromotion.meal_names = this.newMealPromotion.meal_names.filter(mn => mn !== mealName);
  }
}
