import { Component, OnInit } from '@angular/core';
import { MenuService } from '../../../services/menu/menu.service';
import { CommonModule } from '@angular/common';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { LangService } from '../../../services/lang/lang.service';
import { Subscription } from 'rxjs';
import {
  NewAddonRequest,
  NewBeverageRequest,
  NewIngredientRequest,
  NewMealRequest,
  RemovedAddonRequest,
  RemovedBeverageRequest,
  RemovedIngredientRequest,
  RemovedMealRequest,
} from '../../../requests/requests';
import {
  BeverageResponse,
  AddonResponse,
  MealResponse,
  IngredientResponse,
} from '../../../responses/responses';
import { Size } from '../../../enums/size.enum';
import { IngredientType } from '../../../enums/ingredient-type.enum';
import { AlertService } from '../../../services/alert/alert.service';
import { CapacityPipe } from '../../../pipes/capacity.pipe';
import { PricePipe } from '../../../pipes/price.pipe';
import { TranslationHelperService } from '../../../services/translation-helper/translation-helper.service';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [
    CommonModule,
    TranslateModule,
    ReactiveFormsModule,
    FormsModule,
    CapacityPipe,
    PricePipe,
  ],
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

  isAddingBeverage: boolean = false;
  isAddingAddon: boolean = false;
  isAddingMeal: boolean = false;
  isAddingIngredient: boolean = false;
  isAdding: boolean = false;
  isEditing: boolean = false;

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
    new_meal_ingredients: [],
  };
  newIngredient: NewIngredientRequest = {
    new_ingredient_name: '',
    new_ingredient_type: null,
  };

  ingredientTypes: IngredientType[] = [
    IngredientType.BREAD,
    IngredientType.MEAT,
    IngredientType.VEGETABLE,
    IngredientType.SAUCE,
    IngredientType.OTHER,
  ];
  sizeOrder: Size[] = [Size.SMALL, Size.MEDIUM, Size.LARGE, Size.XL];

  selectedBread: string | null = null;
  selectedVegetables: Set<string> = new Set();
  selectedOthers: Set<string> = new Set();

  constructor(
    private menuService: MenuService,
    private authenticationService: AuthenticationService,
    private formBuilder: FormBuilder,
    private langService: LangService,
    private translate: TranslateService,
    private alertService: AlertService,
    private translationHelper: TranslationHelperService
  ) {
    this.languageChangeSubscription =
      this.langService.languageChanged$.subscribe(() => {
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
      (error) => console.log('Error loading beverages', error)
    );
  }

  loadAddons(): void {
    this.menuService.getAddons().subscribe(
      (data: AddonResponse[]) => {
        this.addons = data;
        this.initializeAddonForms(data);
      },
      (error) => console.log('Error loading addons', error)
    );
  }

  loadMeals(): void {
    this.menuService.getMeals().subscribe(
      (data: MealResponse[]) => {
        this.meals = data;
        this.initializeMealForms(data);
      },
      (error) => console.log('Error loading meals', error)
    );
  }

  loadIngredients(): void {
    this.menuService.getIngredients().subscribe(
      (data: IngredientResponse[]) => {
        this.ingredients = data;
      },
      (error) => console.log('Error loading ingredients', error)
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
        price: new FormControl(addon.price),
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
          XL: new FormControl(meal.prices.XL),
        }),
        ingredients: this.formBuilder.array(
          meal.ingredients.map((ingredient) =>
            this.formBuilder.group({
              name: new FormControl(ingredient.name),
              ingredient_type: new FormControl(ingredient.ingredient_type),
            })
          )
        ),
      });
    });
  }

  isManager(): boolean {
    return this.authenticationService.isManager();
  }

  getTranslatedMealName(mealName: string): string {
    return this.translationHelper.getTranslatedMealName(mealName);
  }

  getTranslatedBeverageName(beverageName: string): string {
    return this.translationHelper.getTranslatedBeverageName(beverageName);
  }

  getTranslatedAddonName(addonName: string): string {
    return this.translationHelper.getTranslatedAddonName(addonName);
  }

  getTranslatedIngredientName(ingredientName: string): string {
    return this.translationHelper.getTranslatedIngredientName(ingredientName);
  }

  editBeverageRow(beverage: BeverageResponse): void {
    if (this.isEditing) return;

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
    if (this.isEditing) return;

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
        XL: new FormControl(meal.prices.XL),
      }),
      ingredients: this.formBuilder.array(
        meal.ingredients.map((ingredient) => new FormControl(ingredient.name))
      ),
    });
  }

  saveBeverageRow(beverage: BeverageResponse): void {
    let beverageNameTranslated = this.getTranslatedBeverageName(beverage.name);

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
      next: () => {
        beverage.isEditing = false;
        this.isEditing = false;

        this.alertService.showSuccessfulBeverageUpdateAlert(
          beverageNameTranslated
        );
        this.hideErrorMessages();
        this.loadBeverages();
      },
      error: (error) => this.handleError(error),
    });
  }

  saveAddonRow(addon: AddonResponse): void {
    let addonNameTranslated = this.getTranslatedAddonName(addon.name);

    const formGroup = this.addonForms[addon.name];
    const newPrice = formGroup.get('price')!.value;
    const updatedAddon = {
      updated_addon_name: addon.name,
      updated_addon_price: newPrice,
    };

    this.menuService.updateAddon(updatedAddon).subscribe({
      next: () => {
        addon.isEditing = false;
        this.isEditing = false;

        this.alertService.showSuccessfulAddonUpdateAlert(addonNameTranslated);
        this.hideErrorMessages();
        this.loadAddons();
      },
      error: (error) => {
        this.handleError(error);
      },
    });
  }

  saveMealRow(meal: MealResponse): void {
    let mealNameTranslated = this.getTranslatedMealName(meal.name);

    const formGroup = this.mealForms[meal.name];
    const newPrices = formGroup.get('prices')!.value;
    const newIngredients = formGroup.get('ingredients')!.value;
    const filteredNewPrices = this.getFilteredPrices(newPrices);
    const updatedMeal = {
      updated_meal_name: meal.name,
      updated_meal_prices: filteredNewPrices,
      updated_meal_ingredients: newIngredients,
    };

    this.menuService.updateMeal(updatedMeal).subscribe({
      next: () => {
        meal.isEditing = false;
        this.isEditing = false;

        this.alertService.showSuccessfulMealUpdateAlert(mealNameTranslated);
        this.hideErrorMessages();
        this.loadMeals();
      },
      error: (error) => this.handleError(error),
    });
  }

  removeBeverage(beverage: BeverageResponse): void {
    let beverageNameTranslated = this.getTranslatedBeverageName(beverage.name);

    this.alertService
      .showRemoveBeverageAlert(beverageNameTranslated)
      .then((confirmed) => {
        if (!confirmed) return;

        this.menuService
          .removeBeverage({
            name: beverage.name,
            capacity: beverage.capacity,
          } as RemovedBeverageRequest)
          .subscribe(() => {
            this.alertService.showSuccessfulBeverageRemoveAlert(
              beverageNameTranslated
            );
            this.loadBeverages();
          });
      });
  }

  removeAddon(addon: AddonResponse): void {
    let addonNameTranslated = this.getTranslatedAddonName(addon.name);

    this.alertService
      .showRemoveAddonAlert(addonNameTranslated)
      .then((confirmed) => {
        if (!confirmed) return;

        this.menuService
          .removeAddon({ name: addon.name } as RemovedAddonRequest)
          .subscribe(() => {
            this.alertService.showSuccessfulAddonRemoveAlert(
              addonNameTranslated
            );
            this.loadAddons();
          });
      });
  }

  removeMeal(meal: MealResponse): void {
    let mealNameTranslated = this.getTranslatedMealName(meal.name);

    this.alertService
      .showRemoveMealAlert(mealNameTranslated)
      .then((confirmed) => {
        if (!confirmed) return;

        this.menuService
          .removeMeal({ name: meal.name } as RemovedMealRequest)
          .subscribe(() => {
            this.alertService.showSuccessfulMealRemoveAlert(mealNameTranslated);
            this.loadMeals();
          });
      });
  }

  removeIngredient(ingredient: IngredientResponse): void {
    let ingredientNameTranslated = this.getTranslatedIngredientName(
      ingredient.name
    );
    let mealsWithThisIngredient = this.meals
      .filter((meal) =>
        meal.ingredients.some((i) => i.name === ingredient.name)
      )
      .map((meal) => this.getTranslatedMealName(meal.name))
      .join(', ');

    this.alertService
      .showRemoveIngredientAlert(
        ingredientNameTranslated,
        mealsWithThisIngredient
      )
      .then((confirmed) => {
        if (!confirmed) return;

        this.menuService
          .removeIngredient({
            name: ingredient.name,
          } as RemovedIngredientRequest)
          .subscribe(() => {
            this.alertService.showSuccessfulIngredientRemoveAlert(
              ingredientNameTranslated
            );
            this.loadIngredients();
          });
      });
  }

  addBeverage(): void {
    this.menuService.addBeverage(this.newBeverage).subscribe({
      next: () => {
        this.alertService.showSuccessfulBeverageAddAlert(
          this.newBeverage.new_beverage_name
        );
        this.loadBeverages();
        this.resetNewBeverage();
        this.hideAddBeverageTable();
        this.hideErrorMessages();
      },
      error: (error) => this.handleError(error),
    });
  }

  addAddon(): void {
    this.menuService.addAddon(this.newAddon).subscribe({
      next: () => {
        this.alertService.showSuccessfulAddonAddAlert(
          this.newAddon.new_addon_name
        );
        this.loadAddons();
        this.resetNewAddon();
        this.hideAddAddonTable();
        this.hideErrorMessages();
      },
      error: (error) => this.handleError(error),
    });
  }

  addMeal(): void {
    this.newMeal.new_meal_prices = this.getFilteredPrices(
      this.newMeal.new_meal_prices
    );

    this.menuService.addMeal(this.newMeal).subscribe({
      next: () => {
        this.alertService.showSuccessfulMealAddAlert(
          this.newMeal.new_meal_name
        );
        this.loadMeals();
        this.resetNewMeal();
        this.hideAddMealTable();
        this.hideErrorMessages();
      },
      error: (error) => this.handleError(error),
    });
  }

  private getFilteredPrices(prices: { [key in Size]?: number }): {
    [key: string]: number;
  } {
    return Object.entries(prices)
      .filter(([size, price]) => typeof price === 'number' && price > 0)
      .reduce((acc, [size, price]) => {
        acc[size] = price;
        return acc;
      }, {} as { [key: string]: number });
  }

  addIngredient(): void {
    this.menuService.addIngredient(this.newIngredient).subscribe({
      next: () => {
        this.alertService.showSuccessfulIngredientAddAlert(
          this.newIngredient.new_ingredient_name
        );
        this.loadIngredients();
        this.resetNewIngredient();
        this.hideAddIngredientTable();
        this.hideErrorMessages();
      },
      error: (error) => this.handleError(error),
    });
  }

  showAddBeverageTable(): void {
    if (this.isEditing) return;

    this.hideErrorMessages();
    this.isAddingBeverage = true;
    this.isAdding = true;
  }

  showAddAddonTable(): void {
    if (this.isEditing) return;

    this.hideErrorMessages();
    this.isAddingAddon = true;
    this.isAdding = true;
  }

  showAddMealTable(): void {
    if (this.isEditing) return;

    this.hideErrorMessages();
    this.isAddingMeal = true;
    this.isAdding = true;
  }

  showAddIngredientTable(): void {
    if (this.isEditing) return;

    this.hideErrorMessages();
    this.isAddingIngredient = true;
    this.isAdding = true;
  }

  hideAddBeverageTable(): void {
    this.hideErrorMessages();
    this.isAddingBeverage = false;
    this.isAdding = false;
  }

  hideAddAddonTable(): void {
    this.hideErrorMessages();
    this.isAddingAddon = false;
    this.isAdding = false;
  }

  hideAddMealTable(): void {
    this.hideErrorMessages();
    this.isAddingMeal = false;
    this.isAdding = false;
  }

  hideAddIngredientTable(): void {
    this.hideErrorMessages();
    this.isAddingIngredient = false;
    this.isAdding = false;
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
      new_beverage_price: 0,
    };
  }

  resetNewAddon(): void {
    this.newAddon = {
      new_addon_name: '',
      new_addon_price: 0,
    };
  }

  resetNewMeal(): void {
    this.newMeal = {
      new_meal_name: '',
      new_meal_prices: {},
      new_meal_ingredients: [],
    };
    this.selectedBread = null;
    this.selectedVegetables.clear();
    this.selectedOthers.clear();
  }

  resetNewIngredient(): void {
    this.newIngredient = {
      new_ingredient_name: '',
      new_ingredient_type: null,
    };
  }

  onIngredientCheckboxChange(ingredient: IngredientResponse): void {
    if (ingredient.ingredient_type === 'BREAD') {
      this.handleBreadIngredientCheckboxChange(ingredient);
    } else if (ingredient.ingredient_type === 'VEGETABLE') {
      this.handleVegetableIngredientCheckboxChange(ingredient);
    } else if (ingredient.ingredient_type === 'OTHER') {
      this.handleOtherIngredientCheckboxChange(ingredient);
    }
  }

  handleBreadIngredientCheckboxChange(ingredient: IngredientResponse): void {
    if (this.selectedBread === ingredient.name) {
      this.selectedBread = null;
      this.removeIngredientFromNewMeal(ingredient);
    } else {
      this.selectedBread = ingredient.name;
      this.addIngredientToNewMeal(ingredient);
    }
  }

  handleVegetableIngredientCheckboxChange(
    ingredient: IngredientResponse
  ): void {
    if (this.selectedVegetables.has(ingredient.name)) {
      this.selectedVegetables.delete(ingredient.name);
      this.removeIngredientFromNewMeal(ingredient);
    } else {
      this.selectedVegetables.add(ingredient.name);
      this.addIngredientToNewMeal(ingredient);
    }
  }

  handleOtherIngredientCheckboxChange(ingredient: IngredientResponse): void {
    if (this.selectedOthers.has(ingredient.name)) {
      this.selectedOthers.delete(ingredient.name);
      this.removeIngredientFromNewMeal(ingredient);
    } else {
      this.selectedOthers.add(ingredient.name);
      this.addIngredientToNewMeal(ingredient);
    }
  }

  onSizeCheckboxChange(meal: MealResponse, size: Size, event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    const priceControl = this.mealForms[meal.name].get(
      'prices.' + size
    ) as FormControl;

    inputElement.checked
      ? priceControl.setValue(priceControl.value || 0)
      : priceControl.setValue(null);
  }

  onUpdateIngredientCheckboxChange(
    meal: MealResponse,
    ingredient: IngredientResponse,
    event: Event
  ): void {
    const inputElement = event.target as HTMLInputElement;
    const ingredientsControl = this.mealForms[meal.name].get(
      'ingredients'
    ) as FormArray;

    const index = ingredientsControl.controls.findIndex(
      (control) => control.get('name')?.value === ingredient.name
    );

    if (inputElement.checked) {
      if (index === -1) {
        ingredientsControl.push(
          this.formBuilder.group({
            name: new FormControl(ingredient.name),
            ingredient_type: new FormControl(ingredient.ingredient_type),
          })
        );
      }
    } else {
      if (index >= 0) ingredientsControl.removeAt(index);
    }
  }

  addIngredientToNewMeal(ingredient: IngredientResponse): void {
    const exists = this.newMeal.new_meal_ingredients.find(
      (i) =>
        i.name === ingredient.name &&
        i.ingredient_type === ingredient.ingredient_type
    );

    if (!exists) {
      this.newMeal.new_meal_ingredients.push({
        name: ingredient.name,
        ingredient_type: ingredient.ingredient_type,
      });
    }
  }

  removeIngredientFromNewMeal(ingredient: IngredientResponse): void {
    this.newMeal.new_meal_ingredients =
      this.newMeal.new_meal_ingredients.filter(
        (ingr) =>
          !(
            ingr.name === ingredient.name &&
            ingr.ingredient_type === ingredient.ingredient_type
          )
      );
  }

  mealContainsIngredient(
    meal: MealResponse,
    ingredient: IngredientResponse
  ): boolean {
    return meal.ingredients.some(
      (mealIngredient) => mealIngredient.name === ingredient.name
    );
  }

  getSauces(): string {
    return this.ingredients
      .filter((ingredient) => ingredient.ingredient_type === 'SAUCE')
      .map((ingredient) => this.getTranslatedIngredientName(ingredient.name))
      .join(', ');
  }

  getMeats(): string {
    return this.ingredients
      .filter((ingredient) => ingredient.ingredient_type === 'MEAT')
      .map((ingredient) => this.getTranslatedIngredientName(ingredient.name))
      .join(', ');
  }

  sortSizes = (a: any, b: any): number => {
    return this.sizeOrder.indexOf(a.key) - this.sizeOrder.indexOf(b.key);
  };

  sortIngredientsByType(
    ingredients: IngredientResponse[]
  ): IngredientResponse[] {
    return ingredients.sort((a, b) => {
      const order = ['BREAD', 'VEGETABLE', 'OTHER'];
      return (
        order.indexOf(a.ingredient_type) - order.indexOf(b.ingredient_type)
      );
    });
  }

  sortIngredientsByName(
    ingredients: IngredientResponse[]
  ): IngredientResponse[] {
    return ingredients.sort((a, b) => {
      let firstIngredientNameTranslated = this.getTranslatedIngredientName(
        a.name
      );
      let secondIngredientNameTranslated = this.getTranslatedIngredientName(
        b.name
      );

      return firstIngredientNameTranslated.localeCompare(
        secondIngredientNameTranslated
      );
    });
  }

  sortMealsByName(meals: MealResponse[]): MealResponse[] {
    return meals.sort((a, b) => {
      let firstMealNameTranslated = this.getTranslatedMealName(a.name);
      let secondMealNameTranslated = this.getTranslatedMealName(b.name);

      return firstMealNameTranslated.localeCompare(secondMealNameTranslated);
    });
  }

  sortAddonsByName(addons: AddonResponse[]): AddonResponse[] {
    return addons.sort((a, b) => {
      let firstAddonNameTranslated = this.getTranslatedAddonName(a.name);
      let secondAddonNameTranslated = this.getTranslatedAddonName(b.name);

      return firstAddonNameTranslated.localeCompare(secondAddonNameTranslated);
    });
  }

  sortBeveragesByName(beverages: BeverageResponse[]): BeverageResponse[] {
    return beverages.sort((a, b) => {
      let firstBeverageNameTranslated = this.getTranslatedBeverageName(a.name);
      let secondBeverageNameTranslated = this.getTranslatedBeverageName(b.name);

      return firstBeverageNameTranslated.localeCompare(
        secondBeverageNameTranslated
      );
    });
  }

  hasPromotionToThisSize(
    meal: MealResponse,
    size: string
  ): { hasPromotion: boolean; discountPercentage: number } {
    const sizeEnum = Size[size as keyof typeof Size];
    const promotion = meal.meal_promotions.find((m) =>
      m.sizes.includes(sizeEnum)
    );

    if (promotion) {
      return {
        hasPromotion: true,
        discountPercentage: promotion.discount_percentage,
      };
    }

    return { hasPromotion: false, discountPercentage: 0 };
  }

  handleError(error: any): void {
    error.errorMessages
      ? (this.errorMessages = error.errorMessages)
      : (this.errorMessages = { general: 'An unexpected error occurred' });
  }

  hideErrorMessages(): void {
    this.errorMessages = {};
  }
}
