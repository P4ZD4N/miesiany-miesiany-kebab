import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { TrackOrderRequest } from '../../../requests/requests';
import { OrderService } from '../../../services/order/order.service';
import { OrdersService } from '../../../services/orders/orders.service';
import { OrderResponse } from '../../../responses/responses';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { LangService } from '../../../services/lang/lang.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-track-order',
  standalone: true,
  imports: [TranslateModule, CommonModule, FormsModule],
  templateUrl: './track-order.component.html',
  styleUrl: './track-order.component.scss'
})
export class TrackOrderComponent implements OnInit{

  storageKey = 'trackOrderData';
  objectKeys = Object.keys;
  errorMessages: { [key: string]: string } = {};
  discountPercentage: number | null = null;
  trackOrderRequest: TrackOrderRequest = {
    id: null,
    customer_phone: ''
  };
  order: OrderResponse = {
    id: 0,
    order_type: null, 
    order_status: null,
    customer_phone: '',
    customer_email: '',
    street: '',
    house_number: 0,
    postal_code: '',
    city: '',
    additional_comments: '',
    total_price: 0,
    created_at: '',
    meals: [
      {
        meal_name: '',
        final_price: 0,
        size: null,
        quantity: 0,
        ingredient_names: []
      }
    ],
    beverages: [
      {
        beverage_name: '',
        final_price: 0,
        capacity: 0,
        quantity: 0
      }
    ],
    addons: [
      {
        addon_name: '',
        final_price: 0,
        quantity: 0
      }
    ]
  };
  languageChangeSubscription: Subscription;

  constructor(
    private orderService: OrderService, 
    private ordersService: OrdersService,
    private langService: LangService,
    private translate: TranslateService
  ) {
    this.languageChangeSubscription = this.langService.languageChanged$.subscribe(() => {
      this.errorMessages = {};
    });
  }

  ngOnInit(): void {
    const saved = this.getTrackOrderData();
    const trackOrderData = this.orderService.trackOrderData;
  
    const id = saved?.id ?? trackOrderData.orderId;
    const phone = saved?.customer_phone ?? trackOrderData.customerPhone;

    if (this.orderService.trackOrderData.discountPercentage != 0) {
      this.discountPercentage = this.orderService.trackOrderData.discountPercentage;
    }

    if (id && phone) {
      this.trackOrderRequest = { id, customer_phone: phone};
      this.setTrackOrderData(this.trackOrderRequest);
      this.startTrackingOrder();
    }
  }

  startTrackingOrder(): void {
    this.ordersService.trackOrder(this.trackOrderRequest).subscribe({
      next: (response) => {
        this.initOrder(response);
        this.setTrackOrderData(this.trackOrderRequest);
      },
      error: (error) => {
        this.handleError(error);
      },
    }); 
  }

  stopTrackingOrder(): void {

    Swal.fire({
      text: this.langService.currentLang === 'pl' 
        ? `Pomyslnie zaprzestano sledzic zamowienie z id '${this.trackOrderRequest.id}'!` 
        : `Successfully stopped tracking order with id '${this.trackOrderRequest.id}'!`,
      icon: 'success',
      iconColor: 'green',
      confirmButtonColor: 'green',
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
    });

    this.clearTrackOrderData();

    this.trackOrderRequest = {
      id: null,
      customer_phone: ''
    };

    this.order = {
      id: 0,
      order_type: null, 
      order_status: null,
      customer_phone: '',
      customer_email: '',
      street: '',
      house_number: 0,
      postal_code: '',
      city: '',
      additional_comments: '',
      total_price: 0,
      created_at: '',
      meals: [
        {
          meal_name: '',
          final_price: 0,
          size: null,
          quantity: 0,
          ingredient_names: []
        }
      ],
      beverages: [
        {
          beverage_name: '',
          final_price: 0,
          capacity: 0,
          quantity: 0
        }
      ],
      addons: [
        {
          addon_name: '',
          final_price: 0,
          quantity: 0
        }
      ]
    };
  }

  initOrder(response: OrderResponse): void {
    this.order = {
      id: response.id,
      order_type: response.order_type,
      order_status: response.order_status,
      customer_phone: response.customer_phone,
      customer_email: response.customer_email,
      street: response.street,
      house_number: response.house_number,
      postal_code: response.postal_code,
      city: response.city,
      additional_comments: response.additional_comments,
      total_price: response.total_price,
      created_at: response.created_at,
      meals: response.meals,
      beverages: response.beverages,
      addons: response.addons
    }
  }

  setTrackOrderData(request: TrackOrderRequest): void {
    sessionStorage.setItem(this.storageKey, JSON.stringify(request));
  }

  getTrackOrderData(): TrackOrderRequest | null {
    const data = sessionStorage.getItem(this.storageKey);
    return data ? JSON.parse(data) : null;
  }

  clearTrackOrderData(): void {
    sessionStorage.removeItem(this.storageKey);
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

  getTranslatedIngredientName(ingredientName: string): string {
    let ingredientNameTranslated = this.translate.instant('menu.meals.ingredients.' + ingredientName);

    if (ingredientNameTranslated === 'menu.meals.ingredients.' + ingredientName) {
      ingredientNameTranslated = ingredientName;
    }
    
    return ingredientNameTranslated;
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
