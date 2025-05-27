import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { PromotionsService } from '../../../services/promotions/promotions.service';
import { LangService } from '../../../services/lang/lang.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { AddonPromotionResponse, AddonResponse, BeveragePromotionResponse, BeverageResponse, MealPromotionResponse, MealResponse } from '../../../responses/responses';
import { CommonModule } from '@angular/common';
import { NewAddonPromotionRequest, NewBeveragePromotionRequest, NewMealPromotionRequest, NewNewsletterSubscriberRequest, RegenerateOtpRequest, RemovedAddonPromotionRequest, RemovedBeveragePromotionRequest, RemovedMealPromotionRequest, UpdatedAddonPromotionRequest, UpdatedBeveragePromotionRequest, UpdatedMealPromotionRequest, VerifyNewsletterSubscriptionRequest } from '../../../requests/requests';
import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Size } from '../../../enums/size.enum';
import { MenuService } from '../../../services/menu/menu.service';
import Swal from 'sweetalert2';
import { NewsletterService } from '../../../services/newsletter/newsletter.service';

@Component({
  selector: 'app-promotions',
  standalone: true,
  imports: [CommonModule, TranslateModule, ReactiveFormsModule, FormsModule],
  templateUrl: './promotions.component.html',
  styleUrl: './promotions.component.scss'
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
    meal_names: []
  };

  newBeveragePromotion: NewBeveragePromotionRequest = {
    description: '',
    discount_percentage: 0,
    beverages_with_capacities: {}
  };

  newAddonPromotion: NewAddonPromotionRequest = {
    description: '',
    discount_percentage: 0,
    addon_names: []
  };

  mealPromotions: MealPromotionResponse[] = [];
  beveragePromotions: BeveragePromotionResponse[] = [];
  addonPromotions: AddonPromotionResponse[] = [];
  errorMessages: { [key: string]: string } = {};
  languageChangeSubscription: Subscription;

  mealPromotionForms: { [key: string]: FormGroup } = {};

  currentlyEditedPromotion: MealPromotionResponse | BeveragePromotionResponse | AddonPromotionResponse | null = null;
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
      private translate: TranslateService,
      private menuService: MenuService, 
      private newsletterService: NewsletterService
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
    this.loadAddonNames();
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

  loadAddonNames(): void {
    this.menuService.getAddons().subscribe(
      (data: AddonResponse[]) => {
        this.addonNames = data.map(addon => addon.name)
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

  showAddAddonPromotionTable() {
    this.hideErrorMessages();
    this.isAddingAddonPromotion = true;
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
          background: '#141414',
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
          background: '#141414',
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

  addAddonPromotion(): void {
    this.promotionsService.addAddonPromotion(this.newAddonPromotion).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? `Pomyslnie dodano nowa promocje na dania'!` : `Successfully added new meal promotion!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: '#141414',
          color: 'white',
          confirmButtonText: 'Ok',
        });

        this.loadAddonPromotions();
        this.resetNewAddonPromotion();
        this.hideAddAddonPromotionTable();
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

  hideAddAddonPromotionTable(): void {
    this.hideErrorMessages();
    this.isAddingAddonPromotion = false;
    this.isAdding = false;
    this.resetNewAddonPromotion();
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

  resetNewAddonPromotion(): void {
    this.newAddonPromotion = {
      description: '',
      discount_percentage: 0,
      addon_names: []
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

  toggleAddonNameAdd(addonName: string, event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    
    if (checked) this.newAddonPromotion.addon_names.push(addonName);
    else this.newAddonPromotion.addon_names = this.newAddonPromotion.addon_names.filter(an => an !== addonName);
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

  toggleAddonNameEdit(addonName: string, event: Event) {
    if (!this.updatedAddonPromotion) return;
    
    const checked = (event.target as HTMLInputElement).checked;
    
    if (checked) {
      this.updatedAddonPromotion.updated_addon_names.push(addonName);
    } else {
      this.updatedAddonPromotion.updated_addon_names = this.updatedAddonPromotion.updated_addon_names.filter(an => an !== addonName);
    }
  }

  editMealPromotionRow(mealPromotion: MealPromotionResponse) {
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

  editBeveragePromotionRow(beveragePromotion: BeveragePromotionResponse) {
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

  editAddonPromotionRow(addonPromotion: AddonPromotionResponse) {
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
      updated_addon_names: [...addonPromotion.addon_names]
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
          background: '#141414',
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
          background: '#141414',
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

  updateAddonPromotion(updatedPromotion: UpdatedAddonPromotionRequest) {
    if (!updatedPromotion || !this.currentlyEditedPromotion) return;
    
    this.promotionsService.updateAddonPromotion(updatedPromotion).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? 'Pomyslnie zaktualizowano promocje na dodatki!' : 'Successfully updated addon promotion!',
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: '#141414',
          color: 'white',
          confirmButtonText: 'Ok',
        });
  
        this.loadAddonPromotions();
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
    this.updatedAddonPromotion = null;
    this.isEditing = false;
    this.isEditingBeveragePromotion = false;
    this.isEditingMealPromotion = false;
    this.isEditingAddonPromotion = false;

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
      background: '#141414',
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
            background: '#141414',
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
      background: '#141414',
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
            background: '#141414',
            color: 'white',
            confirmButtonText: 'Ok',
          });
          this.loadBeveragePromotions();
        });
      }
    });
  }

  removeAddonPromotion(addonPromotion: AddonPromotionResponse): void {
    const confirmationMessage =
    this.langService.currentLang === 'pl'
    ? `Czy na pewno chcesz usunac ta promocje na dodatki?`
    : `Are you sure you want to remove this addon promotion?`;

    Swal.fire({
      title: this.langService.currentLang === 'pl' ? 'Potwierdzenie' : 'Confirmation',
      text: confirmationMessage,
      icon: 'warning',
      iconColor: 'red',
      showCancelButton: true,
      confirmButtonColor: '#0077ff',
      cancelButtonColor: 'red',
      background: '#141414',
      color: 'white',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Tak' : 'Yes',
      cancelButtonText: this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel',
    }).then((result) => {
      if (result.isConfirmed) {
        this.promotionsService.removeAddonPromotion({ id: addonPromotion.id } as RemovedAddonPromotionRequest).subscribe(() => {
          Swal.fire({
            text: this.langService.currentLang === 'pl' ? `Pomyslnie usunieto promocje na dodatki!` : `Successfully removed addon promotion!`,
            icon: 'success',
            iconColor: 'green',
            confirmButtonColor: 'green',
            background: '#141414',
            color: 'white',
            confirmButtonText: 'Ok',
          });
          this.loadAddonPromotions();
        });
      }
    });
  }

  async startSigningUpToNewsletter(): Promise<void> {
    const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';

    const { value: firstName } = await Swal.fire({
      title: this.langService.currentLang === 'pl' ? `Czesc!` : `Hi!`,
      text: this.langService.currentLang === 'pl' ? `Podaj swoje imie :)` : `Enter your name :)`,
      input: "text",
      inputPlaceholder: this.langService.currentLang === 'pl' ? `Twoje imie` : `Your name`,
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
      confirmButtonColor: '#198754',
      showCancelButton: true,
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
    });

    if (firstName) {
      this.startEnteringNewsletterEmail(String(firstName).charAt(0).toUpperCase() + String(firstName).slice(1).toLocaleLowerCase());
    }
  }

  async startEnteringNewsletterEmail(firstName: string): Promise<void> {
    const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';
    const errorText = this.langService.currentLang === 'pl' ? 'Podaj poprawny adres email' : 'Please enter a valid email';
    
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    const { value: email } = await Swal.fire({
      title: this.langService.currentLang === 'pl' ? `Milo Cie poznac ${firstName}!` : `Nice to meet you ${firstName}!`,
      input: "email",
      inputLabel: this.langService.currentLang === 'pl' ? `Podaj swoj email` : `Enter your email`,
      inputPlaceholder: this.langService.currentLang === 'pl' ? `Twoj email` : `Your email`,
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
      confirmButtonColor: '#198754',
      showCancelButton: true,
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
      customClass: {
        validationMessage: 'custom-validation-message',
      },
      inputValidator: (value) => {
        if (!value || !emailRegex.test(value)) {
          return errorText;
        }

        return null;
      },
    });
    
    if (email) {
      this.startChoosingNewsletterLanguage(firstName, email)
    }
  }

  async startChoosingNewsletterLanguage(firstName: string, email: string): Promise<void> {
    const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';
    const errorText = this.langService.currentLang === 'pl' ? 'Prosze wybrac jezyk' : 'Please select a language';

    const inputOptions = new Promise((resolve) => {
      resolve({
        "POLISH": this.langService.currentLang === 'pl' ? `🇵🇱 Polski` : `🇵🇱 Polish`,
        "ENGLISH": this.langService.currentLang === 'pl' ? `🇺🇸 Angielski` : `🇺🇸 English`
      });
    });

    const { value: language } = await Swal.fire({
      title: `${firstName}!`,
      text:this.langService.currentLang === 'pl' ? `Wybierz jezyk, w jakim chcesz otrzymywac wiadomosci!` : `Choose language, in which you would like to receive messages!`,
      input: "radio",
      inputOptions,
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
      confirmButtonColor: '#198754',
      showCancelButton: true,
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
      customClass: {
        validationMessage: 'custom-validation-message',
        input: 'swal-radio-container'
      },
      inputValidator: (value) => {
        if (!value) {
          return errorText;
        }
        return null;
      },
    });
    
    if (language) {
      this.subscribeNewsletter({
        first_name: firstName,
        email: email,
        messages_language: language
      });
    }
  }

  async subscribeNewsletter(request: NewNewsletterSubscriberRequest): Promise<void> {

    const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';
    const regenerateButtonText = this.langService.currentLang === 'pl' ? 'Nowy kod' : 'New code';

    this.newsletterService.subscribe(request).subscribe({
      next: async (response) => {
        const otp = await Swal.fire({
          allowOutsideClick: false,
          title: this.langService.currentLang === 'pl' ? 
            'Potwierdz subskrypcje!' : 
            'Confirm subscription!',
          input: "text",
          text: this.langService.currentLang === 'pl' ? 
            'Wyslalismy do Ciebie wiadomosc email, w ktorej znajdziesz kod. Wpisz go ponizej' : 
            'We sent to you email message, in which you can find code. Enter it below',
          inputPlaceholder: this.langService.currentLang === 'pl' ? `6-cyfrowy kod` : `6-digit code`,
          confirmButtonColor: 'green',
          showCancelButton: true,
          cancelButtonText: cancelButtonText,
          cancelButtonColor: 'red',
          showDenyButton: true,
          denyButtonText: regenerateButtonText, 
          denyButtonColor: '#007bff',
          background: '#141414',
          color: 'white',
          confirmButtonText: 'Ok',
          inputValidator: (value) => {
            if (!value) {
              return this.langService.currentLang === 'pl' ? 
                'Musisz wprowadzic kod!' : 
                'You need to enter the code!';
            }
            
            if (value.length !== 6) {
              return this.langService.currentLang === 'pl' ? 
                'Kod musi miec dokladnie 6 cyfr!' : 
                'Code must be exactly 6 digits!';
            }
            
            if (!/^\d+$/.test(value)) {
              return this.langService.currentLang === 'pl' ? 
                'Kod moze zawierac tylko cyfry!' : 
                'Code can only contain digits!';
            }
            
            return null;
          },
          customClass: {
            validationMessage: 'custom-validation-message'
          },
        });

        if (otp.isConfirmed && otp.value) {
          this.verifySubscription({
            otp: Number(otp.value),
            email: request.email
          });
        } else if (otp.isDenied) {
          this.regenerateOtp({
            email: request.email
          });
        }
      },
      error: async (error) => {
        this.handleError(error);
        await Swal.fire({
          title: this.langService.currentLang === 'pl' ? 
            'Wystapil blad' : 
            'Error occured',
          text: error.errorMessages.message,
          icon: 'error',
          iconColor: 'red',
          confirmButtonColor: 'red',
          background: '#141414',
          color: 'white',
          confirmButtonText: 'Ok',
        });
      }
    });
  }

  verifySubscription(request: VerifyNewsletterSubscriptionRequest) {
    this.newsletterService.verifySubscription(request).subscribe({
      next: async (response) => {
        await Swal.fire({
          title: this.langService.currentLang === 'pl' ? 
            'Subskrypcja potwierdzona!' : 
            'Subscription confirmed!',
          text: this.langService.currentLang === 'pl' ? 
            'Dziekujemy, ze chcesz byc czescia kebabowej spolecznosci! Mamy nadzieje, ze przyszle promocje Cie zainteresuja :)' : 
            'Thank you for your desire to be part of the kebab community! We hope that future promotions will be interesting for you :)',
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: '#141414',
          color: 'white',
          confirmButtonText: 'Ok',
        });
      },
      error: async (error) => {
        const errorMessage = error.errorMessages?.message || 
                             (this.langService.currentLang === 'pl' ? 
                             'Wystapil blad. Sprawdz kod i sprobuj ponownie.' : 
                             'A validation error occurred. Check code and try again.');
        const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';
        const regenerateButtonText = this.langService.currentLang === 'pl' ? 'Nowy kod' : 'New code';

        const otp = await Swal.fire({
          allowOutsideClick: false,
          title: this.langService.currentLang === 'pl' ? 
            'Sprobuj ponownie!' : 
            'Try again!',
          input: 'text',
          text: errorMessage,
          inputPlaceholder: this.langService.currentLang === 'pl' ? 
            '6-cyfrowy kod' : 
            '6-digit code',
          confirmButtonColor: 'green',
          showCancelButton: true,
          cancelButtonColor: 'red',
          cancelButtonText: cancelButtonText,
          showDenyButton: true,
          denyButtonText: regenerateButtonText, 
          denyButtonColor: '#007bff',
          background: '#141414',
          color: 'white',
          confirmButtonText: 'Ok',
          inputValidator: (value) => {
            if (!value) {
              return this.langService.currentLang === 'pl' ? 
                'Musisz wprowadzic kod!' : 
                'You need to enter the code!';
            }
            if (value.length !== 6) {
              return this.langService.currentLang === 'pl' ? 
                'Kod musi miec dokladnie 6 cyfr!' : 
                'Code must be exactly 6 digits!';
            }
            if (!/^\d+$/.test(value)) {
              return this.langService.currentLang === 'pl' ? 
                'Kod moze zawierac tylko cyfry!' : 
                'Code can only contain digits!';
            }
            return null;
          },
          customClass: {
            validationMessage: 'custom-validation-message'
          },
        });

        if (otp.isConfirmed && otp.value) {
          this.verifySubscription({
            otp: otp.value,
            email: request.email
          });
        } else if (otp.isDenied) {
          this.regenerateOtp({
            email: request.email
          });
        }
      }
    });
  }

  regenerateOtp(request: RegenerateOtpRequest) {

    const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';
    const regenerateButtonText = this.langService.currentLang === 'pl' ? 'Nowy kod' : 'New code';

    this.newsletterService.regenerateOtp(request).subscribe({
      next: async (response) => {
        const otp = await Swal.fire({
          allowOutsideClick: false,
          title: this.langService.currentLang === 'pl' ? 
            'Nowy kod wygenerowany!' : 
            'New code generated!',
          input: "text",
          text: this.langService.currentLang === 'pl' ? 
            'Wyslalismy do Ciebie wiadomosc email, w ktorej znajdziesz nowy kod. Wpisz go ponizej' : 
            'We sent to you email message, in which you can find new code. Enter it below',
          inputPlaceholder: this.langService.currentLang === 'pl' ? `6-cyfrowy kod` : `6-digit code`,
          confirmButtonColor: 'green',
          showCancelButton: true,
          cancelButtonText: cancelButtonText,
          cancelButtonColor: 'red',
          showDenyButton: true,
          denyButtonText: regenerateButtonText, 
          denyButtonColor: '#007bff',
          background: '#141414',
          color: 'white',
          confirmButtonText: 'Ok',
          inputValidator: (value) => {
            if (!value) {
              return this.langService.currentLang === 'pl' ? 
                'Musisz wprowadzic kod!' : 
                'You need to enter the code!';
            }
            
            if (value.length !== 6) {
              return this.langService.currentLang === 'pl' ? 
                'Kod musi miec dokladnie 6 cyfr!' : 
                'Code must be exactly 6 digits!';
            }
            
            if (!/^\d+$/.test(value)) {
              return this.langService.currentLang === 'pl' ? 
                'Kod moze zawierac tylko cyfry!' : 
                'Code can only contain digits!';
            }
            
            return null;
          },
          customClass: {
            validationMessage: 'custom-validation-message'
          },
        });

        if (otp.isConfirmed && otp.value) {
          this.verifySubscription({
            otp: Number(otp.value),
            email: request.email
          });
        } else if (otp.isDenied) {
          this.regenerateOtp({
            email: request.email
          });
        }
      },
      error: async (error) => {
        const errorMessage = error.errorMessages?.message || 
                             (this.langService.currentLang === 'pl' ? 
                             'Wystapil blad. Sprawdz kod i sprobuj ponownie.' : 
                             'A validation error occurred. Check code and try again.');
        const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';
        const regenerateButtonText = this.langService.currentLang === 'pl' ? 'Nowy kod' : 'New code';

        const otp = await Swal.fire({
          allowOutsideClick: false,
          title: this.langService.currentLang === 'pl' ? 
            'Sprobuj ponownie!' : 
            'Try again!',
          input: 'text',
          text: errorMessage,
          inputPlaceholder: this.langService.currentLang === 'pl' ? 
            '6-cyfrowy kod' : 
            '6-digit code',
          confirmButtonColor: 'green',
          showCancelButton: true,
          cancelButtonColor: 'red',
          cancelButtonText: cancelButtonText,
          showDenyButton: true,
          denyButtonText: regenerateButtonText, 
          denyButtonColor: '#007bff',
          background: '#141414',
          color: 'white',
          confirmButtonText: 'Ok',
          inputValidator: (value) => {
            if (!value) {
              return this.langService.currentLang === 'pl' ? 
                'Musisz wprowadzic kod!' : 
                'You need to enter the code!';
            }
            if (value.length !== 6) {
              return this.langService.currentLang === 'pl' ? 
                'Kod musi miec dokladnie 6 cyfr!' : 
                'Code must be exactly 6 digits!';
            }
            if (!/^\d+$/.test(value)) {
              return this.langService.currentLang === 'pl' ? 
                'Kod moze zawierac tylko cyfry!' : 
                'Code can only contain digits!';
            }
            return null;
          },
          customClass: {
            validationMessage: 'custom-validation-message'
          },
        });

        if (otp.isConfirmed && otp.value) {
          this.verifySubscription({
            otp: otp.value,
            email: request.email
          });
        } else if (otp.isDenied) {
          this.regenerateOtp({
            email: request.email
          });
        }
      }
    });
  }

  getTranslatedMealName(mealName: string): string {
    let mealNameTranslated = this.translate.instant('menu.meals.' + mealName);

    if (mealNameTranslated === 'menu.meals.' + mealName) {
      mealNameTranslated = mealName;
    }
    
    return mealNameTranslated;
  }

  getTranslatedBeverageName(beverageName: string): string {
    let beverageNameTranslated = this.translate.instant('menu.beverages.' + beverageName);

    if (beverageNameTranslated === 'menu.addons.' + beverageName) {
      beverageNameTranslated = beverageName;
    }
    
    return beverageNameTranslated;
  }

  getTranslatedAddonName(addonName: string): string {
    let addonNameTranslated = this.translate.instant('menu.addons.' + addonName);

    if (addonNameTranslated === 'menu.addons.' + addonName) {
      addonNameTranslated = addonName;
    }
    
    return addonNameTranslated;
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
