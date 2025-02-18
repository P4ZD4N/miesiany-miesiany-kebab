import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { PromotionsService } from '../../../services/promotions/promotions.service';
import { LangService } from '../../../services/lang/lang.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { AddonPromotionResponse, BeveragePromotionResponse, BeverageResponse, MealPromotionResponse, MealResponse } from '../../../responses/responses';
import { CommonModule } from '@angular/common';
import { NewBeveragePromotionRequest, NewMealPromotionRequest, RemovedBeveragePromotionRequest, RemovedMealPromotionRequest, UpdatedBeveragePromotionRequest, UpdatedMealPromotionRequest } from '../../../requests/requests';
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
  beveragesWithCapacities: { [key in string]: number[] } = {}
  beveragesWithCapacitiesEntries: [string, number[]][] = [];
  sizeOrder: Size[] = [Size.SMALL, Size.MEDIUM, Size.LARGE, Size.XL];

  newMealPromotion: NewMealPromotionRequest = {
    description: '',
    sizes: [],
    discount_percentage: 0,
    meal_names: []
  };

  newBeveragePromotion: NewBeveragePromotionRequest = {
    description: '',
    discount_percentage: 0,
    beverages_with_capacities: {}
  };

  mealPromotions: MealPromotionResponse[] = [];
  beveragePromotions: BeveragePromotionResponse[] = [];
  addonPromotions: AddonPromotionResponse[] = []
  errorMessages: { [key: string]: string } = {};
  languageChangeSubscription: Subscription;

  mealPromotionForms: { [key: string]: FormGroup } = {};

  currentlyEditedPromotion: MealPromotionResponse | BeveragePromotionResponse | null = null;
  updatedMealPromotion: UpdatedMealPromotionRequest | null = null;
  updatedBeveragePromotion: UpdatedBeveragePromotionRequest | null = null;

  isAddingMealPromotion = false;
  isEditingMealPromotion = false;
  isAddingBeveragePromotion = false;
  isEditingBeveragePromotion = false;
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
    this.loadBeveragesWithCapacities();
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

  loadBeveragesWithCapacities(): void {
    this.menuService.getBeverages().subscribe(
      (data: BeverageResponse[]) => {
        data.forEach(b => {
          if (!this.beveragesWithCapacities[b.name]) {
            this.beveragesWithCapacities[b.name] = []
          }

          this.beveragesWithCapacities[b.name].push(b.capacity)
        });

        this.beveragesWithCapacitiesEntries = Object.entries(this.beveragesWithCapacities).sort((a, b) => a[0].localeCompare(b[0]));
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

  showAddBeveragePromotionTable(): void {
    this.hideErrorMessages();
    this.isAddingBeveragePromotion = true;
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

  addBeveragePromotion(): void {
    this.promotionsService.addBeveragePromotion(this.newBeveragePromotion).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? `Pomyslnie dodano nowa promocje na napoje'!` : `Successfully added new beverage promotion!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
        });

        this.loadBeveragePromotions();
        this.resetNewBeveragePromotion();
        this.hideAddBeveragePromotionTable();
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

  hideAddBeveragePromotionTable(): void {
    this.hideErrorMessages();
    this.isAddingBeveragePromotion = false;
    this.isAdding = false;
    this.resetNewBeveragePromotion();
  }

  resetNewMealPromotion(): void {
    this.newMealPromotion = {
      description: '',
      sizes: [],
      discount_percentage: 0,
      meal_names: []
    };
  }

  resetNewBeveragePromotion(): void {
    this.newBeveragePromotion = {
      description: '',
      discount_percentage: 0,
      beverages_with_capacities: {}
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

  toggleCapacityAdd(beverageName: string, capacity: number, event: Event) {
    const checked = (event.target as HTMLInputElement).checked;

    if (checked) {
      if (!this.newBeveragePromotion.beverages_with_capacities[beverageName]) {
        this.newBeveragePromotion.beverages_with_capacities[beverageName] = [];
      }

      this.newBeveragePromotion.beverages_with_capacities[beverageName].push(capacity);
    } else this.newBeveragePromotion.beverages_with_capacities[beverageName] = this.newBeveragePromotion.beverages_with_capacities[beverageName].filter(c => c !== capacity);
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

  toggleCapacityEdit(beverageName: string, capacity: number, event: Event) {
    if (!this.updatedBeveragePromotion) return;
    
    const checked = (event.target as HTMLInputElement).checked;
    
    if (checked) {
      if (!this.updatedBeveragePromotion.updated_beverages_with_capacities[beverageName]) {
        this.updatedBeveragePromotion.updated_beverages_with_capacities[beverageName] = [];
      }

      this.updatedBeveragePromotion.updated_beverages_with_capacities[beverageName].push(capacity);
    } else this.updatedBeveragePromotion.updated_beverages_with_capacities[beverageName] = this.updatedBeveragePromotion.updated_beverages_with_capacities[beverageName].filter(c => c !== capacity);
  }

  editMealPromotinRow(mealPromotion: MealPromotionResponse) {
    if (this.isEditing) {
      return;
    }

    this.hideErrorMessages();
    this.isEditing = true;
    this.isEditingMealPromotion = true;
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

  editBeveragePromotinRow(beveragePromotion: BeveragePromotionResponse) {
    if (this.isEditing) {
      return;
    }

    this.hideErrorMessages();
    this.isEditing = true;
    this.isEditingBeveragePromotion = true;
    this.currentlyEditedPromotion = beveragePromotion;
    beveragePromotion.isEditing = true;

    this.updatedBeveragePromotion = {
      id: beveragePromotion.id,
      updated_description: beveragePromotion.description,
      updated_discount_percentage: beveragePromotion.discount_percentage,
      updated_beverages_with_capacities: {...beveragePromotion.beverages_with_capacities}
    };
  }

  updateMealPromotion(updatedPromotion: UpdatedMealPromotionRequest) {
    if (!updatedPromotion || !this.currentlyEditedPromotion) return;
    
    this.promotionsService.updateMealPromotion(updatedPromotion).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? 'Pomyslnie zaktualizowano promocje na dania!' : 'Successfully updated meal promotion!',
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
        });
  
        this.loadMealPromotions();
        this.hideUpdatePromotionTable();
      },
      error: (error) => {
        this.handleError(error);
      }
    });
  }

  updateBeveragePromotion(updatedPromotion: UpdatedBeveragePromotionRequest) {
    if (!updatedPromotion || !this.currentlyEditedPromotion) return;
    
    this.promotionsService.updateBeveragePromotion(updatedPromotion).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? 'Pomyslnie zaktualizowano promocje na napoje!' : 'Successfully updated beverage promotion!',
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
        });
  
        this.loadBeveragePromotions();
        this.hideUpdatePromotionTable();
      },
      error: (error) => {
        this.handleError(error);
      }
    });
  }

  hideUpdatePromotionTable() {
    if (this.currentlyEditedPromotion) {
      this.currentlyEditedPromotion.isEditing = false;
    }
    this.currentlyEditedPromotion = null;
    this.updatedMealPromotion = null;
    this.updatedBeveragePromotion = null;
    this.isEditing = false;
    this.isEditingBeveragePromotion = false;
    this.isEditingMealPromotion = false;
    this.hideErrorMessages();
  }

  removeMealPromotion(mealPromotion: MealPromotionResponse): void {
    const confirmationMessage =
      this.langService.currentLang === 'pl'
        ? `Czy na pewno chcesz usunac ta promocje na dania?`
        : `Are you sure you want to remove this meal promotion?`;

    Swal.fire({
      title: this.langService.currentLang === 'pl' ? 'Potwierdzenie' : 'Confirmation',
      text: confirmationMessage,
      icon: 'warning',
      iconColor: 'red',
      showCancelButton: true,
      confirmButtonColor: '#0077ff',
      cancelButtonColor: 'red',
      background: 'black',
      color: 'white',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Tak' : 'Yes',
      cancelButtonText: this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel',
    }).then((result) => {
      if (result.isConfirmed) {
        this.promotionsService.removeMealPromotion({ id: mealPromotion.id } as RemovedMealPromotionRequest).subscribe(() => {
          Swal.fire({
            text: this.langService.currentLang === 'pl' ? `Pomyslnie usunieto promocje na dania!` : `Successfully removed meal promotion!`,
            icon: 'success',
            iconColor: 'green',
            confirmButtonColor: 'green',
            background: 'black',
            color: 'white',
            confirmButtonText: 'Ok',
          });
          this.loadMealPromotions();
        });
      }
    });
  }

  removeBeveragePromotion(beveragePromotion: BeveragePromotionResponse): void {
    const confirmationMessage =
      this.langService.currentLang === 'pl'
      ? `Czy na pewno chcesz usunac ta promocje na napoje?`
      : `Are you sure you want to remove this beverage promotion?`;

    Swal.fire({
      title: this.langService.currentLang === 'pl' ? 'Potwierdzenie' : 'Confirmation',
      text: confirmationMessage,
      icon: 'warning',
      iconColor: 'red',
      showCancelButton: true,
      confirmButtonColor: '#0077ff',
      cancelButtonColor: 'red',
      background: 'black',
      color: 'white',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Tak' : 'Yes',
      cancelButtonText: this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel',
    }).then((result) => {
      if (result.isConfirmed) {
        this.promotionsService.removeBeveragePromotion({ id: beveragePromotion.id } as RemovedBeveragePromotionRequest).subscribe(() => {
          Swal.fire({
            text: this.langService.currentLang === 'pl' ? `Pomyslnie usunieto promocje na napoje!` : `Successfully removed beverage promotion!`,
            icon: 'success',
            iconColor: 'green',
            confirmButtonColor: 'green',
            background: 'black',
            color: 'white',
            confirmButtonText: 'Ok',
          });
          this.loadBeveragePromotions();
        });
      }
    });
  }

  isMealTranslationAvailable(mealName: string): boolean {
    const translatedName = this.translate.instant('menu.meals.' + mealName);
    return translatedName !== 'menu.meals.' + mealName;
  }

  isBeverageTranslationAvailable(beverageName: string): boolean {
    const translatedName = this.translate.instant('menu.beverages.' + beverageName);
    return translatedName !== 'menu.beverages.' + beverageName;
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
