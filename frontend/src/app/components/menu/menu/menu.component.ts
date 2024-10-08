import { Component, OnInit } from '@angular/core';
import { MenuService } from '../../../services/menu/menu.service';
import { CommonModule } from '@angular/common';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { FormArray, FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LangService } from '../../../services/lang/lang.service';
import { Subscription } from 'rxjs';
import Swal from 'sweetalert2';
import { NewAddonRequest, NewBeverageRequest, NewIngredientRequest, NewMealRequest, RemovedAddonRequest, RemovedBeverageRequest, RemovedIngredientRequest, RemovedMealRequest } from '../../../requests/requests';
import { BeverageResponse, AddonResponse, MealResponse, IngredientResponse, RemovedMealResponse } from '../../../responses/responses';
import { Size } from '../../../enums/size.enum';
import { IngredientType } from '../../../enums/ingredient-type.enum';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [CommonModule, TranslateModule, ReactiveFormsModule, FormsModule],
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss'], 
})
export class MenuComponent implements OnInit {

  beverages: BeverageResponse[] = [];
  addons: AddonResponse[] = [];
  meals: MealResponse[] = [];
  ingredients: IngredientResponse[] = [];

  beverageForms: { [key: string]: FormGroup } = {};
  addonForms: { [key: string]: FormGroup } = {};
  mealForms: { [key: string]: FormGroup } = {};
  errorMessages: { [key: string]: string } = {};

  languageChangeSubscription: Subscription;

  isAddingBeverage = false;
  isAddingAddon = false;
  isAddingMeal = false;
  isAddingIngredient = false;
  isEditing = false;

  newBeverage: NewBeverageRequest = {
    new_beverage_name: '',
    new_beverage_capacity: 0,
    new_beverage_price: 0,
  };
  newAddon: NewAddonRequest = {
    new_addon_name: '',
    new_addon_price: 0,
  };
  newMeal: NewMealRequest = {
    new_meal_name: '',
    new_meal_prices: {},
    new_meal_ingredients: []
  };
  newIngredient: NewIngredientRequest = {
    new_ingredient_name: '',
    new_ingredient_type: null
  }

  ingredientTypes: IngredientType[] = [IngredientType.BREAD, IngredientType.MEAT, IngredientType.VEGETABLE, IngredientType.SAUCE, IngredientType.OTHER];
  sizeOrder: Size[] = [Size.SMALL, Size.MEDIUM, Size.LARGE, Size.XL];
  
  selectedBread: string | null = null; 
  selectedVegetables: Set<string> = new Set();
  selectedOthers: Set<string> = new Set();

  constructor(
    private menuService: MenuService,
    private authenticationService: AuthenticationService,
    private formBuilder: FormBuilder,
    private langService: LangService,
    private translate: TranslateService
  ) {
    this.languageChangeSubscription = this.langService.languageChanged$.subscribe(() => {
      this.hideErrorMessages();
    });
  }

  ngOnInit(): void {
    this.loadBeverages();
    this.loadAddons();
    this.loadMeals();
    this.loadIngredients();
  }

  loadBeverages(): void {
    this.menuService.getBeverages().subscribe(
      (data: BeverageResponse[]) => {
        this.beverages = data;
        this.initializeBeverageForms(data);
      },
      (error) => {
        console.log('Error loading beverages', error);
      }
    );
  }

  loadAddons(): void {
    this.menuService.getAddons().subscribe(
      (data: AddonResponse[]) => {
        this.addons = data;
        this.initializeAddonForms(data);
      },
      (error) => {
        console.log('Error loading addons', error);
      }
    );
  }

  loadMeals(): void {
    this.menuService.getMeals().subscribe(
      (data: MealResponse[]) => {
        this.meals = data;
        this.initializeMealForms(data);
      },
      (error) => {
        console.log('Error loading meals', error);
      }
    );
  }

  loadIngredients(): void {
    this.menuService.getIngredients().subscribe(
      (data: IngredientResponse[]) => {
        this.ingredients = data;
      },
      (error) => {
        console.log('Error loading ingredients', error);
      }
    );
  }

  initializeBeverageForms(beverages: BeverageResponse[]): void {
    beverages.forEach((beverage) => {
      this.beverageForms[beverage.name] = this.formBuilder.group({
        capacity: new FormControl(beverage.capacity),
        price: new FormControl(beverage.price),
      });
    });
  }

  initializeAddonForms(addons: AddonResponse[]): void {
    addons.forEach((addon) => {
      this.addonForms[addon.name] = this.formBuilder.group({
        price: new FormControl(addon.price)
      });
    });
  }

  initializeMealForms(meals: MealResponse[]): void {
    meals.forEach((meal) => {
      this.mealForms[meal.name] = this.formBuilder.group({
        prices: this.formBuilder.group({
          SMALL: new FormControl(meal.prices.SMALL),  
          MEDIUM: new FormControl(meal.prices.MEDIUM), 
          LARGE: new FormControl(meal.prices.LARGE),
          XL: new FormControl(meal.prices.XL)
        }),
        ingredients: this.formBuilder.array(
          meal.ingredients.map(ingredient => 
            this.formBuilder.group({
              name: new FormControl(ingredient.name),
              ingredient_type: new FormControl(ingredient.ingredient_type)
            })
          )
        )
      });
    });
  }

  editBeverageRow(beverage: BeverageResponse): void {
    if (this.isEditing) {
      return;
    }

    this.hideErrorMessages();
    this.isEditing = true;
    beverage.isEditing = true;

    const form = this.beverageForms[beverage.name];

    form.patchValue({

      capacity: beverage.capacity,
      price: beverage.price,
    });
  }

  editAddonRow(addon: AddonResponse): void {
    if (this.isEditing) {
      return;
    }

    this.hideErrorMessages();
    this.isEditing = true;
    addon.isEditing = true;

    const form = this.beverageForms[addon.name];

    form.patchValue({
      price: addon.price,
    });
  }

  editMealRow(meal: MealResponse): void {
    if (this.isEditing) {
      return;
    }

    this.hideErrorMessages();
    this.isEditing = true;
    meal.isEditing = true;

    const form = this.mealForms[meal.name];

    form.patchValue({
      prices: this.formBuilder.group({
        small: new FormControl(meal.prices.SMALL),  
        medium: new FormControl(meal.prices.MEDIUM), 
        large: new FormControl(meal.prices.LARGE),
        XL: new FormControl(meal.prices.XL)
      }),
      ingredients: this.formBuilder.array(
        meal.ingredients.map(ingredient => new FormControl(ingredient.name))
      )
    });
  }

  saveBeverageRow(beverage: BeverageResponse): void {
    let beverageNameTranslated = this.translate.instant('menu.beverages.' + beverage.name);

    if (!this.isBeverageTranslationAvailable(beverage.name)) {
      beverageNameTranslated = beverage.name;
    }

    const formGroup = this.beverageForms[beverage.name];
    const newCapacity = formGroup.get('capacity')!.value;
    const newPrice = formGroup.get('price')!.value;

    const updatedBeverage = {
      updated_beverage_name: beverage.name,
      updated_beverage_old_capacity: beverage.capacity,
      updated_beverage_new_capacity: newCapacity,
      updated_beverage_price: newPrice,
    };

    this.menuService.updateBeverage(updatedBeverage).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? `Pomyslnie zaktualizowano napoj '${beverageNameTranslated}'!` : `Successfully updated beverage '${beverageNameTranslated}'!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
        });

        beverage.isEditing = false;
        this.isEditing = false;
        this.hideErrorMessages();
        this.loadBeverages();
      },
      error: (error) => {
        this.handleError(error);
      },
    });
  }

  saveAddonRow(addon: AddonResponse): void {
    let addonNameTranslated = this.translate.instant('menu.addons.' + addon.name);

    if (!this.isAddonTranslationAvailable(addon.name)) {
      addonNameTranslated = addon.name;
    }

    const formGroup = this.addonForms[addon.name];
    const newPrice = formGroup.get('price')!.value;
    const updatedAddon = {
      updated_addon_name: addon.name,
      updated_addon_price: newPrice,
    };

    this.menuService.updateAddon(updatedAddon).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? `Pomyslnie zaktualizowano dodatek '${addonNameTranslated}'!` : `Successfully updated addon '${addonNameTranslated}'!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
        });

        addon.isEditing = false;
        this.isEditing = false;
        this.hideErrorMessages();
        this.loadAddons();
      },
      error: (error) => {
        this.handleError(error);
      },
    });
  }

  saveMealRow(meal: MealResponse): void {
    let mealNameTranslated = this.translate.instant('menu.meals.' + meal.name);

    if (!this.isMealTranslationAvailable(meal.name)) {
      mealNameTranslated = meal.name;
    }
    
    const formGroup = this.mealForms[meal.name];
    const newPrices = formGroup.get('prices')!.value;
    const newIngredients = formGroup.get('ingredients')!.value;

    const filteredNewPrices = Object.entries(newPrices)
    .filter(([size, price]) => typeof price === 'number' && price > 0)
    .reduce((acc, [size, price]) => {
        acc[size] = price as number;
        return acc;
    }, {} as { [key: string]: number }); 

    console.log(newPrices);
    console.log(filteredNewPrices);
    console.log(newIngredients);

    const updatedMeal = {
      updated_meal_name: meal.name,
      updated_meal_prices: filteredNewPrices,
      updated_meal_ingredients: newIngredients
    };

    console.log(updatedMeal);

    this.menuService.updateMeal(updatedMeal).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? `Pomyslnie zaktualizowano danie '${mealNameTranslated}'!` : `Successfully updated meal '${mealNameTranslated}'!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
        });

        meal.isEditing = false;
        this.isEditing = false;
        this.hideErrorMessages();
        this.loadMeals();
      },
      error: (error) => {
        this.handleError(error);
      },
    });
  }

  removeBeverage(beverage: BeverageResponse): void {
    let beverageNameTranslated = this.translate.instant('menu.beverages.' + beverage.name);

    if (!this.isBeverageTranslationAvailable(beverage.name)) {
      beverageNameTranslated = beverage.name;
    }

    const confirmationMessage =
      this.langService.currentLang === 'pl'
        ? `Czy na pewno chcesz usunac napoj ${beverageNameTranslated}?`
        : `Are you sure you want to remove beverage ${beverageNameTranslated}?`;

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
        this.menuService.removeBeverage({ name: beverage.name, capacity: beverage.capacity } as RemovedBeverageRequest).subscribe(() => {
          Swal.fire({
            text: this.langService.currentLang === 'pl' ? `Pomyslnie usunieto napoj '${beverageNameTranslated}'!` : `Successfully removed beverage '${beverageNameTranslated}'!`,
            icon: 'success',
            iconColor: 'green',
            confirmButtonColor: 'green',
            background: 'black',
            color: 'white',
            confirmButtonText: 'Ok',
          });
          this.loadBeverages();
        });
      }
    });
  }

  removeAddon(addon: AddonResponse): void {
    let addonNameTranslated = this.translate.instant('menu.addons.' + addon.name);

    if (!this.isAddonTranslationAvailable(addon.name)) {
      addonNameTranslated = addon.name;
    }

    const confirmationMessage =
      this.langService.currentLang === 'pl'
        ? `Czy na pewno chcesz usunac dodatek ${addonNameTranslated}?`
        : `Are you sure you want to remove addon ${addonNameTranslated}?`;

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
        this.menuService.removeAddon({ name: addon.name } as RemovedAddonRequest).subscribe(() => {
          Swal.fire({
            text: this.langService.currentLang === 'pl' ? `Pomyslnie usunieto dodatek '${addonNameTranslated}'!` : `Successfully removed addon '${addonNameTranslated}'!`,
            icon: 'success',
            iconColor: 'green',
            confirmButtonColor: 'green',
            background: 'black',
            color: 'white',
            confirmButtonText: 'Ok',
          });
          this.loadAddons();
        });
      }
    });
  }

  removeMeal(meal: MealResponse): void {
    let mealNameTranslated = this.translate.instant('menu.meals.' + meal.name);

    if (!this.isMealTranslationAvailable(meal.name)) {
      mealNameTranslated = meal.name;
    }

    const confirmationMessage =
      this.langService.currentLang === 'pl'
        ? `Czy na pewno chcesz usunac danie ${mealNameTranslated}?`
        : `Are you sure you want to remove meal ${mealNameTranslated}?`;

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
        this.menuService.removeMeal({ name: meal.name } as RemovedMealRequest).subscribe(() => {
          Swal.fire({
            text: this.langService.currentLang === 'pl' ? `Pomyslnie usunieto danie '${mealNameTranslated}'!` : `Successfully removed meal '${mealNameTranslated}'!`,
            icon: 'success',
            iconColor: 'green',
            confirmButtonColor: 'green',
            background: 'black',
            color: 'white',
            confirmButtonText: 'Ok',
          });
          this.loadMeals();
        });
      }
    });
  }

  removeIngredient(ingredient: IngredientResponse): void {
    let ingredientNameTranslated = this.translate.instant('menu.meals.ingredients.' + ingredient.name);

    if (!this.isIngredientTranslationAvailable(ingredient.name)) {
      ingredientNameTranslated = ingredient.name;
    }

    let mealsWithThisIngredient = this.meals
        .filter(meal => meal.ingredients.some(i => i.name === ingredient.name))
        .map(meal => {
            let mealNameTranslated = this.translate.instant('menu.meals.' + meal.name);
    
            if (!this.isMealTranslationAvailable(meal.name)) {
                mealNameTranslated = meal.name;
            }
    
            return mealNameTranslated;
        })
        .join(', ');

    const confirmationMessage =
      this.langService.currentLang === 'pl'
        ? `Czy na pewno chcesz usunac skladnik ${ingredientNameTranslated}? Nastepujace dania zawieraja ten skladnik: ${mealsWithThisIngredient}`
        : `Are you sure you want to remove ingredient ${ingredientNameTranslated}? Following meals contains this ingredient: ${mealsWithThisIngredient}`;

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
        this.menuService.removeIngredient({ name: ingredient.name } as RemovedIngredientRequest).subscribe(() => {
          Swal.fire({
            text: this.langService.currentLang === 'pl' ? `Pomyslnie usunieto skladnik '${ingredientNameTranslated}'!` : `Successfully removed ingredient '${ingredientNameTranslated}'!`,
            icon: 'success',
            iconColor: 'green',
            confirmButtonColor: 'green',
            background: 'black',
            color: 'white',
            confirmButtonText: 'Ok',
          });
          this.loadIngredients();
          this.loadMeals();
        });
      }
    });
  }

  addBeverage(): void {
    this.menuService.addBeverage(this.newBeverage).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? `Pomyslnie dodano napoj '${this.newBeverage.new_beverage_name}'!` : `Successfully added beverage '${this.newBeverage.new_beverage_name}'!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
        });

        this.loadBeverages();
        this.resetNewBeverage();
        this.hideAddBeverageTable();
        this.hideErrorMessages();
      },
      error: (error) => {
        this.handleError(error);
      },
    });
  }

  addAddon(): void {
    this.menuService.addAddon(this.newAddon).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? `Pomyslnie dodano dodatek '${this.newAddon.new_addon_name}'!` : `Successfully added addon '${this.newAddon.new_addon_name}'!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
        });

        this.loadAddons();
        this.resetNewAddon();
        this.hideAddAddonTable();
        this.hideErrorMessages();
      },
      error: (error) => {
        this.handleError(error);
      },
    });
  }

  addMeal(): void {
    const filteredPrices = Object.entries(this.newMeal.new_meal_prices)
            .filter(([size, price]) => typeof price === 'number' && price > 0)
            .reduce((acc, [size, price]) => {
                acc[size] = price;
                return acc;
            }, {} as { [key: string]: number }); 

    this.newMeal.new_meal_prices = filteredPrices;

    this.menuService.addMeal(this.newMeal).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? `Pomyslnie dodano danie '${this.newMeal.new_meal_name}'!` : `Successfully added meal '${this.newMeal.new_meal_name}'!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
        });

        this.loadMeals();
        this.resetNewMeal();
        this.hideAddMealTable();
        this.hideErrorMessages();
      },
      error: (error) => {
        this.handleError(error);
      },
    });
  }

  addIngredient(): void {
    this.menuService.addIngredient(this.newIngredient).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? `Pomyslnie dodano skladnik '${this.newIngredient.new_ingredient_name}'!` : `Successfully added ingredient '${this.newIngredient.new_ingredient_name}'!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
        });

        this.loadIngredients();
        this.resetNewIngredient();
        this.hideAddIngredientTable();
        this.hideErrorMessages();
      },
      error: (error) => {
        this.handleError(error);
      },
    });
  }

  showAddBeverageTable(): void {
    if (this.isEditing) {
      return;
    }

    this.hideErrorMessages();
    this.isAddingBeverage = true;
  }

  showAddAddonTable(): void {
    if (this.isEditing) {
      return;
    }

    this.hideErrorMessages();
    this.isAddingAddon = true;
  }

  showAddMealTable(): void {
    if (this.isEditing) {
      return;
    }

    this.hideErrorMessages();
    this.isAddingMeal = true;
  }

  showAddIngredientTable(): void {
    if (this.isEditing) {
      return;
    }

    this.hideErrorMessages();
    this.isAddingIngredient = true;
  }

  hideAddBeverageTable(): void {
    this.hideErrorMessages();
    this.isAddingBeverage = false;
  }

  hideAddAddonTable(): void {
    this.hideErrorMessages();
    this.isAddingAddon = false;
  }

  hideAddMealTable(): void {
    this.hideErrorMessages();
    this.isAddingMeal = false;
  }

  hideAddIngredientTable(): void {
    this.hideErrorMessages();
    this.isAddingIngredient = false;
  }

  hideEditableBeverageRow(beverage: BeverageResponse): void {
    beverage.isEditing = false;
    this.isEditing = false;
    this.hideErrorMessages();
  }

  hideEditableAddonRow(addon: AddonResponse): void {
    addon.isEditing = false;
    this.isEditing = false;
    this.hideErrorMessages();
  }

  hideEditableMealRow(meal: MealResponse): void {
    meal.isEditing = false;
    this.isEditing = false;
    this.hideErrorMessages();
  }

  resetNewBeverage(): void {
    this.newBeverage = { 
      new_beverage_name: '', 
      new_beverage_capacity: 0, 
      new_beverage_price: 0 
    };
  }

  resetNewAddon(): void {
    this.newAddon = { 
      new_addon_name: '', 
      new_addon_price: 0 
    };
  }

  resetNewMeal(): void {
    this.newMeal = {
      new_meal_name: '',
      new_meal_prices: {},
      new_meal_ingredients: []
    };
    this.selectedBread = null;
    this.selectedVegetables.clear();
    this.selectedOthers.clear();
  }

  resetNewIngredient(): void {
    this.newIngredient = { 
      new_ingredient_name: '', 
      new_ingredient_type: null
    };
  }

  handleError(error: any) {
    if (error.errorMessages) {
      this.errorMessages = error.errorMessages;
      console.log(this.errorMessages);
    } else {
      this.errorMessages = { general: 'An unexpected error occurred' };
    }
  }

  hideErrorMessages(): void {
    this.errorMessages = {};
  }

  formatCapacity(price: number): string {
    return price + ' L';
  }

  formatPrice(price: number | unknown): string {
    if (typeof price === 'number') {
      return price.toFixed(2) + ' zl';
    } else {
      return 'Invalid price';
    }
  }

  isManager(): boolean {
    return this.authenticationService.isManager();
  }

  isBeverageTranslationAvailable(beverageName: string): boolean {
    const translatedName = this.translate.instant('menu.beverages.' + beverageName);
    return translatedName !== 'menu.beverages.' + beverageName;
  }

  isAddonTranslationAvailable(addonName: string): boolean {
    const translatedName = this.translate.instant('menu.addons.' + addonName);
    return translatedName !== 'menu.addons.' + addonName;
  }

  isIngredientTranslationAvailable(ingredientName: string): boolean {
    const translatedName = this.translate.instant('menu.meals.ingredients.' + ingredientName);
    return translatedName !== 'menu.meals.ingredients.' + ingredientName;
  }

  isMealTranslationAvailable(mealName: string): boolean {
    const translatedName = this.translate.instant('menu.meals.' + mealName);
    return translatedName !== 'menu.meals.' + mealName;
  }

  onIngredientCheckboxChange(ingredient: any): void {
    if (ingredient.ingredient_type === 'BREAD') {
      if (this.selectedBread === ingredient.name) {
        this.selectedBread = null; 
        this.removeIngredientFromNewMeal(ingredient); 
      } else {
        this.selectedBread = ingredient.name; 
        this.addIngredientToNewMeal(ingredient);
      }
    } else if (ingredient.ingredient_type === 'VEGETABLE') {
      if (this.selectedVegetables.has(ingredient.name)) {
        this.selectedVegetables.delete(ingredient.name); 
        this.removeIngredientFromNewMeal(ingredient); 
      } else {
        this.selectedVegetables.add(ingredient.name); 
        this.addIngredientToNewMeal(ingredient);
      }
    } else if (ingredient.ingredient_type === 'OTHER') {
      if (this.selectedOthers.has(ingredient.name)) {
        this.selectedOthers.delete(ingredient.name); 
        this.removeIngredientFromNewMeal(ingredient);
      } else {
        this.selectedOthers.add(ingredient.name);
        this.addIngredientToNewMeal(ingredient);
      }
    }
  }

  onSizeCheckboxChange(meal: MealResponse, size: Size, event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    const priceControl = this.mealForms[meal.name].get('prices.' + size) as FormControl;
    
    if (inputElement.checked) {
      priceControl.setValue(priceControl.value || 0); 
    } else {
      priceControl.setValue(null); 
    }
  }

  onUpdateIngredientCheckboxChange(meal: MealResponse, ingredient: IngredientResponse, event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    const ingredientsControl = this.mealForms[meal.name].get('ingredients') as FormArray;

    const index = ingredientsControl.controls.findIndex(control => control.get('name')?.value === ingredient.name);
  
    if (inputElement.checked) {
      if (index === -1) {
        ingredientsControl.push(this.formBuilder.group({
          name: new FormControl(ingredient.name),
          ingredient_type: new FormControl(ingredient.ingredient_type)
        }));
      }
    } else {
      if (index >= 0) {
        ingredientsControl.removeAt(index);
      }
    }
  }

  addIngredientToNewMeal(ingredient: any): void {
    const exists = this.newMeal.new_meal_ingredients.find(
      (i) => i.name === ingredient.name && i.ingredient_type === ingredient.ingredient_type
    );

    if (!exists) {
      this.newMeal.new_meal_ingredients.push({
        name: ingredient.name,
        ingredient_type: ingredient.ingredient_type
      });
    }
  }

  removeIngredientFromNewMeal(ingredient: any): void {
    this.newMeal.new_meal_ingredients = this.newMeal.new_meal_ingredients.filter(
      (i) => !(i.name === ingredient.name && i.ingredient_type === ingredient.ingredient_type)
    );
  }

  mealContainsIngredient(meal: MealResponse, ingredient: IngredientResponse): boolean {
    return meal.ingredients.some(mealIngredient => mealIngredient.name === ingredient.name);
  }

  getSauces(): string {
  
    return this.ingredients
        .filter(ingredient => ingredient.ingredient_type === 'SAUCE')
        .map(ingredient => {
          let ingredientNameTranslated = this.translate.instant('menu.meals.ingredients.' + ingredient.name);

          if (!this.isIngredientTranslationAvailable(ingredient.name)) {
            ingredientNameTranslated = ingredient.name;
          }

          return ingredientNameTranslated;
        })
        .join(', ');
  }

  getMeats(): string {
      return this.ingredients
          .filter(ingredient => ingredient.ingredient_type === 'MEAT')
          .map(ingredient => {
            let ingredientNameTranslated = this.translate.instant('menu.meals.ingredients.' + ingredient.name);

            if (!this.isIngredientTranslationAvailable(ingredient.name)) {
              ingredientNameTranslated = ingredient.name;
            }

            return ingredientNameTranslated;
          })
          .join(', ');
  }

  sortSizes = (a: any, b: any): number => {
    return this.sizeOrder.indexOf(a.key) - this.sizeOrder.indexOf(b.key);
  };

  sortIngredientsByType(ingredients: IngredientResponse[]): IngredientResponse[] {
    return ingredients.sort((a, b) => {
      const order = ['BREAD', 'VEGETABLE', 'OTHER'];
      return order.indexOf(a.ingredient_type) - order.indexOf(b.ingredient_type);
    });
  }

  sortIngredientsByName(ingredients: IngredientResponse[]): IngredientResponse[] {
    return ingredients.sort((a, b) => {

      let firstIngredientNameTranslated = this.translate.instant('menu.meals.ingredients.' + a.name);
      let secondIngredientNameTranslated = this.translate.instant('menu.meals.ingredients.' + b.name);

      if (!this.isIngredientTranslationAvailable(a.name)) {
        firstIngredientNameTranslated = a.name;
      }
  
      if (!this.isIngredientTranslationAvailable(b.name)) {
        secondIngredientNameTranslated = b.name; 
      }
  
      return firstIngredientNameTranslated.localeCompare(secondIngredientNameTranslated);
    });
  }

  sortMealsByName(meals: MealResponse[]): MealResponse[] {
    return meals.sort((a, b) => {
      
      let firstMealNameTranslated = this.translate.instant('menu.meals.' + a.name);
      let secondMealNameTranslated = this.translate.instant('menu.meals.' + b.name);

      if (!this.isMealTranslationAvailable(a.name)) {
        firstMealNameTranslated = a.name;
      }
  
      if (!this.isMealTranslationAvailable(b.name)) {
        secondMealNameTranslated = b.name; 
      }
  
      return firstMealNameTranslated.localeCompare(secondMealNameTranslated);
    });
  }

  sortAddonsByName(addons: AddonResponse[]): AddonResponse[] {
    return addons.sort((a, b) => {
      
      let firstAddonNameTranslated = this.translate.instant('menu.addons.' + a.name);
      let secondAddonNameTranslated = this.translate.instant('menu.addons.' + b.name);

      if (!this.isAddonTranslationAvailable(a.name)) {
        firstAddonNameTranslated = a.name;
      }
  
      if (!this.isAddonTranslationAvailable(b.name)) {
        secondAddonNameTranslated = b.name; 
      }
  
      return firstAddonNameTranslated.localeCompare(secondAddonNameTranslated);
    });
  }

  sortBeveragesByName(beverages: BeverageResponse[]): BeverageResponse[] {
    return beverages.sort((a, b) => {
      
      let firstBeverageNameTranslated = this.translate.instant('menu.beverages.' + a.name);
      let secondBeverageNameTranslated = this.translate.instant('menu.beverages.' + b.name);

      if (!this.isBeverageTranslationAvailable(a.name)) {
        firstBeverageNameTranslated = a.name;
      }
  
      if (!this.isBeverageTranslationAvailable(b.name)) {
        secondBeverageNameTranslated = b.name; 
      }
  
      return firstBeverageNameTranslated.localeCompare(secondBeverageNameTranslated);
    });
  }
}