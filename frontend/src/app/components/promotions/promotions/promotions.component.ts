import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { PromotionsService } from '../../../services/promotions/promotions.service';
import { LangService } from '../../../services/lang/lang.service';
import { TranslateModule } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import {
  AddonPromotionResponse,
  AddonResponse,
  BeveragePromotionResponse,
  BeverageResponse,
  MealPromotionResponse,
  MealResponse,
} from '../../../responses/responses';
import { CommonModule } from '@angular/common';
import {
  NewAddonPromotionRequest,
  NewBeveragePromotionRequest,
  NewMealPromotionRequest,
  RemovedAddonPromotionRequest,
  RemovedBeveragePromotionRequest,
  RemovedMealPromotionRequest,
  UpdatedAddonPromotionRequest,
  UpdatedBeveragePromotionRequest,
  UpdatedMealPromotionRequest,
} from '../../../requests/requests';
import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Size } from '../../../enums/size.enum';
import { MenuService } from '../../../services/menu/menu.service';
import { AlertService } from '../../../services/alert/alert.service';
import { TranslationHelperService } from '../../../services/translation-helper/translation-helper.service';
import { NewsletterSignUpService } from '../../../services/newsletter/sign-up/newsletter-sign-up.service';

@Component({
  selector: 'app-promotions',
  standalone: true,
  imports: [CommonModule, TranslateModule, ReactiveFormsModule, FormsModule],
  templateUrl: './promotions.component.html',
  styleUrl: './promotions.component.scss',
})
export class PromotionsComponent implements OnInit {
  mealNames: string[] = [];
  beveragesWithCapacities: { [key in string]: number[] } = {};
  beveragesWithCapacitiesEntries: [string, number[]][] = [];
  addonNames: string[] = [];
  sizeOrder: Size[] = [Size.SMALL, Size.MEDIUM, Size.LARGE, Size.XL];

  newMealPromotion: NewMealPromotionRequest = {
    description: '',
    sizes: [],
    discount_percentage: 0,
    meal_names: [],
  };

  newBeveragePromotion: NewBeveragePromotionRequest = {
    description: '',
    discount_percentage: 0,
    beverages_with_capacities: {},
  };

  newAddonPromotion: NewAddonPromotionRequest = {
    description: '',
    discount_percentage: 0,
    addon_names: [],
  };

  mealPromotions: MealPromotionResponse[] = [];
  beveragePromotions: BeveragePromotionResponse[] = [];
  addonPromotions: AddonPromotionResponse[] = [];
  errorMessages: { [key: string]: string } = {};
  languageChangeSubscription: Subscription;

  mealPromotionForms: { [key: string]: FormGroup } = {};

  currentlyEditedPromotion:
    | MealPromotionResponse
    | BeveragePromotionResponse
    | AddonPromotionResponse
    | null = null;
  updatedMealPromotion: UpdatedMealPromotionRequest | null = null;
  updatedBeveragePromotion: UpdatedBeveragePromotionRequest | null = null;
  updatedAddonPromotion: UpdatedAddonPromotionRequest | null = null;

  isAddingMealPromotion = false;
  isEditingMealPromotion = false;
  isAddingBeveragePromotion = false;
  isEditingBeveragePromotion = false;
  isAddingAddonPromotion = false;
  isEditingAddonPromotion = false;
  isAdding = false;
  isEditing = false;

  constructor(
    private authenticationService: AuthenticationService,
    private promotionsService: PromotionsService,
    private langService: LangService,
    private menuService: MenuService,
    private newsletterSignUpService: NewsletterSignUpService,
    private alertService: AlertService,
    private translationHelper: TranslationHelperService
  ) {
    this.languageChangeSubscription =
      this.langService.languageChanged$.subscribe(() => {
        this.hideErrorMessages();
      });
  }

  ngOnInit(): void {
    this.loadMealPromotions();
    this.loadBeveragePromotions();
    this.loadAddonPromotions();
    this.loadMealNames();
    this.loadBeveragesWithCapacities();
    this.loadAddonNames();
  }

  private loadMealPromotions(): void {
    this.promotionsService.getMealPromotions().subscribe({
      next: (data: MealPromotionResponse[]) => (this.mealPromotions = data),
      error: (error) => this.handleError(error),
    });
  }

  private loadBeveragePromotions(): void {
    this.promotionsService.getBeveragePromotions().subscribe({
      next: (data: BeveragePromotionResponse[]) =>
        (this.beveragePromotions = data),
      error: (error) => this.handleError(error),
    });
  }

  private loadAddonPromotions(): void {
    this.promotionsService.getAddonPromotions().subscribe({
      next: (data: AddonPromotionResponse[]) => (this.addonPromotions = data),
      error: (error) => this.handleError(error),
    });
  }

  private loadMealNames(): void {
    this.menuService.getMeals().subscribe({
      next: (data: MealResponse[]) =>
        (this.mealNames = data.map((meal) => meal.name)),
      error: (error) => this.handleError(error),
    });
  }

  private loadBeveragesWithCapacities(): void {
    this.menuService.getBeverages().subscribe({
      next: (data: BeverageResponse[]) => {
        data.forEach((b) => {
          if (!this.beveragesWithCapacities[b.name]) {
            this.beveragesWithCapacities[b.name] = [];
          }

          this.beveragesWithCapacities[b.name].push(b.capacity);
        });

        this.beveragesWithCapacitiesEntries = Object.entries(
          this.beveragesWithCapacities
        ).sort((a, b) => a[0].localeCompare(b[0]));
      },
      error: (error) => this.handleError(error),
    });
  }

  private loadAddonNames(): void {
    this.menuService.getAddons().subscribe({
      next: (data: AddonResponse[]) =>
        (this.addonNames = data.map((addon) => addon.name)),
      error: (error) => this.handleError(error),
    });
  }

  protected showAddMealPromotionTable(): void {
    this.hideErrorMessages();
    this.isAddingMealPromotion = true;
    this.isAdding = true;
  }

  protected showAddBeveragePromotionTable(): void {
    this.hideErrorMessages();
    this.isAddingBeveragePromotion = true;
    this.isAdding = true;

    console.log(this.beveragesWithCapacitiesEntries)
  }

  protected showAddAddonPromotionTable(): void {
    this.hideErrorMessages();
    this.isAddingAddonPromotion = true;
    this.isAdding = true;
  }

  protected addMealPromotion(): void {
    this.promotionsService.addMealPromotion(this.newMealPromotion).subscribe({
      next: () => {
        this.alertService.showSuccessfulMealPromotionAddAlert();
        this.loadMealPromotions();
        this.resetNewMealPromotion();
        this.hideAddMealPromotionTable();
        this.hideErrorMessages();
      },
      error: (error) => this.handleError(error),
    });
  }

  protected addBeveragePromotion(): void {
    this.promotionsService
      .addBeveragePromotion(this.newBeveragePromotion)
      .subscribe({
        next: () => {
          this.alertService.showSuccessfulBeveragePromotionAddAlert();
          this.loadBeveragePromotions();
          this.resetNewBeveragePromotion();
          this.hideAddBeveragePromotionTable();
          this.hideErrorMessages();
        },
        error: (error) => this.handleError(error),
      });
  }

  protected addAddonPromotion(): void {
    this.promotionsService.addAddonPromotion(this.newAddonPromotion).subscribe({
      next: () => {
        this.alertService.showSuccessfulAddonPromotionAddAlert();
        this.loadAddonPromotions();
        this.resetNewAddonPromotion();
        this.hideAddAddonPromotionTable();
        this.hideErrorMessages();
      },
      error: (error) => this.handleError(error),
    });
  }

  protected hideAddMealPromotionTable(): void {
    this.hideErrorMessages();
    this.isAddingMealPromotion = false;
    this.isAdding = false;
    this.resetNewMealPromotion();
  }

  protected hideAddBeveragePromotionTable(): void {
    this.hideErrorMessages();
    this.isAddingBeveragePromotion = false;
    this.isAdding = false;
    this.resetNewBeveragePromotion();
  }

  protected hideAddAddonPromotionTable(): void {
    this.hideErrorMessages();
    this.isAddingAddonPromotion = false;
    this.isAdding = false;
    this.resetNewAddonPromotion();
  }

  protected resetNewMealPromotion(): void {
    this.newMealPromotion = {
      description: '',
      sizes: [],
      discount_percentage: 0,
      meal_names: [],
    };
  }

  protected resetNewBeveragePromotion(): void {
    this.newBeveragePromotion = {
      description: '',
      discount_percentage: 0,
      beverages_with_capacities: {},
    };
  }

  protected resetNewAddonPromotion(): void {
    this.newAddonPromotion = {
      description: '',
      discount_percentage: 0,
      addon_names: [],
    };
  }

  protected toggleSize(size: Size, event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;
    checked
      ? this.newMealPromotion.sizes.push(size)
      : (this.newMealPromotion.sizes = this.newMealPromotion.sizes.filter(
          (s) => s !== size
        ));
  }

  protected toggleMealNameAdd(mealName: string, event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;
    checked
      ? this.newMealPromotion.meal_names.push(mealName)
      : (this.newMealPromotion.meal_names =
          this.newMealPromotion.meal_names.filter((mn) => mn !== mealName));
  }

  protected toggleCapacityAdd(
    beverageName: string,
    capacity: number,
    event: Event
  ): void {
    const checked = (event.target as HTMLInputElement).checked;
    checked
      ? this.addCapacityToNewBeveragePromotion(beverageName, capacity)
      : this.removeCapacityFromNewBeveragePromotion(beverageName, capacity);
  }

  private addCapacityToNewBeveragePromotion(
    beverageName: string,
    capacity: number
  ): void {
    if (!this.newBeveragePromotion.beverages_with_capacities[beverageName]) {
      this.newBeveragePromotion.beverages_with_capacities[beverageName] = [];
    }

    this.newBeveragePromotion.beverages_with_capacities[beverageName].push(
      capacity
    );
  }

  private removeCapacityFromNewBeveragePromotion(
    beverageName: string,
    capacity: number
  ): void {
    this.newBeveragePromotion.beverages_with_capacities[beverageName] =
      this.newBeveragePromotion.beverages_with_capacities[beverageName].filter(
        (c) => c !== capacity
      );
  }

  protected toggleAddonNameAdd(addonName: string, event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;
    checked
      ? this.newAddonPromotion.addon_names.push(addonName)
      : (this.newAddonPromotion.addon_names =
          this.newAddonPromotion.addon_names.filter((an) => an !== addonName));
  }

  protected toggleSizeEdit(size: Size, event: Event): void {
    if (!this.updatedMealPromotion) return;

    const checked = (event.target as HTMLInputElement).checked;
    checked
      ? this.updatedMealPromotion.updated_sizes.push(size)
      : (this.updatedMealPromotion.updated_sizes =
          this.updatedMealPromotion.updated_sizes.filter((s) => s !== size));
  }

  protected toggleMealNameEdit(mealName: string, event: Event): void {
    if (!this.updatedMealPromotion) return;

    const checked = (event.target as HTMLInputElement).checked;
    checked
      ? this.updatedMealPromotion.updated_meal_names.push(mealName)
      : (this.updatedMealPromotion.updated_meal_names =
          this.updatedMealPromotion.updated_meal_names.filter(
            (mn) => mn !== mealName
          ));
  }

  protected toggleCapacityEdit(
    beverageName: string,
    capacity: number,
    event: Event
  ): void {
    if (!this.updatedBeveragePromotion) return;

    const checked = (event.target as HTMLInputElement).checked;
    checked
      ? this.addCapacityToUpdatedBeveragePromotion(beverageName, capacity)
      : this.removeCapacityFromUpdatedBeveragePromotion(beverageName, capacity);
  }

  private addCapacityToUpdatedBeveragePromotion(
    beverageName: string,
    capacity: number
  ): void {
    if (
      !this.updatedBeveragePromotion!.updated_beverages_with_capacities[
        beverageName
      ]
    ) {
      this.updatedBeveragePromotion!.updated_beverages_with_capacities[
        beverageName
      ] = [];
    }

    this.updatedBeveragePromotion!.updated_beverages_with_capacities[
      beverageName
    ].push(capacity);
  }

  private removeCapacityFromUpdatedBeveragePromotion(
    beverageName: string,
    capacity: number
  ): void {
    this.updatedBeveragePromotion!.updated_beverages_with_capacities[
      beverageName
    ] = this.updatedBeveragePromotion!.updated_beverages_with_capacities[
      beverageName
    ].filter((c) => c !== capacity);
  }

  protected toggleAddonNameEdit(addonName: string, event: Event) {
    if (!this.updatedAddonPromotion) return;

    const checked = (event.target as HTMLInputElement).checked;
    checked
      ? this.updatedAddonPromotion.updated_addon_names.push(addonName)
      : (this.updatedAddonPromotion.updated_addon_names =
          this.updatedAddonPromotion.updated_addon_names.filter(
            (an) => an !== addonName
          ));
  }

  protected editMealPromotionRow(mealPromotion: MealPromotionResponse): void {
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
      updated_meal_names: [...mealPromotion.meal_names],
    };
  }

  protected editBeveragePromotionRow(
    beveragePromotion: BeveragePromotionResponse
  ): void {
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
      updated_beverages_with_capacities: {
        ...beveragePromotion.beverages_with_capacities,
      },
    };
  }

  protected editAddonPromotionRow(
    addonPromotion: AddonPromotionResponse
  ): void {
    if (this.isEditing) {
      return;
    }

    this.hideErrorMessages();
    this.isEditing = true;
    this.isEditingAddonPromotion = true;
    this.currentlyEditedPromotion = addonPromotion;
    addonPromotion.isEditing = true;

    this.updatedAddonPromotion = {
      id: addonPromotion.id,
      updated_description: addonPromotion.description,
      updated_discount_percentage: addonPromotion.discount_percentage,
      updated_addon_names: [...addonPromotion.addon_names],
    };
  }

  protected updateMealPromotion(
    updatedPromotion: UpdatedMealPromotionRequest
  ): void {
    if (!updatedPromotion || !this.currentlyEditedPromotion) return;

    this.promotionsService.updateMealPromotion(updatedPromotion).subscribe({
      next: () => {
        this.alertService.showSuccessfulMealPromotionUpdateAlert();
        this.loadMealPromotions();
        this.hideUpdatePromotionTable();
      },
      error: (error) => this.handleError(error),
    });
  }

  protected updateBeveragePromotion(
    updatedPromotion: UpdatedBeveragePromotionRequest
  ): void {
    if (!updatedPromotion || !this.currentlyEditedPromotion) return;

    this.promotionsService.updateBeveragePromotion(updatedPromotion).subscribe({
      next: () => {
        this.alertService.showSuccessfulBeveragePromotionUpdateAlert();
        this.loadBeveragePromotions();
        this.hideUpdatePromotionTable();
      },
      error: (error) => this.handleError(error),
    });
  }

  protected updateAddonPromotion(
    updatedPromotion: UpdatedAddonPromotionRequest
  ): void {
    if (!updatedPromotion || !this.currentlyEditedPromotion) return;

    this.promotionsService.updateAddonPromotion(updatedPromotion).subscribe({
      next: () => {
        this.alertService.showSuccessfulAddonPromotionUpdateAlert();
        this.loadAddonPromotions();
        this.hideUpdatePromotionTable();
      },
      error: (error) => this.handleError(error),
    });
  }

  protected hideUpdatePromotionTable(): void {
    if (this.currentlyEditedPromotion) {
      this.currentlyEditedPromotion.isEditing = false;
    }

    this.currentlyEditedPromotion = null;
    this.updatedMealPromotion = null;
    this.updatedBeveragePromotion = null;
    this.updatedAddonPromotion = null;
    this.isEditing = false;
    this.isEditingBeveragePromotion = false;
    this.isEditingMealPromotion = false;
    this.isEditingAddonPromotion = false;

    this.hideErrorMessages();
  }

  protected removeMealPromotion(mealPromotion: MealPromotionResponse): void {
    this.alertService.showRemoveMealPromotionAlert().then((confirmed) => {
      if (!confirmed) return;

      this.promotionsService
        .removeMealPromotion({
          id: mealPromotion.id,
        } as RemovedMealPromotionRequest)
        .subscribe(() => {
          this.alertService.showSuccessfulMealPromotionRemoveAlert();
          this.loadMealPromotions();
        });
    });
  }

  protected removeBeveragePromotion(
    beveragePromotion: BeveragePromotionResponse
  ): void {
    this.alertService.showRemoveBeveragePromotionAlert().then((confirmed) => {
      if (!confirmed) return;

      this.promotionsService
        .removeBeveragePromotion({
          id: beveragePromotion.id,
        } as RemovedBeveragePromotionRequest)
        .subscribe(() => {
          this.alertService.showSuccessfulBeveragePromotionRemoveAlert();
          this.loadBeveragePromotions();
        });
    });
  }

  protected removeAddonPromotion(addonPromotion: AddonPromotionResponse): void {
    this.alertService.showRemoveAddonPromotionAlert().then((confirmed) => {
      if (!confirmed) return;

      this.promotionsService
        .removeAddonPromotion({
          id: addonPromotion.id,
        } as RemovedAddonPromotionRequest)
        .subscribe(() => {
          this.alertService.showSuccessfulAddonPromotionRemoveAlert();
          this.loadAddonPromotions();
        });
    });
  }

  protected startSigningUpToNewsletter(): void {
    this.newsletterSignUpService.startSigningUpToNewsletter();
  }

  protected getTranslatedMealName(mealName: string): string {
    return this.translationHelper.getTranslatedMealName(mealName);
  }

  protected getTranslatedBeverageName(beverageName: string): string {
    return this.translationHelper.getTranslatedBeverageName(beverageName);
  }

  protected getTranslatedAddonName(addonName: string): string {
    return this.translationHelper.getTranslatedAddonName(addonName);
  }

  protected isManager(): boolean {
    return this.authenticationService.isManager();
  }

  private handleError(error: any) {
    error.errorMessages
      ? (this.errorMessages = error.errorMessages)
      : (this.errorMessages = { general: 'An unexpected error occurred' });
  }

  private hideErrorMessages(): void {
    this.errorMessages = {};
  }
}
