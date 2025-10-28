import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { TrackOrderRequest } from '../../../requests/requests';
import { OrderService } from '../../../services/order/order.service';
import { OrdersService } from '../../../services/orders/orders.service';
import { OrderResponse } from '../../../responses/responses';
import { FormsModule } from '@angular/forms';
import { LangService } from '../../../services/lang/lang.service';
import { Subscription } from 'rxjs';
import { TranslationHelperService } from '../../../services/translation-helper/translation-helper.service';
import { AlertService } from '../../../services/alert/alert.service';
import { WebSocketService } from '../../../services/websocket/websocket.service';

@Component({
  selector: 'app-track-order',
  standalone: true,
  imports: [TranslateModule, CommonModule, FormsModule],
  templateUrl: './track-order.component.html',
  styleUrl: './track-order.component.scss',
})
export class TrackOrderComponent implements OnInit {
  errorMessages: { [key: string]: string } = {};
  trackOrderRequest: TrackOrderRequest = {
    id: null,
    customer_phone: '',
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
        ingredient_names: [],
      },
    ],
    beverages: [
      {
        beverage_name: '',
        final_price: 0,
        capacity: 0,
        quantity: 0,
      },
    ],
    addons: [
      {
        addon_name: '',
        final_price: 0,
        quantity: 0,
      },
    ],
  };
  discountPercentage: number | null = null;
  storageKey: string = 'trackOrderData';
  languageChangeSubscription: Subscription;

  constructor(
    private orderService: OrderService,
    private ordersService: OrdersService,
    private langService: LangService,
    private alertService: AlertService,
    private translationHelper: TranslationHelperService,
    private webSocketService: WebSocketService
  ) {
    this.languageChangeSubscription =
      this.langService.languageChanged$.subscribe(() => {
        this.errorMessages = {};
      });
  }

  ngOnInit(): void {
    const saved = this.getTrackOrderData();
    const trackOrderData = this.orderService.trackOrderData;
    const id = saved?.id ?? trackOrderData.orderId;
    const phone = saved?.customer_phone ?? trackOrderData.customerPhone;
    const discountPercentage =
      this.orderService.trackOrderData.discountPercentage;

    if (discountPercentage) this.discountPercentage = discountPercentage;
    if (id && phone) {
      this.trackOrderRequest = { id, customer_phone: phone };
      this.putTrackOrderDataInLocalStorage(this.trackOrderRequest);
      this.startTrackingOrder();
    }
  }

  private subscribeToRealTimeUpdates(): void {
    this.subscribeToOrderUpdates();
    this.subscribeToOrderRemove();
  }

  private subscribeToOrderUpdates(): void {
    this.webSocketService.onOrderStatusUpdate().subscribe({
      next: (updatedOrder: OrderResponse) => {
        if (updatedOrder.id === this.order.id) {
          this.initOrder(updatedOrder);
        }
      },
      error: (error) => console.error('WebSocket update error', error),
    });
  }

  private subscribeToOrderRemove(): void {
    this.webSocketService.onOrderRemoved().subscribe({
      next: (removedOrderId: number) => {
        if (removedOrderId === this.order.id) {
          this.removeTrackOrderDataFromLocalStorage();
          this.resetTrackedOrderData();
        }
      },
      error: (error) => console.error('WebSocket removal error', error),
    });
  }

  protected startTrackingOrder(): void {
    this.ordersService.trackOrder(this.trackOrderRequest).subscribe({
      next: (response) => {
        this.initOrder(response);
        this.putTrackOrderDataInLocalStorage(this.trackOrderRequest);
        this.subscribeToRealTimeUpdates();
      },
      error: (error) => this.handleError(error),
    });
  }

  private initOrder(response: OrderResponse): void {
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
      addons: response.addons,
    };
  }

  protected stopTrackingOrder(): void {
    this.alertService.showStopTrackingOrderAlert(this.trackOrderRequest.id);
    this.removeTrackOrderDataFromLocalStorage();
    this.resetTrackedOrderData();
  }

  private resetTrackedOrderData(): void {
    this.trackOrderRequest = {
      id: null,
      customer_phone: '',
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
          ingredient_names: [],
        },
      ],
      beverages: [
        {
          beverage_name: '',
          final_price: 0,
          capacity: 0,
          quantity: 0,
        },
      ],
      addons: [
        {
          addon_name: '',
          final_price: 0,
          quantity: 0,
        },
      ],
    };
  }

  private removeTrackOrderDataFromLocalStorage(): void {
    localStorage.removeItem(this.storageKey);
  }

  private putTrackOrderDataInLocalStorage(request: TrackOrderRequest): void {
    localStorage.setItem(this.storageKey, JSON.stringify(request));
  }

  private getTrackOrderData(): TrackOrderRequest | null {
    const data = localStorage.getItem(this.storageKey);
    return data ? JSON.parse(data) : null;
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

  protected getTranslatedIngredientName(ingredientName: string): string {
    return this.translationHelper.getTranslatedIngredientName(ingredientName);
  }

  private handleError(error: any): void {
    error.errorMessages
      ? (this.errorMessages = error.errorMessages)
      : (this.errorMessages = { general: 'An unexpected error occurred' });
  }
}
