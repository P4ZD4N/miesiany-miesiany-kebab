import { Component, OnInit } from '@angular/core';
import { MenuService } from '../../../services/menu/menu.service';
import { CommonModule } from '@angular/common';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { LangService } from '../../../services/lang/lang.service';
import { Subscription } from 'rxjs';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [CommonModule, TranslateModule, ReactiveFormsModule],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent implements OnInit {
  beverages: any[] = [];
  addons: any[] = [];
  meals: any[] = [];
  beverageForms: { [key: string]: FormGroup } = {};
  beverageFormErrorMessage: string | null = null;
  languageChangeSubscription: Subscription;

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
    })
  }

  ngOnInit(): void {
    this.loadBeverages();
    this.loadAddons();
    this.loadMeals();
  }

  loadBeverages(): void {
    this.menuService.getBeverages().subscribe(
      (data: any[]) => {
          this.beverages = data;
          this.initializeBeverageForms(data);
      },
      (error) => {
        console.log('Error loading beverages', error);
      }
    )
  }

  loadAddons(): void {
    this.menuService.getAddons().subscribe(
      (data: any[]) => {
          this.addons = data;
      },
      (error) => {
        console.log('Error loading addons', error);
      }
    )
  }

  loadMeals(): void {
    this.menuService.getMeals().subscribe(
      (data: any[]) => {
        this.meals = data;
      },
      (error) => {
        console.log('Error loading meals', error);
      }
    )
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

  initializeBeverageForms(beverages: any[]): void {
    beverages.forEach((beverage) => {
      this.beverageForms[beverage.name] = this.formBuilder.group({
        capacity: new FormControl(beverage.capacity),
        price: new FormControl(beverage.price),
      });
    });
  }

  editBeverageRow(beverage: any): void {
    beverage.isEditing = true;
    const form = this.beverageForms[beverage.name];
    form.patchValue({
      capacity: beverage.capacity,
      price: beverage.price,
    });
  }

  saveBeverageRow(beverage: any): void {
    const formGroup = this.beverageForms[beverage.name];
    const newCapacity = formGroup.get('capacity')!.value;
    const newPrice = formGroup.get('price')!.value;

    if (newPrice < 0) {
      if (this.langService.currentLang === 'pl') {
        this.beverageFormErrorMessage = 'Cena musi byc wieksza od 0!';
      } else if (this.langService.currentLang === 'en') {
        this.beverageFormErrorMessage = 'Price must be positive!';
      }
      return;
    }

    if (newCapacity < 0) {
      if (this.langService.currentLang === 'pl') {
        this.beverageFormErrorMessage = 'Pojemnosc musi byc wieksza od 0!';
      } else if (this.langService.currentLang === 'en') {
        this.beverageFormErrorMessage = 'Capacity must be positive!';
      }
      return;
    }

    const updatedBeverage = {
      name: beverage.name,
      capacity: newCapacity,
      price: newPrice
    };

    this.menuService.updateBeverage(updatedBeverage).subscribe(
      response => {
        if (response) {
          this.beverageFormErrorMessage = null;
          beverage.isEditing = false;
          this.loadBeverages(); 
        }
      }
    );
  }

  hideErrorMessages(): void {
    this.beverageFormErrorMessage = null;
  }

  removeBeverage(beverage: any): void {

    const beverageNameTranslated = this.translate.instant('menu.beverages.' + beverage.name);

    const confirmationMessage = this.langService.currentLang === 'pl' 
    ? `Czy na pewno chcesz usunac napoj ${beverageNameTranslated}?` 
    : `Are you sure you want to remove beverage ${beverageNameTranslated}?`;

    Swal.fire({
      title: this.langService.currentLang === 'pl' ? 'Potwierdzenie' : 'Confirmation',
      text: confirmationMessage,
      icon: 'warning',
      iconColor: "red",
      showCancelButton: true,
      confirmButtonColor: '#0077ff',
      cancelButtonColor: 'red',
      background: 'black',
      color: 'white',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Tak' : 'Yes',
      cancelButtonText: this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel',
    }).then((result) => {
      if (result.isConfirmed) {
        this.menuService.removeBeverage(beverage).subscribe(() => {
          this.loadBeverages();
        });
      }
    });
  }
}
