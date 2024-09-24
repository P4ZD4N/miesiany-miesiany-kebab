import { Component, OnInit } from '@angular/core';
import { MenuService } from '../../../services/menu/menu.service';
import { CommonModule } from '@angular/common';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LangService } from '../../../services/lang/lang.service';
import { Subscription } from 'rxjs';
import Swal from 'sweetalert2';
import { NewBeverageRequest, RemovedBeverageRequest } from '../../../requests/requests';
import { BeverageResponse, AddonResponse, MealResponse } from '../../../responses/responses';

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
  beverageForms: { [key: string]: FormGroup } = {};
  errorMessages: { [key: string]: string } = {};
  languageChangeSubscription: Subscription;
  isAdding = false;
  isEditing = false;
  newBeverage: NewBeverageRequest = {
    name: '',
    capacity: 0,
    price: 0,
  };
  sizeOrder = ['SMALL', 'MEDIUM', 'LARGE', 'XL'];

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
      },
      (error) => {
        console.log('Error loading meals', error);
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

  saveBeverageRow(beverage: BeverageResponse): void {
    const formGroup = this.beverageForms[beverage.name];
    const newCapacity = formGroup.get('capacity')!.value;
    const newPrice = formGroup.get('price')!.value;

    const updatedBeverage = {
      name: beverage.name,
      old_capacity: beverage.capacity,
      new_capacity: newCapacity,
      price: newPrice,
    };

    this.menuService.updateBeverage(updatedBeverage).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? `Pomyslnie zaktualizowano napoj '${updatedBeverage.name}'!` : `Successfully updated beverage '${updatedBeverage.name}'!`,
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

  removeBeverage(beverage: BeverageResponse): void {
    let beverageNameTranslated = this.translate.instant('menu.beverages.' + beverage.name);

    if (!this.isBeverageTranslationAvailable(beverageNameTranslated)) {
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
            text: this.langService.currentLang === 'pl' ? `Pomyslnie usunieto napoj '${beverage.name}'!` : `Successfully removed beverage '${beverage.name}'!`,
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

  showAddTable(): void {
    if (this.isEditing) {
      return;
    }
    this.hideErrorMessages();
    this.isAdding = true;
  }

  hideAddTable(): void {
    this.hideErrorMessages();
    this.isAdding = false;
  }

  hideEditableRow(beverage: BeverageResponse): void {
    beverage.isEditing = false;
    this.isEditing = false;
    this.hideErrorMessages();
  }

  addBeverage(): void {
    this.menuService.addBeverage(this.newBeverage).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? `Pomyslnie dodano napoj '${this.newBeverage.name}'!` : `Successfully added beverage '${this.newBeverage.name}'!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
        });

        this.loadBeverages();
        this.resetNewBeverage();
        this.hideAddTable();
        this.hideErrorMessages();
      },
      error: (error) => {
        this.handleError(error);
      },
    });
  }

  resetNewBeverage(): void {
    this.newBeverage = { name: '', capacity: 0, price: 0 };
  }

  handleError(error: any) {
    if (error.errorMessages) {
      this.errorMessages = error.errorMessages;
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

  sortSizes = (a: any, b: any): number => {
    return this.sizeOrder.indexOf(a.key) - this.sizeOrder.indexOf(b.key);
  };

  isManager(): boolean {
    return this.authenticationService.isManager();
  }

  isBeverageTranslationAvailable(beverageName: string): boolean {
    const translatedName = this.translate.instant('menu.beverages.' + beverageName);
    return translatedName !== 'menu.beverages.' + beverageName;
  }
}