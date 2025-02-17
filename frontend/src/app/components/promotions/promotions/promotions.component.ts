import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { PromotionsService } from '../../../services/promotions/promotions.service';
import { LangService } from '../../../services/lang/lang.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { AddonPromotionResponse, BeveragePromotionResponse, MealPromotionResponse, MealResponse } from '../../../responses/responses';
import { CommonModule } from '@angular/common';
import { NewMealPromotionRequest, UpdatedMealPromotionRequest } from '../../../requests/requests';
import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
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

  mealPromotionForms: { [key: string]: FormGroup } = {};

  currentlyEditedPromotion: MealPromotionResponse | null = null;
  updatedMealPromotion: UpdatedMealPromotionRequest | null = null;

  isAddingMealPromotion = false;
  isAdding = false;
  isEditing = false;

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

  toggleSize(size: Size, event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    
    if (checked) this.newMealPromotion.sizes.push(size);
    else this.newMealPromotion.sizes = this.newMealPromotion.sizes.filter(s => s !== size);
  }

  toggleMealNameAdd(mealName: string, event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    
    if (checked) this.newMealPromotion.meal_names.push(mealName);
    else this.newMealPromotion.meal_names = this.newMealPromotion.meal_names.filter(mn => mn !== mealName);
  }

  toggleSizeEdit(size: Size, event: Event) {
    if (!this.updatedMealPromotion) return;
    
    const checked = (event.target as HTMLInputElement).checked;
    
    if (checked) {
      this.updatedMealPromotion.updated_sizes.push(size);
    } else {
      this.updatedMealPromotion.updated_sizes = this.updatedMealPromotion.updated_sizes.filter(s => s !== size);
    }
  }

  toggleMealNameEdit(mealName: string, event: Event) {
    if (!this.updatedMealPromotion) return;
    
    const checked = (event.target as HTMLInputElement).checked;
    
    if (checked) {
      this.updatedMealPromotion.updated_meal_names.push(mealName);
    } else {
      this.updatedMealPromotion.updated_meal_names = this.updatedMealPromotion.updated_meal_names.filter(mn => mn !== mealName);
    }
  }

  editMealPromotinRow(mealPromotion: MealPromotionResponse) {
    if (this.isEditing) {
      return;
    }

    this.hideErrorMessages();
    this.isEditing = true;
    this.currentlyEditedPromotion = mealPromotion;
    mealPromotion.isEditing = true;

    this.updatedMealPromotion = {
      id: mealPromotion.id,
      updated_description: mealPromotion.description,
      updated_sizes: [...mealPromotion.sizes],
      updated_discount_percentage: mealPromotion.discount_percentage,
      updated_meal_names: [...mealPromotion.meal_names]
    };
  }

  saveMealPromotionRow(updatedPromotion: UpdatedMealPromotionRequest) {
    if (!updatedPromotion || !this.currentlyEditedPromotion) return;
    
    this.promotionsService.updateMealPromotion(updatedPromotion).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? 'Pomyślnie zaktualizowano promocję!' : 'Successfully updated the promotion!',
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
        });
  
        this.loadMealPromotions();
        this.hideEditableMealPromotionRow(this.currentlyEditedPromotion);
      },
      error: (error) => {
        this.handleError(error);
      }
    });
  }

  hideEditableMealPromotionRow(mealPromotion: MealPromotionResponse | null) {
    if (this.currentlyEditedPromotion) {
      this.currentlyEditedPromotion.isEditing = false;
    }
    this.currentlyEditedPromotion = null;
    this.updatedMealPromotion = null;
    this.isEditing = false;
    this.hideErrorMessages();
  }

  removeMealPromotion(mealPromotion: MealPromotionResponse) {

  }

  isMealTranslationAvailable(mealName: string): boolean {
    const translatedName = this.translate.instant('menu.meals.' + mealName);
    return translatedName !== 'menu.meals.' + mealName;
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
}
